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
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.mordant.terminal.Terminal
import com.libermall.tnt.config.MainnetConfig
import com.libermall.tnt.config.SandboxConfig
import com.libermall.tnt.config.TestnetConfig
import com.libermall.tnt.logger.TonLogger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.ton.adnl.client.engine.cio.CIOAdnlClientEngine
import org.ton.api.liteclient.config.LiteClientConfigGlobal
import org.ton.lite.client.LiteClient
import org.ton.logger.Logger

class TntCommand :
    CliktCommand(
        name = "tnt",
        help = """            
            (T)ON (N)FT (T)ool - an all-in-one tool to query, modify, and create Non-Fungible Tokens on The Open Network.
        """.trimIndent(),
        epilog = """
            (T)ON (N)FT (T)ool - all-in-one utility to work with NFTs on The Open Network${'\u0085'}
            Project page: https://github.com/LiberMall/tnt${'\u0085'}
            Copyright (c) 2022${'\u0085'}
            ${'\u0085'}
            This program is free software: you can redistribute it and/or modify${'\u0085'}
            it under the terms of the GNU Affero General Public License as${'\u0085'}
            published by the Free Software Foundation, either version 3 of the${'\u0085'}
            License, or (at your option) any later version.${'\u0085'}
            ${'\u0085'}
            This program is distributed in the hope that it will be useful,${'\u0085'}
            but WITHOUT ANY WARRANTY; without even the implied warranty of${'\u0085'}
            MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the${'\u0085'}
            GNU Affero General Public License for more details.${'\u0085'}
            ${'\u0085'}
            You should have received a copy of the GNU Affero General Public License${'\u0085'}
            along with this program.  If not, see <https://www.gnu.org/licenses/>.${'\u0085'}
        """.trimIndent()
    ),
    KoinComponent {
    val network by option("--network", help = "Network to perform operations in")
        .choice("mainnet", "testnet", "sandbox")
        .default("testnet")

    override fun run() {
        loadKoinModules(
            module {
                single {
                    when (network) {
                        "mainnet" -> LiteClient(
                            CIOAdnlClientEngine.create(), configFromString(MainnetConfig), TonLogger(Logger.Level.DEBUG)
                        )

                        "testnet" ->
                            LiteClient(
                                CIOAdnlClientEngine.create(),
                                configFromString(TestnetConfig),
                                TonLogger(Logger.Level.DEBUG)
                            )

                        "sandbox" -> LiteClient(
                            CIOAdnlClientEngine.create(), configFromString(SandboxConfig), TonLogger(Logger.Level.DEBUG)
                        )

                        else -> throw IllegalStateException()
                    }
                }
                single {
                    Terminal().apply {
                        info.updateTerminalSize()
                    }
                }
            }
        )
    }

    companion object {
        private val json by lazy {
            Json {
                ignoreUnknownKeys = true
            }
        }

        @JvmStatic
        private fun configFromString(str: String) =
            json.decodeFromString<LiteClientConfigGlobal>(str)
    }
}
