// SPDX-FileCopyrightText: 2020-2024 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from AsProperty.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePropertyWithDefault02

import android.content.*
import kotlin.test.*
import kotlinx.serialization.builtins.*
import net.edwardday.serialization.preferences.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PropertyTest {

    val sharedPreferences = createContext()
        .getSharedPreferences("test_preferences", Context.MODE_PRIVATE)
    val preferences = Preferences(sharedPreferences)

    @AfterTest
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

var setting: Boolean by preferences.asProperty(tag = "aSetting", default = false)
    @Test
    fun test() {
        assertFalse(setting)
        setting = true
        assertTrue(setting)
    }
}
