// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    base // needed for knit
    kotlin("android") version "1.4.32" apply false
    kotlin("plugin.serialization") version "1.4.32" apply false

    id("org.jmailen.kotlinter") version "3.4.4"
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
        classpath("com.android.tools.build:gradle:4.2.0")
        classpath("org.jetbrains.kotlinx:kotlinx-knit:0.2.3")
    }
}
