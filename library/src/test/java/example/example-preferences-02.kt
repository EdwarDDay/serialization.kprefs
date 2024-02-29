// SPDX-FileCopyrightText: 2020-2024 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from Preferences.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePreferences02

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
data class PrefTest(val u: Unit)

val pref = Preferences(sharedPreferences) { encodeObjectStarts = true }
pref.encode(PrefTest.serializer(), "test", PrefTest(Unit))

assertTrue(sharedPreferences.getBoolean("test.u", false))
    }
}
