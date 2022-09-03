// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from Preferences.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePreferences01

import android.content.*
import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PreferencesTest {

    val sharedPreferences = createContext()
        .getSharedPreferences("test_preferences", Context.MODE_PRIVATE)

    @AfterTest
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

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
