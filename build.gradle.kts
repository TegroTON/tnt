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

plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    application
}

group = "com.libermall"
version = "0.1.0"

application {
    mainClass.set("com.libermall.tnt.ApplicationKt")
}

distributions {
    main {
        contents {
            from("README.md", "LICENSE.txt")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.github.ajalt.clikt:clikt:4.2.2")
                implementation("com.github.ajalt.mordant:mordant:2.0.0-beta7")
                implementation("com.github.andreypfau.ton-kotlin:ton-kotlin:0.2.16")
                implementation("io.github.microutils:kotlin-logging:2.1.23")
                implementation("io.insert-koin:koin-core:3.2.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("ch.qos.logback:logback-classic:1.4.0")
            }
        }
    }
}
