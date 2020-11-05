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

object BuildPlugin {

    const val androidLibrary = "com.android.library"
    const val androidApplication = "com.android.application"

    const val bintray = "com.jfrog.bintray"

    const val detekt = "io.gitlab.arturbosch.detekt"
    const val dokka = "org.jetbrains.dokka"

    const val ktlint = "org.jmailen.kotlinter"

    object Kotlin {
        const val android = "android"

        const val serialization = "plugin.serialization"
    }
}
