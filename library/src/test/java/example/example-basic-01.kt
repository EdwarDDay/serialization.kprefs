// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from README.md by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.exampleBasic01

import kotlin.test.*
import net.edwardday.serialization.preferences.*

class ReadmeExample {

    val sharedPreferences = TestablePreferences()

    fun someComputation() {
        someFlag = true
    }

val preferences = Preferences(sharedPreferences)

var someFlag: Boolean by preferences.asProperty(default = false)

@Test
fun test() {
    someComputation() // some computation where someFlag is set to true
    if (!someFlag) { // reads value from SharedPreferences at key "someFlag"
        fail()
    }
}
}
