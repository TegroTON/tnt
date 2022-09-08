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

package com.libermall.tnt.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.libermall.tnt.contract.nft.CollectionContract
import com.libermall.tnt.flatten
import com.libermall.tnt.toSafeBounceable
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.block.AddrStd
import org.ton.contract.FullContent
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

class CollectionCommand : CliktCommand(
    name = "collection",
    help = """
        Query NFT collection information
    """.trimIndent(),
), KoinComponent {
    val liteClient by inject<LiteClient>()
    val terminal by inject<Terminal>()

    val address by argument(name = "ADDRESS", help = "NFT collection contract address").convert { AddrStd(it) }

    override fun run() = runBlocking {
        val contract = CollectionContract.of(address, liteClient)

        terminal.println(
            table {
                captionTop("Collection Properties")
                body {
                    row("Address", address.toSafeBounceable())
                    row("Size", contract.next_item_index)
                    row("Owner", contract.owner.toSafeBounceable() ?: "none")
                }
            }
        )

        terminal.println(
            table {
                captionTop("Collection Content")

                column(1) {
                    overflowWrap = OverflowWrap.BREAK_WORD
                }

                body {
                    when (val full = contract.content.parse { loadTlb(FullContent) }) {
                        is FullContent.OffChain -> {
                            row("Kind", "off-chain")
                            row("Uri", full.uri.data.flatten().decodeToString())
                        }

                        is FullContent.OnChain -> {
                            row("Kind", "on-chain")
                            full.data.toMap().forEach {
                                row(it.key, it.value.flatten().decodeToString())
                            }
                        }
                    }
                }
            }
        )
    }
}
