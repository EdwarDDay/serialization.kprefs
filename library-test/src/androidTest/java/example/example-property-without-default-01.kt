// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from Delegates.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePropertyWithoutDefault01

import android.content.*
import androidx.test.filters.SmallTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.*
import kotlinx.serialization.builtins.*
import net.edwardday.serialization.preferences.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class PropertyTest {

    val sharedPreferences = ApplicationProvider.getApplicationContext<Context>()
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
