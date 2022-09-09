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

package com.libermall.tnt.contract.wallet

import com.libermall.tnt.sendMessage
import kotlinx.coroutines.delay
import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.Coins
import org.ton.block.StateInit
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.crypto.hex
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.storeTlb

data class WalletV3R2(
    private val private_key: PrivateKeyEd25519 = PrivateKeyEd25519.generate(),
    val workchain_id: Int = 0,
    val wallet_id: UInt = (698983191 + workchain_id).toUInt(),
) : KoinComponent {
    init {
        logger.warn { "For recovery purposes, single-use wallet's private key is ${base64(private_key.key)}" }
        logger.warn { "Never share this private key with anyone!" }
    }

    private val liteClient by inject<LiteClient>()

    private val data = CellBuilder.createCell {
        storeUInt32(0u) // seqno
        storeUInt32(wallet_id)
        storeBytes(private_key.publicKey().key)
    }

    val stateInit = StateInit(CODE, data)

    val address =
        AddrStd(workchain_id, CellBuilder.createCell { storeTlb(StateInit, stateInit) }.hash())

    suspend fun createSignedMessage(builder: suspend CellBuilder.() -> Unit): Cell {
        val data = CellBuilder.beginCell().apply { builder() }.endCell()
        return CellBuilder.createCell {
            storeBytes(private_key.sign(data.hash()))
            storeBits(data.bits)
            storeRefs(data.refs)
        }
    }

    suspend fun bundleTransfer(builder: suspend BundleTransferBuilder.() -> Unit) = BundleTransferBuilder(
        this
    ).apply { builder() }.build().sendMessage(liteClient)

    suspend fun balance(): Coins? = balance(address, liteClient)

    suspend fun awaitBalance(): Coins {
        var balance: Coins
        do {
            balance = balance() ?: Coins.ofNano(0)
            delay(2_000L)
        } while (balance.amount.value <= BigInt.ZERO)
        return balance
    }

    suspend fun seqno(): UInt = seqno(address, liteClient)

    companion object : KLogging() {
        val CODE =
            BagOfCells(hex("B5EE9C724101010100710000DEFF0020DD2082014C97BA218201339CBAB19F71B0ED44D0D31FD31F31D70BFFE304E0A4F2608308D71820D31FD31FD31FF82313BBF263ED44D0D31FD31FD3FFD15132BAF2A15144BAF2A204F901541055F910F2A3F8009320D74A96D307D402FB00E8D101A4C8CB1FCB1FCBFFC9ED5410BD6DAD")).roots.first()

        @JvmStatic
        suspend fun balance(
            address: AddrStd,
            liteClient: LiteClient,
        ): Coins? =
            liteClient.getAccount(
                LiteServerAccountId(address),
                liteClient.getLastBlockId()
            )?.storage?.balance?.coins

        @JvmStatic
        suspend fun seqno(
            address: AddrStd,
            liteClient: LiteClient
        ): UInt =
            liteClient.runSmcMethod(
                LiteServerAccountId(address),
                liteClient.getLastBlockId(),
                "seqno"
            )
                .toMutableVmStack()
                .popNumber()
                .toBigInt()
                .toString() // TODO
                .toUInt()
    }
}
