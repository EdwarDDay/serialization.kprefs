// SPDX-FileCopyrightText: 2020-2023 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    base // needed for knit
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt) apply false

    alias(libs.plugins.dokka) apply false

    alias(libs.plugins.licensee) apply false
}

repositories {
    mavenCentral()
}

apply {
    plugin("kotlinx-knit")
}

buildscript {

    repositories {
        google()
    }

    dependencies {
        classpath(libs.android.build.tools.gradle)
        classpath(libs.kotlinx.knit)
    }
}
