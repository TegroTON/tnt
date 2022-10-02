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
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.mordant.terminal.Terminal
import com.libermall.tnt.readFileAsByteArray
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.api.exception.TonException
import org.ton.boc.BagOfCells
import org.ton.lite.client.LiteClient

class SendCommand : CliktCommand(
    name = "send",
    help = """
        Send bag-of-cells files to The Open Network
    """.trimIndent(),
), KoinComponent {
    val liteClient by inject<LiteClient>()
    val terminal by inject<Terminal>()

    val bagsOfCells by argument(name = "FILES", help = "List of .boc files to be sent")
        .convert { it to BagOfCells(readFileAsByteArray(it)) }
        .multiple(required = true)

    override fun run() = runBlocking {
        flow {
            while (currentCoroutineContext().isActive) {
                emit(liteClient.getLastBlockId())
                delay(4_000L)
            }
        }
            .distinctUntilChanged()
            .withIndex()
            .filter { it.index % 2 == 0 } // Each second block
            .map { it.value }
            .onEach { logger.debug { "masterchain block seqno ${it.seqno}" } }
            .zip(bagsOfCells.asFlow()) { blockId, fileBoC ->
                blockId.seqno to fileBoC
            }
            .collect {
                var retries = 0
                val (seqno, fileBoC) = it
                val (filename, boc) = fileBoC

                terminal.print("Sending file $filename...");
                while (retries <= 10) {
                    try {
                        val result = liteClient.liteApi.sendMessage(boc)
                        terminal.println(" status=${result.status}.")
                        break
                    } catch (e: TonException) {
                        terminal.print(" retrying...")
                        delay(2_000L)
                        ++retries
                    }
                }
                if (retries >= 10)
                    terminal.println("failed.")
            }
    }

    companion object : KLogging()
}
