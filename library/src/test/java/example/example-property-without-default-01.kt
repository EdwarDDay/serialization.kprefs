// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from AsProperty.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePropertyWithoutDefault01

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

var setting by preferences.asProperty(Boolean.serializer())
    @Test
    fun test() {
        setting = false
        assertFalse(setting)
        setting = true
        assertTrue(setting)
    }
}
