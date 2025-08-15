// SPDX-FileCopyrightText: 2020-2024 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

import app.cash.licensee.SpdxId
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)

    alias(libs.plugins.licensee)
    alias(libs.plugins.dokka)

    id("publishing.maven-convention")
}

apply {
    plugin("kotlinx-knit")
}

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "net.edwardday.serialization.preferences"

    defaultConfig {
        minSdk = 11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        allWarningsAsErrors = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        warningsAsErrors = true
    }

    buildFeatures {
        androidResources = false
        buildConfig = false
        resValues = false
    }
}

kotlin {
    explicitApi()
}

ktlint {
    filter {
        // ktlint should ignore knit generated files
        exclude("**/example/**")
    }
}

detekt {
    config.from(files("detekt-config.yml"))
}

dependencies {
    api(libs.kotlinx.serialization.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotest.property)
}

tasks.withType<DokkaTask> {
    moduleName.set("kprefs")
    dokkaSourceSets {
        configureEach {
            reportUndocumented.set(true)
            noAndroidSdkLink.set(false)
            externalDocumentationLink("https://kotlin.github.io/kotlinx.serialization/")
        }
    }
}

tasks.register<Jar>("dokkaJavadocJar") {
    group = "publishing"
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap(DokkaTask::outputDirectory))
    archiveClassifier.set("javadoc")
}

licensee {
    allow(SpdxId.Apache_20)
}
