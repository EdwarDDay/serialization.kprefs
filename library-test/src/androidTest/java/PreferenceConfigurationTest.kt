// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class PreferenceConfigurationTest {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferences: Preferences

    @BeforeTest
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sharedPreferences = context.getSharedPreferences("test_preferences", Context.MODE_PRIVATE)
        preferences = Preferences(sharedPreferences)
    }

    @AfterTest
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun checkInitialValues() {
        val calledInPlace: Boolean
        Preferences(preferences) {
            assertSame(sharedPreferences, this.sharedPreferences)
            assertSame(DoubleRepresentation.LONG_BITS, this.doubleRepresentation)
            assertTrue(this.encodeObjectStarts)
            assertTrue(this.encodeStringSetNatively)
            assertFalse(this.synchronizeEncoding)
            assertEquals(
                expected = listOf("kotlin.collections.HashSet", "kotlin.collections.LinkedHashSet"),
                actual = this.stringSetDescriptorNames,
            )
            assertSame(EmptySerializersModule, this.serializersModule)
            calledInPlace = true
        }
        assertTrue(calledInPlace)
    }

    @Test
    fun throwOnChangingStringSetDesciptorNamesWithoutNativeSetEncoding() {
        assertFailsWith<IllegalArgumentException> {
            Preferences(preferences) {
                encodeStringSetNatively = false
                stringSetDescriptorNames.clear()
            }
        }
    }
}
