// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from Preferences.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePreferences02

import android.content.*
import androidx.test.filters.SmallTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class PreferencesTest {

    val sharedPreferences = ApplicationProvider.getApplicationContext<Context>()
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
