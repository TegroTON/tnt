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

import org.ton.block.Coins
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class DeployItemOp(
    val query_id: ULong,
    val index: ULong,
    val amount: Coins,
    val content: Cell,
) : CollectionOp {
    companion object : TlbCodec<DeployItemOp> by DeployItemOpConstructor {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<DeployItemOp> = DeployItemOpConstructor
    }
}

private object DeployItemOpConstructor : TlbConstructor<DeployItemOp>(
    schema = "deploy#00000001 query_id:uint64 index:uint64 amount:Coins content:^Cell = InternalMsgBody;"
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: DeployItemOp) {
        cellBuilder.apply {
            storeUInt64(value.query_id)
            storeUInt64(value.index)
            storeTlb(Coins, value.amount)
            storeRef(value.content)
        }
    }

    override fun loadTlb(cellSlice: CellSlice): DeployItemOp = cellSlice.run {
        DeployItemOp(
            query_id = loadUInt64(),
            index = loadUInt64(),
            amount = loadTlb(Coins),
            content = loadRef(),
        )
    }
}
