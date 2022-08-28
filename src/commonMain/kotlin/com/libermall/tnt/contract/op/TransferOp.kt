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

import org.ton.block.Either
import org.ton.block.Maybe
import org.ton.block.MsgAddress
import org.ton.block.VarUInteger
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.constructor.tlbCodec
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class TransferOp(
    val query_id: ULong = 0u,
    val new_owner: MsgAddress,
    val response_destination: MsgAddress,
    val custom_payload: Maybe<Cell>,
    val forward_amount: VarUInteger,
    val forward_payload: Either<Cell, Cell>,
) : ItemOp {
    companion object : TlbCodec<TransferOp> by TransferOpConstructor {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<TransferOp> = TransferOpConstructor
    }
}

private object TransferOpConstructor : TlbConstructor<TransferOp>(
    schema = "transfer#5fcc3d14 query_id:uint64 new_owner:MsgAddress response_destination:MsgAddress " +
            "custom_payload:(Maybe ^Cell) forward_amount:(VarUInteger 16) forward_payload:(Either Cell ^Cell) " +
            "= InternalMsgBody;"
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: TransferOp) {
        cellBuilder.apply {
            storeUInt64(value.query_id)
            storeTlb(MsgAddress, value.new_owner)
            storeTlb(MsgAddress, value.response_destination)
            storeTlb(Maybe.tlbCodec(AnyTlbConstructor), value.custom_payload)
            storeTlb(VarUInteger.tlbCodec(16), value.forward_amount)
            storeTlb(Either.tlbCodec(AnyTlbConstructor, Cell.tlbCodec()), value.forward_payload)
        }
    }

    override fun loadTlb(cellSlice: CellSlice): TransferOp = cellSlice.run {
        TransferOp(
            query_id = loadUInt64(),
            new_owner = loadTlb(MsgAddress),
            response_destination = loadTlb(MsgAddress),
            custom_payload = loadTlb(Maybe.tlbCodec(AnyTlbConstructor)),
            forward_amount = loadTlb(VarUInteger.tlbCodec(16)),
            forward_payload = loadTlb(Either.tlbCodec(AnyTlbConstructor, Cell.tlbCodec()))
        )
    }
}
