// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
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
        kotlinCompilerExtensionVersion = "1.3.0-rc01"
    }
}

dependencies {
    implementation(project(":library"))

    // Integration with activities
    implementation("androidx.activity:activity-compose:1.5.1")
    val composeLibrariesVersion = "1.2.0"
    // Compose Material Design
    implementation("androidx.compose.material:material:$composeLibrariesVersion")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$composeLibrariesVersion")
}
