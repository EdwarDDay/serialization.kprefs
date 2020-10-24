import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")

    id("org.jmailen.kotlinter")
    id("io.gitlab.arturbosch.detekt")

    `maven-publish`
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"

    defaultConfig {
        minSdkVersion(9)
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        @kotlin.OptIn(ExperimentalStdlibApi::class)
        val args = buildList(2) {
            add("-Xopt-in=kotlin.RequiresOptIn")
            if (!name.contains("test", ignoreCase = true)) {
                add("-Xexplicit-api=strict")
            }
        }
        freeCompilerArgs = args
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.0.0")

    testImplementation("junit:junit:4.13.1")
    testImplementation(kotlin("test"))
}

val sourcesJar by tasks.register<Jar>("sourcesJar") {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap(DokkaTask::outputDirectory))
    archiveClassifier.set("javadoc")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                artifact(sourcesJar)
                artifact(dokkaJavadocJar)

                group = "it.edwardday.serialization"
                artifactId = "kprefs"
                version = properties["VERSION_NAME"]!!.toString()

                pom {
                    name.set("Kotlinx.serialization Android SharedPreferences format")
                    packaging = "aar"
                    description.set("A serialization format for Android SharedPreferences")
                    url.set("https://github.com/EdwarDDay/serialization.kprefs")
                    scm {
                        url.set("https://github.com/EdwarDDay/serialization.kprefs")
                        connection.set("scm:git:git://github.com/EdwarDDay/serialization.kprefs.git")
                        developerConnection.set("scm:git:ssh://github.com/EdwarDDay/serialization.kprefs.git")
                        tag.set("HEAD")
                    }
                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("EdwarDDay")
                            name.set("Eduard Wolf")
                        }
                    }
                }
            }
        }
    }
}

bintray {
    fun checkedProperty(key: String, fallbackEnvVar: String): String? =
        if (project.hasProperty(key)) project.properties[key]!!.toString() else System.getenv(fallbackEnvVar)

    user = checkedProperty("bintray.user", "BINTRAY_USER")
    key = checkedProperty("bintray.key", "BINTRAY_KEY")
    setPublications("release")

    with(pkg) {
        repo = "maven"
        name = "it.edwardday.serialization:kprefs"
        vcsUrl = "https://github.com/EdwarDDay/serialization.kprefs.git"

        version.name = properties["VERSION_NAME"]!!.toString()
    }
}
