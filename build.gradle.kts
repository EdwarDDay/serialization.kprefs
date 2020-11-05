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
    kotlin(BuildPlugin.Kotlin.android) version BuildVersion.kotlin apply false
    kotlin(BuildPlugin.Kotlin.serialization) version BuildVersion.kotlin apply false

    id(BuildPlugin.ktlint) version BuildVersion.ktlint
    id(BuildPlugin.detekt) version BuildVersion.detekt apply false

    id(BuildPlugin.bintray) version BuildVersion.bintray apply false
    id(BuildPlugin.dokka) version BuildVersion.dokka apply false
}

buildscript {

    repositories {
        google()
    }

    dependencies {
        classpath(BuildClasspath.androidTools)
        classpath(BuildClasspath.kotlinGradle)
    }
}
