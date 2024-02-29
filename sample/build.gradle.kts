// SPDX-FileCopyrightText: 2020-2024 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "net.edwardday.serialization.preferences.testapplication"

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
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(projects.library)

    // Integration with activities
    implementation(libs.androidx.activity.compose)
    // Compose Material Design
    implementation(libs.androidx.compose.material)
    // Tooling support (Previews, etc.)
    implementation(libs.androidx.compose.ui.tooling)
}
