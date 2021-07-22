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
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "net.edwardday.serialization.preferences.testapplication"
        minSdkVersion(22)
        targetSdkVersion(30)
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

    lintOptions {
        isAbortOnError = false
    }

    buildTypes {

        getByName("release") {
            minifyEnabled(true)
            isShrinkResources = true
        }
    }
}

dependencies {
    implementation(project(":library"))
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
}
