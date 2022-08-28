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

import org.ton.block.MsgAddress
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class ChangeOwnerOp(
    val query_id: ULong,
    val new_owner: MsgAddress
) : CollectionOp {
    companion object : TlbCodec<ChangeOwnerOp> by ChangeOwnerOpConstructor {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<ChangeOwnerOp> = ChangeOwnerOpConstructor
    }
}

private object ChangeOwnerOpConstructor : TlbConstructor<ChangeOwnerOp>(
    schema = "change_owner#00000003 query_id:uint64 owner:MsgAddress = InternalMsgBody;"
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: ChangeOwnerOp) {
        cellBuilder.apply {
            storeUInt64(value.query_id)
            storeTlb(MsgAddress, value.new_owner)
        }
    }

    override fun loadTlb(cellSlice: CellSlice): ChangeOwnerOp = cellSlice.run {
        ChangeOwnerOp(
            query_id = loadUInt64(),
            new_owner = loadTlb(MsgAddress),
        )
    }
}
