import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.LintTask

/*
 * Copyright 2020 Eduard Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id(BuildPlugin.androidLibrary)
    kotlin(BuildPlugin.Kotlin.android)
    kotlin(BuildPlugin.Kotlin.serialization)

    id(BuildPlugin.ktlint)
    id(BuildPlugin.detekt)

    id(BuildPlugin.dokka)

    id("publishing.maven-convention")
}

apply {
    plugin(BuildPlugin.knit)
}

repositories {
    mavenCentral()
    google()
    jcenter() // https://youtrack.jetbrains.com/issue/IDEA-261387
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"

    defaultConfig {
        minSdkVersion(11)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        allWarningsAsErrors = true
    }

    lintOptions {
        isWarningsAsErrors = true
    }

    buildFeatures {
        // androidResources = false
        buildConfig = false
    }
}

// workaround, because explicit api mode is not yet supported for android projects
// https://youtrack.jetbrains.com/issue/KT-37652
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = if (name.contains("test", ignoreCase = true)) {
            listOf("-Xopt-in=kotlin.RequiresOptIn")
        } else {
            listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xexplicit-api=strict")
        }
    }
}

// ktlint should ignore knit generated files
tasks.withType<LintTask> {
    exclude("**/example/**")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.0.1")

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

// fix for https://github.com/Kotlin/dokka/issues/1302
tasks.register<Copy>("dokkaHtmlIndexFix") {
    group = "documentation"
    dependsOn(tasks.dokkaHtml)
    from("dokka/index.html")
    into(tasks.dokkaHtml.get().outputDirectory)
}

tasks.register<Jar>("sourcesJar") {
    group = "publishing"
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("dokkaJavadocJar") {
    group = "publishing"
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap(DokkaTask::outputDirectory))
    archiveClassifier.set("javadoc")
}
