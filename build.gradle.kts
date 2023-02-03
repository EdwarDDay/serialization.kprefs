// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    base // needed for knit
    val kotlinVersion = "1.8.10"
    @Suppress("RemoveSingleExpressionStringTemplate") // needed for dependabot
    kotlin("android") version "$kotlinVersion" apply false
    @Suppress("RemoveSingleExpressionStringTemplate") // needed for dependabot
    kotlin("plugin.serialization") version "$kotlinVersion" apply false

    id("org.jmailen.kotlinter") version "3.13.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0" apply false

    id("org.jetbrains.dokka") version "1.7.20" apply false

    id("app.cash.licensee") version "1.6.0" apply false
}

apply {
    plugin("kotlinx-knit")
}

buildscript {

    repositories {
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlinx:kotlinx-knit:0.2.3")
    }
}
