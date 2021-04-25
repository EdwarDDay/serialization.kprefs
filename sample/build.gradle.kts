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
    jcenter() // https://youtrack.jetbrains.com/issue/IDEA-261387
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"

    defaultConfig {
        applicationId = "net.edwardday.serialization.preferences.testapplication"
        minSdkVersion(22)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "0.0.1"
        multiDexEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
}
