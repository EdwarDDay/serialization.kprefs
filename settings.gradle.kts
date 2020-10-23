/*
 * Copyright 2020 Eduard Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }

    plugins {
        id("com.android.library") version "4.1.0"
        kotlin("android") version "1.4.10"
        kotlin("plugin.serialization") version "1.4.10"

        id("org.jmailen.kotlinter") version "3.2.0"
        id("io.gitlab.arturbosch.detekt") version "1.14.1"
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.library" -> useModule("com.android.tools.build:gradle:4.1.0")
            }
        }
    }
}

include(":library")
include(":sample")
