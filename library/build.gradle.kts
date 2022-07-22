// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")

    id("org.jmailen.kotlinter")
    id("io.gitlab.arturbosch.detekt")

    id("app.cash.licensee")
    id("org.jetbrains.dokka")

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

    defaultConfig {
        minSdk = 11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        allWarningsAsErrors = true
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
    sourceSets.all {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // workaround, because explicit api mode is not yet supported for android projects
        // https://youtrack.jetbrains.com/issue/KT-37652
        freeCompilerArgs = listOf("-Xexplicit-api=strict")
    }
}

detekt {
    config = files("detekt-config.yml")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.3.3")
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
    allow("Apache-2.0")
}
