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

package com.libermall.tnt.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.crypto.base64

@Serializer(forClass = Cell::class)
object CellSerializer : KSerializer<Cell> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Cell", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Cell) {
        encoder.encodeString(base64(BagOfCells(value).toByteArray()))
    }

    override fun deserialize(decoder: Decoder): Cell =
        BagOfCells(base64(decoder.decodeString())).roots.first()
}
