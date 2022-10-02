/*
 * (T)ON (N)FT (T)ool - all-in-one utility to work with NFTs on The Open Network
 * Copyright (c) 2022
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.libermall.tnt.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.libermall.tnt.contract.op.ChangeOwnerOp
import com.libermall.tnt.contract.op.DeployItemOp
import com.libermall.tnt.contract.op.TransferOp
import com.libermall.tnt.contract.wallet.WalletV3R2
import com.libermall.tnt.model.CollectionModel
import com.libermall.tnt.model.MintableModel
import com.libermall.tnt.model.SpecModel
import com.libermall.tnt.model.StandaloneItemModel
import com.libermall.tnt.readFileAsString
import com.libermall.tnt.toSafeBounceable
import com.libermall.tnt.toSafeUnbounceable
import com.libermall.tnt.writeByteArrayToFile
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb

class MintCommand : CliktCommand(
    name = "mint",
    help = """
        Turn item/collection specification file into a set of deployable bag-of-cells files
    """.trimIndent()
), KoinComponent {
    var stepNo = 0
    val terminal by inject<Terminal>()

    val spec by argument(name = "SPEC", help = "Input specification file")
        .convert {
            try {
                Json.decodeFromString<SpecModel>(readFileAsString(it))
            } catch (e: Exception) {
                logger.warn(e) {}
                throw e
            }
        }

    val outputDirectory by argument(name = "OUTPUT", help = "Output directory for the result bag-of-cells files")

    override fun run(): Unit = runBlocking {
        spec.entities
            .forEach {
                mint(it)
            }
    }

    private suspend fun mint(entity: MintableModel) = when (entity) {
        is CollectionModel -> mint(entity)
        is StandaloneItemModel -> mint(entity)
    }

    private suspend fun mint(collection: CollectionModel) {
        val wallet = WalletV3R2()
        var localSeqno = 0u

        table {
            captionTop("Collection Summary")
            header {
                row("No.", "Step", "Address", "BoC File")
            }
            body {
                val collectionStub = collection.copy(owner = wallet.address) // transfer ownership later

                row(
                    ++stepNo,
                    "Deploy collection",
                    collectionStub.address().toSafeBounceable(),
                    wallet.bundleTransfer {
                        this.seqno = localSeqno++
                        this.timeout = UInt.MAX_VALUE
                        transfer {
                            dest = collectionStub.address()
                            amount = Coins.ofNano(spec.collection_deploy_amount + spec.blockchain_fee)
                            bounce = false
                            stateInit = collectionStub.stateInit()
                        }
                    }
                        .writeToFile()
                )

                collectionStub.items
                    .mapIndexed { index, model ->
                        DeployItemOp(
                            0uL,
                            index.toULong(),
                            Coins.ofNano(spec.item_deploy_amount),
                            CellBuilder.createCell {
                                storeTlb(MsgAddress, wallet.address) // Transfer ownership later
                                storeRef(model.content.asCell())
                            }
                        ) to collectionStub.itemAddress(index.toULong())
                    }
                    .chunked(4)
                    .forEach {
                        ++stepNo
                        val bocFile = wallet.bundleTransfer {
                            this.seqno = localSeqno++
                            this.timeout = UInt.MAX_VALUE
                            for ((deploy, address) in it) {
                                transfer {
                                    dest = collectionStub.address()
                                    amount = Coins.ofNano(spec.item_deploy_amount + spec.blockchain_fee)
                                    payload(deploy)
                                }
                            }
                        }
                            .writeToFile()

                        for ((_, address) in it) {
                            row(
                                stepNo,
                                "Deploy item",
                                address.toSafeBounceable(),
                                bocFile,
                            )
                        }
                    }

                row(
                    ++stepNo,
                    "Transfer collection ownership",
                    collectionStub.address().toSafeBounceable(),
                    wallet.bundleTransfer {
                        this.seqno = localSeqno++
                        this.timeout = UInt.MAX_VALUE
                        transfer {
                            dest = collectionStub.address()
                            amount = Coins.ofNano(spec.blockchain_fee)
                            payload(ChangeOwnerOp(query_id = 0uL, new_owner = collection.owner))
                        }
                    }
                        .writeToFile()
                )

                collectionStub.items
                    .mapIndexed { index, model ->
                        TransferOp(
                            query_id = 0uL,
                            new_owner = model.owner,
                            response_destination = collection.owner,
                            custom_payload = Maybe.of(null),
                            forward_amount = VarUInteger(spec.item_forward_amount),
                            forward_payload = Either.of(Cell.of(), null),
                        ) to collectionStub.itemAddress(index.toULong())
                    }
                    .chunked(4)
                    .forEach {
                        ++stepNo
                        val bocFile = wallet.bundleTransfer {
                            this.seqno = localSeqno++
                            this.timeout = UInt.MAX_VALUE
                            for ((transfer, address) in it) {
                                transfer {
                                    dest = address
                                    amount = Coins.ofNano(spec.item_forward_amount + spec.blockchain_fee)
                                    payload(transfer)
                                }
                            }
                        }
                            .writeToFile()

                        for ((_, address) in it) {
                            row(
                                stepNo,
                                "Transfer item",
                                address.toSafeBounceable(),
                                bocFile,
                            )
                        }
                    }
            }
        }
            .let { terminal.println(it) }

        terminal.println("Single-use wallet address will be ${wallet.address.toSafeUnbounceable()}")
        terminal.println("Send sufficient funds tho this address and proceed with `tnt send` to deploy contracts.")
    }

    private suspend fun mint(item: StandaloneItemModel) {
        val wallet = WalletV3R2()

        table {
            captionTop("Collection Summary")
            header {
                row("No.", "Step", "Address", "BoC File")
            }
            body {
                // Mint with temporary wallet as the owner, transfer ownership in the same transaction
                val stub = item.copy(owner = wallet.address)

                row(
                    ++stepNo,
                    "Deploy  and transfer item",
                    stub.address().toSafeBounceable(),
                    wallet.bundleTransfer {
                        seqno = 0u
                        this.timeout = UInt.MAX_VALUE
                        transfer {
                            dest = stub.address()
                            amount =
                                Coins.ofNano(spec.item_deploy_amount + spec.item_forward_amount + spec.blockchain_fee)
                            bounce = false
                            stateInit = stub.stateInit()
                            payload(
                                TransferOp(
                                    new_owner = item.owner,
                                    response_destination = item.owner,
                                    custom_payload = Maybe.of(null),
                                    forward_amount = VarUInteger(spec.item_forward_amount),
                                    forward_payload = Either.of(Cell.of(), null),
                                )
                            )
                        }
                    }
                        .writeToFile()
                )
            }
        }
            .let { terminal.println(it) }

        terminal.println("Single-use wallet address will be ${wallet.address.toSafeUnbounceable()}")
        terminal.println("Send sufficient funds tho this address and proceed with `tnt send` to deploy contracts.")
    }

    private fun Message<Cell>.writeToFile() =
        writeByteArrayToFile(
            outputDirectory,
            "step_${stepNo}.boc",
            BagOfCells(CellBuilder.createCell {
                storeTlb(
                    Message.tlbCodec(AnyTlbConstructor),
                    this@writeToFile
                )
            }).toByteArray()
        )

    companion object : KLogging()
}
