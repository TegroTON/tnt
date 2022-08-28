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

package com.libermall.tnt.contract.op

import org.ton.cell.CellBuilder
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor
import org.ton.tlb.storeTlb

sealed interface ItemOp : Op {
    override fun toCell() = CellBuilder.createCell { storeTlb(ItemOp, this@ItemOp) }

    companion object : TlbCodec<ItemOp> by ItemOpCombinator
}

private object ItemOpCombinator : TlbCombinator<ItemOp>() {
    override val constructors: List<TlbConstructor<out ItemOp>> =
        listOf(TransferOp.tlbCodec())

    override fun getConstructor(value: ItemOp): TlbConstructor<out ItemOp> = when (value) {
        is TransferOp -> TransferOp.tlbCodec()
    }
}
