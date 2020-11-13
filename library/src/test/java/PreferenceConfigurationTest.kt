/*
 * Copyright 2020 Eduard Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
