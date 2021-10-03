// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from README.md by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.exampleReadme03

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
class ReadmeExample {

    val sharedPreferences = ApplicationProvider.getApplicationContext<Context>().getSharedPreferences("test_preferences", Context.MODE_PRIVATE)

    @AfterTest
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun readmeTest() {

@Serializable
data class DataClass(val foo: Int, val bar: Int? = 42)

sharedPreferences.edit().putInt("test.foo", 21).apply()
val preferences = Preferences(sharedPreferences)

val test: DataClass = preferences.decode("test")

assertEquals(DataClass(21, null), test)
    }
}
