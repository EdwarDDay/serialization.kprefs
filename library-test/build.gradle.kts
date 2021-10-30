// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")

    id("org.jmailen.kotlinter")
    id("io.gitlab.arturbosch.detekt")
}

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 14

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        allWarningsAsErrors = true
    }

    lint {
        isWarningsAsErrors = true
    }

    buildFeatures {
        androidResources = false
        buildConfig = false
        resValues = false
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

tasks.withType<LintTask> {
    // ktlint should ignore knit generated files
    exclude("**/example/**")
}

tasks.withType<Detekt> {
    // detekt should ignore knit generated files
    exclude("**/example/**")
}

dependencies {
    implementation(project(":library"))

    androidTestImplementation(kotlin("test-junit"))
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}
