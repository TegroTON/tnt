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

package com.libermall.tnt

import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.contract.ContentData
import org.ton.contract.SnakeData
import org.ton.contract.SnakeDataCons
import org.ton.contract.SnakeDataTail
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient
import org.ton.tlb.storeTlb

suspend fun Message<Cell>.sendMessage(liteClient: LiteClient) =
    liteClient.liteApi.sendMessage(this)

fun AddrStd.toRaw() = toString(userFriendly = false)

fun AddrStd.toSafeBounceable() = toString(userFriendly = true)

fun AddrStd.toSafeUnbounceable() = toString(userFriendly = true, bounceable = false)

fun MsgAddress.toRaw() = when (this) {
    is AddrStd -> toString(userFriendly = false)
    is AddrVar -> "$workchain_id:$address"
    else -> null
}

fun MsgAddress.toSafeBounceable() = when (this) {
    is AddrStd -> toSafeBounceable()
    else -> null
}

fun Cell.toBase64() = base64(BagOfCells(this).toByteArray())

fun StateInit.hash() = CellBuilder.createCell { storeTlb(StateInit, this@hash) }.hash()

fun SnakeData.flatten(): ByteArray = when (this) {
    is SnakeDataTail -> bits.toByteArray()
    is SnakeDataCons -> bits.toByteArray() + next.flatten()
}

fun ContentData.flatten(): ByteArray = when (this) {
    is ContentData.Snake -> this.data.flatten()
    is ContentData.Chunks -> TODO("chunky content data")
}

expect fun readFileAsString(file: String): String

expect fun readFileAsByteArray(file: String): ByteArray

expect fun writeByteArrayToFile(base: String, file: String, value: ByteArray): String
