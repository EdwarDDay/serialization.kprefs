// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from README.md by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.exampleReadme02

import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*

class ReadmeExample {

    val sharedPreferences = TestablePreferences()

    @Test
    fun readmeTest() {

val preferences = Preferences(sharedPreferences)

var someFlag: Boolean by preferences.asProperty(default = false)

fun someComputation() {
    someFlag = true
}

someComputation()
if (!someFlag) { // reads value from SharedPreferences at key "someFlag"
    fail()
}
    }
}
