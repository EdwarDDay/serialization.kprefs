// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from Preferences.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePreferences02

import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*

class PreferencesTest {

    val sharedPreferences = TestablePreferences()
    @Serializable
    data class Person(val name: String, val age: Int)

    @Serializable
    data class PrefTest(val u: Unit)

    @Test
    fun test() {

// given the following class
// data class PrefTest(val u: Unit)

val pref = Preferences(sharedPreferences) { encodeObjectStarts = true }
pref.encode(PrefTest.serializer(), "test", PrefTest(Unit))

assertTrue(sharedPreferences.getBoolean("test.u", false))
    }
}
