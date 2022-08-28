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
import com.github.ajalt.mordant.terminal.Terminal
import com.libermall.tnt.contract.nft.CollectionContract
import com.libermall.tnt.contract.op.ChangeOwnerOp
import com.libermall.tnt.contract.op.DeployItemOp
import com.libermall.tnt.contract.op.TransferOp
import com.libermall.tnt.contract.wallet.WalletV3R2
import com.libermall.tnt.model.CollectionModel
import com.libermall.tnt.model.MintableModel
import com.libermall.tnt.model.SpecModel
import com.libermall.tnt.model.StandaloneItemModel
import com.libermall.tnt.toSafeBounceable
import com.libermall.tnt.toSafeUnbounceable
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.lite.client.LiteClient
import org.ton.tlb.storeTlb
import java.io.File

class MintCommand : CliktCommand(
    name = "mint",
    help = """
        Mint a collection
    """.trimIndent()
), KoinComponent {
    val liteClient by inject<LiteClient>()
    val wallet by inject<WalletV3R2>()
    val terminal by inject<Terminal>()

    @OptIn(ExperimentalSerializationApi::class)
    val spec by argument().convert {
        try {
            Json.decodeFromStream<SpecModel>(File(it).inputStream())
        } catch (e: Exception) {
            logger.warn(e, {})
            throw e
        }
    }

    override fun run(): Unit = runBlocking {
        terminal.println("For mint, a single-use wallet contract will be deployed.")
        terminal.println("To proceed, send sufficient amount of coins to ${wallet.address.toSafeUnbounceable()}")
        terminal.println("Awaiting balance...")

        withTimeout(600_000L) {
            val balance = wallet.awaitBalance()
            terminal.println("Got balance of $balance TON")
        }

        delay(5_000L)

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
        val stub = collection.copy(owner = wallet.address) // transfer ownership later

        terminal.println("Deploying the collection ${stub.address().toSafeBounceable()}...")

        wallet.bundleTransfer {
            transfer {
                dest = stub.address()
                amount = Coins.ofNano(spec.collection_deploy_amount + spec.blockchain_fee)
                bounce = false
                stateInit = stub.stateInit()
            }
        }

        delay(10_000L)

        terminal.println("Deploying items...")
        stub.items
            .mapIndexed { index, model ->
                DeployItemOp(
                    0uL,
                    index.toULong(),
                    Coins.ofNano(spec.item_deploy_amount),
                    CellBuilder.createCell {
                        storeTlb(MsgAddress, wallet.address) // Transfer ownership later
                        storeRef(model.content.asCell())
                    }
                )
            }
            .chunked(4)
            .forEach {
                terminal.println("Deploying items no. ${it.minOf { it.index }} through ${it.maxOf { it.index }}")

                wallet.bundleTransfer {
                    for (deployment in it) {
                        transfer {
                            dest = stub.address()
                            amount = Coins.ofNano(spec.item_deploy_amount + spec.blockchain_fee)
                            payload(deployment)
                        }
                    }
                }
                delay(15_000)
            }

        terminal.println("Transferring ownership of the collection to ${collection.owner.toSafeBounceable() ?: "none"}")

        wallet.bundleTransfer {
            transfer {
                dest = stub.address()
                amount = Coins.ofNano(spec.blockchain_fee)
                payload(ChangeOwnerOp(query_id = 0uL, new_owner = collection.owner))
            }
        }

        delay(10_000)
        terminal.println("Transferring ownership of all items to their respective owners")
        (0uL until CollectionContract.of(stub.address(), liteClient).next_item_index)
            .map { index ->
                index to CollectionContract.itemAddressOf(
                    stub.address(),
                    index,
                    liteClient
                ) as MsgAddressInt
            }
            .chunked(4)
            .forEach {
                terminal.println("Transferring items no. ${it.minOf { it.first }} through ${it.maxOf { it.first }}")

                wallet.bundleTransfer {
                    for (item in it) {
                        transfer {
                            dest = item.second
                            amount = Coins.ofNano(spec.item_forward_amount + spec.blockchain_fee)
                            payload(
                                TransferOp(
                                    query_id = 0uL,
                                    new_owner = collection.items[item.first.toInt()].owner,
                                    response_destination = collection.owner,
                                    custom_payload = Maybe.of(null),
                                    forward_amount = VarUInteger(spec.item_forward_amount),
                                    forward_payload = Either.of(Cell.of(), null),
                                )
                            )
                        }
                    }
                }

                delay(15_000)
            }
    }

    private suspend fun mint(item: StandaloneItemModel) {
        // Mint with temporary wallet as the owner, transfer ownership in the same transaction
        val stub = item.copy(owner = wallet.address)

        terminal.println("Deploying standalone item as ${stub.address().toSafeBounceable()}...")
        wallet.bundleTransfer {
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
    }

    companion object : KLogging()
}
