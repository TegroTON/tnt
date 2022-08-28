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

import kotlinx.datetime.Clock
import mu.KLogging
import org.ton.api.exception.TvmException
import org.ton.bitstring.BitString
import org.ton.block.ExtInMsgInfo
import org.ton.block.Message

class BundleTransferBuilder(
    private val wallet: WalletV3R2
) {
    private val transfers = mutableListOf<TransferBuilder>()

    var seqno: UInt? = null
    var timeout: UInt = (Clock.System.now().toEpochMilliseconds() / 1000).toUInt() + 60u

    suspend fun transfer(builder: suspend TransferBuilder.() -> Unit) {
        transfers.add(TransferBuilder().apply { builder() })
        require(transfers.size <= 4)
    }

    suspend fun build() = Message(
        info = ExtInMsgInfo(dest = wallet.address),
        init = wallet.stateInit,
        body = wallet.createSignedMessage {
            storeUInt32(wallet.wallet_id)

            val actualSeqno = try {
                seqno ?: wallet.seqno()
            } catch (e: TvmException) {
                logger.debug("failed to get seqno, assuming = 0", e)
                0u
            }

            if (actualSeqno == 0u) {
                storeBits(BitString("FFFFFFFF"))
            } else {
                storeUInt32(timeout)
            }

            storeUInt32(actualSeqno)

            transfers
                .forEach {
                    storeUInt(it.mode, 8)
                    storeRef(it.build())
                }
        }
    )

    companion object : KLogging()
}

