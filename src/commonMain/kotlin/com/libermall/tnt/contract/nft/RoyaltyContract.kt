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
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class RoyaltyContract(
    val numerator: Int,
    val denominator: Int,
    val destination: MsgAddress,
) {
    fun value() = numerator.toDouble() / denominator

    companion object : KLogging() {
        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): RoyaltyContract =
            liteClient
                .also { logger.debug { "running smc method `royalty_params` on ${address.toRaw()}" } }
                .runSmcMethod(
                    LiteServerAccountId(address),
                    referenceBlock
                        ?.also { logger.debug { "using supplied reference block $it" } }
                        ?: liteClient.getLastBlockId()
                            .also { logger.debug { "using latest masterchain block $it as reference" } },
                    "royalty_params"
                )
                .also { logger.debug { "smc method result: $it" } }
                .toMutableVmStack().let {
                    RoyaltyContract(
                        numerator = it.popNumber().toInt(),
                        denominator = it.popNumber().toInt(),
                        destination = it.popSlice().loadTlb(MsgAddress),
                    )
                }
                .also {
                    logger.debug { "parsed smc method result: $it" }
                }
    }
}
