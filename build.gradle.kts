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

plugins {
    base // needed for knit
    kotlin("android") version "1.4.32" apply false
    kotlin("plugin.serialization") version "1.4.32" apply false

    id("org.jmailen.kotlinter") version "3.4.0"
    id("io.gitlab.arturbosch.detekt") version "1.16.0" apply false

    id("org.jetbrains.dokka") version "1.4.32" apply false
}

apply {
    plugin("kotlinx-knit")
}

buildscript {

    repositories {
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.kotlinx:kotlinx-knit:0.2.3")
    }
}
