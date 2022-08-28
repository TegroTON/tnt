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

package com.libermall.tnt.model

import com.libermall.tnt.hash
import com.libermall.tnt.serializer.CellSerializer
import com.libermall.tnt.serializer.MsgAddressSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mu.KLogging
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.StateInit
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.hex
import org.ton.tlb.storeTlb

@Serializable
@SerialName("item")
data class StandaloneItemModel(
    val index: ULong = 0u,
    @Serializable(with = MsgAddressSerializer::class)
    val owner: MsgAddress,
    val content: ContentModel,
    @Serializable(with = CellSerializer::class)
    val code: Cell = CODE,
    val workchain: Int = 0,
) : MintableModel {
    fun address() = AddrStd(workchain, stateInit().hash())
    fun stateInit() = StateInit(code, data())

    fun data() = CellBuilder.createCell {
        storeUInt64(index)
        storeTlb(MsgAddress, AddrNone) // collection is addr_none for standalone items
        storeTlb(MsgAddress, owner)
        storeRef(content.asCell())
    }

    companion object : KLogging() {
        val CODE =
            BagOfCells(hex("B5EE9C7241020D010001D0000114FF00F4A413F4BCF2C80B0102016202030202CE04050009A11F9FE00502012006070201200B0C02D70C8871C02497C0F83434C0C05C6C2497C0F83E903E900C7E800C5C75C87E800C7E800C3C00812CE3850C1B088D148CB1C17CB865407E90350C0408FC00F801B4C7F4CFE08417F30F45148C2EA3A1CC840DD78C9004F80C0D0D0D4D60840BF2C9A884AEB8C097C12103FCBC20080900113E910C1C2EBCB8536001F65135C705F2E191FA4021F001FA40D20031FA00820AFAF0801BA121945315A0A1DE22D70B01C300209206A19136E220C2FFF2E192218E3E821005138D91C85009CF16500BCF16712449145446A0708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB00104794102A375BE20A00727082108B77173505C8CBFF5004CF1610248040708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB000082028E3526F0018210D53276DB103744006D71708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB0093303234E25502F003003B3B513434CFFE900835D27080269FC07E90350C04090408F80C1C165B5B60001D00F232CFD633C58073C5B3327B5520BF75041B")).roots.first()
    }
}
