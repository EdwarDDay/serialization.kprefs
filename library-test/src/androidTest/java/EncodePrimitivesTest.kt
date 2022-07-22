// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@SmallTest
class EncodePrimitivesTest {

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
    fun putBoolean() {
        preferences.encode("useFancyFeature", true)

        val actual = sharedPreferences.getBoolean("useFancyFeature", false)
        assertTrue(actual)
    }

    @Test
    fun putBooleanNullable() {
        preferences.encode<Boolean?>("useFancyFeature", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun putBooleanNullableWithValue() {
        preferences.encode<Boolean?>("useFancyFeature", false)

        val actual = sharedPreferences.getBoolean("useFancyFeature", true)
        assertFalse(actual)
    }

    @Test
    fun putByte() {
        preferences.encode<Byte>("windowFlags", 0x7A)

        val actual = sharedPreferences.getInt("windowFlags", 0)
        assertEquals(0x7A, actual)
    }

    @Test
    fun putByteNullable() {
        preferences.encode<Byte?>("windowFlags", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun putShort() {
        preferences.encode<Short>("age", 128)

        val actual = sharedPreferences.getInt("age", 0)
        assertEquals(128, actual)
    }

    @Test
    fun putShortNullable() {
        preferences.encode<Short?>("age", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun putInt() {
        preferences.encode("amount", 1234567)

        val actual = sharedPreferences.getInt("amount", 0)
        assertEquals(1234567, actual)
    }

    @Test
    fun putIntNullable() {
        preferences.encode<Int?>("amount", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun putLong() {
        preferences.encode("count", 12345678910)

        val actual = sharedPreferences.getLong("count", 0)
        assertEquals(12345678910, actual)
    }

    @Test
    fun putLongNullable() {
        preferences.encode<Long?>("count", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun putFloat() {
        preferences.encode("wallet", 123.45f)

        val actual = sharedPreferences.getFloat("wallet", 0f)
        assertEquals(123.45f, actual)
    }

    @Test
    fun putFloatNullable() {
        preferences.encode<Float?>("wallet", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun putDoubleAsFloat() {
        preferences = Preferences(sharedPreferences) {
            doubleRepresentation = DoubleRepresentation.FLOAT
        }
        preferences.encode("a_value", 123.45)

        val actual = sharedPreferences.getFloat("a_value", 0f).toDouble()
        assertTrue(actual > 123.449)
        assertTrue(actual < 123.451)
    }

    @Test
    fun putDoubleAsLongBits() {
        preferences = Preferences(sharedPreferences) {
            doubleRepresentation = DoubleRepresentation.LONG_BITS
        }
        preferences.encode("a_value", 123.45)

        val actual = Double.fromBits(sharedPreferences.getLong("a_value", 0))
        assertEquals(123.45, actual)
    }

    @Test
    fun putDoubleAsString() {
        preferences = Preferences(sharedPreferences) {
            doubleRepresentation = DoubleRepresentation.STRING
        }
        preferences.encode("a_value", 123.45)

        val actual = sharedPreferences.getString("a_value", "0")!!.toDouble()

        assertEquals(123.45, actual)
    }

    @Test
    fun putChar() {
        preferences.encode("letter", 'x')

        val actual = sharedPreferences.getString("letter", null)!!.first()
        assertEquals('x', actual)
    }

    @Test
    fun putCharNullable() {
        preferences.encode<Char?>("letter", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun putString() {
        preferences.encode("theText", "a common text to save")

        val actual = sharedPreferences.getString("theText", null)
        assertEquals("a common text to save", actual)
    }

    @Test
    fun putStringNullable() {
        preferences.encode<String?>("theText", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun putEnum() {
        preferences.encode("enum", Weekday.WEDNESDAY)

        assertEquals("WEDNESDAY", sharedPreferences.getString("enum", null))
    }

    @Test
    fun putEnumNullable() {
        preferences.encode<Weekday?>("enum", null)

        assertTrue(sharedPreferences.all.isEmpty())
    }
}
