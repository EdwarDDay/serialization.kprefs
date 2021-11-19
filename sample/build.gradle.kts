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
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    lint {
        isAbortOnError = false
    }

    buildTypes {

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
}

dependencies {
    implementation(project(":library"))
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
}
