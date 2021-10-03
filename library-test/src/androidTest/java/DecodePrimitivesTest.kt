// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.serialization.SerializationException
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@SmallTest
class DecodePrimitivesTest {

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
        sharedPreferences.edit().putInt("windowFlags", 0x5B).apply()

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
        sharedPreferences.edit().putInt("amount", 7654321).apply()

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
        sharedPreferences.edit().putLong("count", 10987654321).apply()

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
        sharedPreferences.edit().putFloat("wallet", 123.45f).apply()

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
        sharedPreferences.edit().putFloat("a_value", 123.45f).apply()

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
        sharedPreferences.edit().putString("a_value", "123.45").apply()

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
        sharedPreferences.edit().putString("letter", "$").apply()

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
        sharedPreferences.edit().putString("theText", "loading a string").apply()

        val actual = preferences.decode<String>("theText")

        assertEquals("loading a string", actual)
    }

    @Test
    fun failOnNotStoredString() {
        assertFailsWith<SerializationException> {
            preferences.decode<String>("theText")
        }
    }

    @Test
    fun decodeEnumMonday() {
        sharedPreferences.edit().putString("enum", "MONDAY").apply()

        val actual = preferences.decode<Weekday>("enum")

        assertSame(Weekday.MONDAY, actual)
    }

    @Test
    fun decodeEnumSunday() {
        sharedPreferences.edit().putString("enum", "SUNDAY").apply()

        val actual = preferences.decode<Weekday>("enum")

        assertSame(Weekday.SUNDAY, actual)
    }

    @Test
    fun decodeEnumNonExistent() {
        sharedPreferences.edit().putString("enum", "NODAY").apply()

        assertFailsWith<SerializationException> {
            preferences.decode<Weekday>("enum")
        }
    }

    @Test
    fun decodeNotStoredEnum() {
        assertFailsWith<SerializationException> {
            preferences.decode<Weekday>("enum")
        }
    }
}
