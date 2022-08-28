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

package com.libermall.tnt.logger

import mu.KLogging
import org.ton.logger.Logger

class TonLogger(override var level: Logger.Level = Logger.Level.INFO) : Logger {
    override fun log(level: Logger.Level, message: () -> String) {
        when (level) {
            Logger.Level.DEBUG -> {
                logger.debug(message)
            }

            Logger.Level.FATAL -> {
                logger.error(message)
            }

            Logger.Level.INFO -> {
                logger.info(message)
            }

            Logger.Level.WARN -> {
                logger.warn(message)
            }
        }
    }

    companion object : KLogging()
}
