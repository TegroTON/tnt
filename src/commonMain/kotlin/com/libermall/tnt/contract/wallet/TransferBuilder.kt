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

import com.libermall.tnt.contract.op.Op
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb

class TransferBuilder {
    lateinit var dest: MsgAddressInt
    lateinit var amount: Coins
    var mode: Int = 3
    var bounce: Boolean = true
    var stateInit: StateInit? = null
    private var payload: Cell = Cell.of()

    fun payload(value: Cell) {
        payload = value
    }

    fun payload(value: Op) {
        payload = value.toCell()
    }

    fun build() = CellBuilder.createCell {
        storeTlb(
            MessageRelaxed.tlbCodec(AnyTlbConstructor), MessageRelaxed(
                info = CommonMsgInfoRelaxed.IntMsgInfoRelaxed(
                    ihr_disabled = true,
                    bounce = bounce,
                    bounced = false,
                    src = AddrNone,
                    dest = dest,
                    value = CurrencyCollection(
                        coins = amount
                    )
                ),
                init = stateInit,
                body = payload,
            )
        )
    }
}
