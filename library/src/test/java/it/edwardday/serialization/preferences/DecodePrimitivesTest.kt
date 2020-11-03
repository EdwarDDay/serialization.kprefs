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

package it.edwardday.serialization.preferences

import android.content.SharedPreferences
import kotlinx.serialization.SerializationException
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DecodePrimitivesTest {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferences: Preferences

    @Before
    fun setup() {
        sharedPreferences = TestablePreferences()
        preferences = Preferences(sharedPreferences)
    }

    @Test
    fun getBoolean() {
        sharedPreferences.edit().putBoolean("useFancyFeature", false).apply()

        val actual = preferences.decode<Boolean>("useFancyFeature")

        assertFalse(actual)
    }

    @Test
    fun failOnNotStoredBoolean() {
        assertFailsWith<SerializationException> {
            preferences.decode<Boolean>("useFancyFeature")
        }
    }

    @Test
    fun getBooleanNullable() {
        val actual = preferences.decode<Boolean?>("useFancyFeature")

        assertNull(actual)
    }

    @Test
    fun getByte() {
        sharedPreferences.edit().putInt("windowFlags", 0x5B)
            .apply()

        val actual = preferences.decode<Byte>("windowFlags")

        assertEquals(0x5B, actual)
    }

    @Test
    fun failOnNotStoredByte() {
        assertFailsWith<SerializationException> {
            preferences.decode<Byte>("windowFlags")
        }
    }

    @Test
    fun getShort() {
        sharedPreferences.edit().putInt("age", 255).apply()

        val actual = preferences.decode<Short>("age")

        assertEquals(255, actual)
    }

    @Test
    fun failOnNotStoredShort() {
        assertFailsWith<SerializationException> {
            preferences.decode<Short>("age")
        }
    }

    @Test
    fun getInt() {
        sharedPreferences.edit().putInt("amount", 7654321)
            .apply()

        val actual = preferences.decode<Int>("amount")

        assertEquals(7654321, actual)
    }

    @Test
    fun failOnNotStoredInt() {
        assertFailsWith<SerializationException> {
            preferences.decode<Int>("amount")
        }
    }

    @Test
    fun getLong() {
        sharedPreferences.edit()
            .putLong("count", 10987654321).apply()

        val actual = preferences.decode<Long>("count")

        assertEquals(10987654321, actual)
    }

    @Test
    fun failOnNotStoredLong() {
        assertFailsWith<SerializationException> {
            preferences.decode<Long>("count")
        }
    }

    @Test
    fun getFloat() {
        sharedPreferences.edit().putFloat("wallet", 123.45f)
            .apply()

        val actual = preferences.decode<Float>("wallet")

        assertEquals(123.45f, actual)
    }

    @Test
    fun failOnNotStoredFloat() {
        assertFailsWith<SerializationException> {
            preferences.decode<Float>("wallet")
        }
    }

    @Test
    fun getDoubleAsFloat() {
        preferences = Preferences(sharedPreferences) {
            doubleRepresentation = DoubleRepresentation.FLOAT
        }
        sharedPreferences.edit().putFloat("a_value", 123.45f)
            .apply()

        val actual = preferences.decode<Double>("a_value")

        assertTrue(actual > 123.449)
        assertTrue(actual < 123.451)
    }

    @Test
    fun getDoubleAsLongBits() {
        preferences = Preferences(sharedPreferences) {
            doubleRepresentation = DoubleRepresentation.LONG_BITS
        }
        sharedPreferences.edit()
            .putLong("a_value", 123.45.toBits()).apply()
        val actual = preferences.decode<Double>("a_value")

        assertEquals(123.45, actual)
    }

    @Test
    fun getDoubleAsString() {
        preferences = Preferences(sharedPreferences) {
            doubleRepresentation = DoubleRepresentation.STRING
        }
        sharedPreferences.edit()
            .putString("a_value", "123.45").apply()

        val actual = preferences.decode<Double>("a_value")

        assertEquals(123.45, actual)
    }

    @Test
    fun failOnNotStoredDouble() {
        assertFailsWith<SerializationException> {
            preferences.decode<Double>("a_value")
        }
    }

    @Test
    fun getChar() {
        sharedPreferences.edit().putString("letter", "$")
            .apply()

        val actual = preferences.decode<Char>("letter")

        assertEquals('$', actual)
    }

    @Test
    fun failOnNotStoredChar() {
        assertFailsWith<SerializationException> {
            preferences.decode<Char>("letter")
        }
    }

    @Test
    fun getString() {
        sharedPreferences.edit()
            .putString("theText", "loading a string").apply()

        val actual = preferences.decode<String>("theText")

        assertEquals("loading a string", actual)
    }

    @Test
    fun failOnNotStoredString() {
        assertFailsWith<SerializationException> {
            preferences.decode<String>("theText")
        }
    }
}
