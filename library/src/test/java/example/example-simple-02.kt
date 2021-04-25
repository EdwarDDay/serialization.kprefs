// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from README.md by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.exampleSimple02

import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*

class ReadmeExample {

    val sharedPreferences = TestablePreferences()

    @Test
    fun readmeTest() {

@Serializable
data class Test(val foo: Int, val bar: Int? = 42)

sharedPreferences.edit().putInt("test.foo", 21).apply()
val preferences = Preferences(sharedPreferences)

val test: Test = preferences.decode("test")

assertEquals(Test(21, null), test)
    }
}
