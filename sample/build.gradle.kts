// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

val composeVersion = "1.2.0"

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.edwardday.serialization.preferences.testapplication"
        minSdk = 22
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 1
        versionName = "0.0.1"
        multiDexEnabled = true
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }

    lint {
        abortOnError = false
    }

    buildTypes {

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
}

dependencies {
    implementation(project(":library"))

    // Integration with activities
    implementation("androidx.activity:activity-compose:1.5.0")
    val composeLibrariesVersion = "1.2.0-rc03"
    // Compose Material Design
    implementation("androidx.compose.material:material:$composeLibrariesVersion")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$composeLibrariesVersion")
}
