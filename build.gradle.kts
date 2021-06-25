// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    base // needed for knit
    kotlin("android") version "1.5.10" apply false
    kotlin("plugin.serialization") version "1.5.20" apply false

    id("org.jmailen.kotlinter") version "3.4.5"
    id("io.gitlab.arturbosch.detekt") version "1.17.1" apply false

    id("org.jetbrains.dokka") version "1.4.32" apply false

    id("app.cash.licensee") version "1.0.2" apply false
}

apply {
    plugin("kotlinx-knit")
}

buildscript {

    repositories {
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath("org.jetbrains.kotlinx:kotlinx-knit:0.2.3")
    }
}
