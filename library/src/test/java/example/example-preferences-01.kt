// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from Preferences.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePreferences01

import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*

class PreferencesTest {

    val sharedPreferences = TestablePreferences()

    @Test
    fun test() {

@Serializable
data class Person(val name: String, val age: Int)

val preferences = Preferences(sharedPreferences)
val abby = Person("Abby", 20)

preferences.encode("person", abby)

assertEquals("Abby", sharedPreferences.getString("person.name", null))
assertEquals(20, sharedPreferences.getInt("person.age", 0))
    }
}
