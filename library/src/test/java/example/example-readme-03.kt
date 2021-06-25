// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from README.md by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.exampleReadme03

import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*

class ReadmeExample {

    val sharedPreferences = TestablePreferences()

@Serializable
data class DataClass(val foo: Int, val bar: Int? = 42)
    @Test
    fun readmeTest() {

sharedPreferences.edit().putInt("test.foo", 21).apply()
val preferences = Preferences(sharedPreferences)

val test: DataClass = preferences.decode("test")

assertEquals(DataClass(21, null), test)
    }
}
