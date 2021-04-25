// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.SharedPreferences
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.EmptySerializersModule
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
class PreferenceConfigurationTest {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferences: Preferences

    @BeforeTest
    fun setup() {
        sharedPreferences = TestablePreferences()
        preferences = Preferences(sharedPreferences)
    }

    @Test
    fun checkInitialValues() {
        var checked = false
        Preferences(preferences) {
            assertSame(sharedPreferences, this.sharedPreferences)
            assertSame(DoubleRepresentation.LONG_BITS, this.doubleRepresentation)
            assertTrue(this.encodeObjectStarts)
            assertTrue(this.encodeStringSetNatively)
            assertEquals(
                expected = listOf("kotlin.collections.HashSet", "kotlin.collections.LinkedHashSet"),
                actual = this.stringSetDescriptorNames
            )
            assertSame(EmptySerializersModule, this.serializersModule)
            checked = true
        }
        assertTrue(checked)
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
