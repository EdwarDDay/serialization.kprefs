// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.LintTask

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
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(11)
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        allWarningsAsErrors = true
    }

    lintOptions {
        isWarningsAsErrors = true
    }

    buildFeatures {
        androidResources = false
        buildConfig = false
        resValues = false
    }
}

tasks.withType<KotlinCompile> {
    val isTestTask = name.contains("test", ignoreCase = true)
    kotlinOptions {
        // workaround, because explicit api mode is not yet supported for android projects
        // https://youtrack.jetbrains.com/issue/KT-37652
        freeCompilerArgs = if (isTestTask) {
            listOf("-Xopt-in=kotlin.RequiresOptIn")
        } else {
            listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xexplicit-api=strict")
        }
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
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.2.1")

    testImplementation(kotlin("test-junit"))
}

tasks.withType<DokkaTask> {
    moduleName.set("kprefs")
    dokkaSourceSets {
        configureEach {
            reportUndocumented.set(true)
            noAndroidSdkLink.set(false)
            externalDocumentationLink(
                "https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-core/kotlinx-serialization-core/"
            )
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
