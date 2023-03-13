// SPDX-FileCopyrightText: 2020-2023 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.LintTask

// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
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
            if (name.contains("test", ignoreCase = true)) {
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // workaround, because explicit api mode is not yet supported for android projects
        // https://youtrack.jetbrains.com/issue/KT-37652
        if (!name.contains("test", ignoreCase = true)) {
            freeCompilerArgs = listOf("-Xexplicit-api=strict")
        }
    }
}

tasks.withType<LintTask> {
    // ktlint should ignore knit generated files
    exclude("**/example/**")
}

detekt {
    config = files("detekt-config.yml")
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
    allow("Apache-2.0")
}
