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

package com.libermall.tnt.contract.nft

import com.libermall.tnt.toRaw
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class CollectionContract(
    val next_item_index: ULong,
    val content: Cell,
    val owner: MsgAddress,
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): CollectionContract =
            liteClient
                .also { logger.debug { "running smc method `get_collection_data` on ${address.toRaw()}" } }
                .runSmcMethod(
                    LiteServerAccountId(address),
                    referenceBlock
                        ?.also { logger.debug { "using supplied reference block $it" } }
                        ?: liteClient.getLastBlockId()
                            .also { logger.debug { "using latest masterchain block $it as reference" } },
                    "get_collection_data"
                )
                .also { logger.debug { "smc method result: $it" } }
                .toMutableVmStack()
                .let {
                    CollectionContract(
                        next_item_index = it.popNumber().toBigInt().toString().toULong(), // TODO
                        content = it.popCell(),
                        owner = it.popSlice().loadTlb(MsgAddress),
                    )
                }
                .also {
                    logger.debug { "parsed smc method result: $it" }
                }

        @JvmStatic
        suspend fun itemAddressOf(
            collection: AddrStd,
            index: ULong,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): MsgAddress =
            liteClient
                .also { logger.debug { "running smc method `get_nft_address_by_index` on ${collection.toRaw()} for index=$index" } }
                .runSmcMethod(
                    LiteServerAccountId(collection),
                    referenceBlock
                        ?.also { logger.debug { "using supplied reference block $it" } }
                        ?: liteClient.getLastBlockId()
                            .also { logger.debug { "using latest masterchain block $it as reference" } },
                    "get_nft_address_by_index",
                    VmStackValue(BigInt(index.toString())) // TODO
                )
                .also { logger.debug { "smc method result: $it" } }
                .toMutableVmStack()
                .popSlice()
                .loadTlb(MsgAddress)
                .also {
                    logger.debug { "parsed smc method result: $it" }
                }

        @JvmStatic
        suspend fun itemContent(
            collection: AddrStd,
            index: ULong,
            individualContent: Cell,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): Cell =
            liteClient
                .also {
                    logger.debug { "running smc method `get_nft_content` on ${collection.toRaw()} for index=$index with individualContent=$individualContent" }
                }
                .runSmcMethod(
                    LiteServerAccountId(collection),
                    referenceBlock
                        ?.also { logger.debug { "using supplied reference block $it" } }
                        ?: liteClient.getLastBlockId()
                            .also { logger.debug { "using latest masterchain block $it as reference" } },
                    "get_nft_content",
                    VmStackValue(BigInt(index.toString())), // TODO
                    VmStackValue(individualContent)
                )
                .also { logger.debug { "smc method result: $it" } }
                .toMutableVmStack()
                .popCell()
                .also {
                    logger.debug { "parsed smc method result: $it" }
                }
    }
}
