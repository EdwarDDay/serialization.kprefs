// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.Context
import android.content.SharedPreferences
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.bytes
import io.kotest.property.exhaustive.enum
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(RobolectricTestRunner::class)
class DecodePrimitivesTest {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferences: Preferences

    @BeforeTest
    fun setup() {
        sharedPreferences = createContext().getSharedPreferences("test_preferences", Context.MODE_PRIVATE)
        preferences = Preferences(sharedPreferences)
    }

    @AfterTest
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun getBoolean() = runTest {
        checkAll(Exhaustive.boolean()) { expected ->
            sharedPreferences.edit().putBoolean("useFancyFeature", expected).apply()

            val actual = preferences.decode<Boolean>("useFancyFeature")

            assertEquals(expected, actual)
        }
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
    fun getByte() = runTest {
        checkAll(Exhaustive.bytes()) { expected ->
            sharedPreferences.edit().putInt("windowFlags", expected.toInt()).apply()

            val actual = preferences.decode<Byte>("windowFlags")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun failOnNotStoredByte() {
        assertFailsWith<SerializationException> {
            preferences.decode<Byte>("windowFlags")
        }
    }

    @Test
    fun getShort() = runTest {
        checkAll(Arb.short()) { expected ->
            sharedPreferences.edit().putInt("age", expected.toInt()).apply()

            val actual = preferences.decode<Short>("age")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun failOnNotStoredShort() {
        assertFailsWith<SerializationException> {
            preferences.decode<Short>("age")
        }
    }

    @Test
    fun getInt() = runTest {
        checkAll(Arb.int()) { expected ->
            sharedPreferences.edit().putInt("amount", expected).apply()

            val actual = preferences.decode<Int>("amount")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun failOnNotStoredInt() {
        assertFailsWith<SerializationException> {
            preferences.decode<Int>("amount")
        }
    }

    @Test
    fun getLong() = runTest {
        checkAll(Arb.long()) { expected ->
            sharedPreferences.edit().putLong("count", expected).apply()

            val actual = preferences.decode<Long>("count")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun failOnNotStoredLong() {
        assertFailsWith<SerializationException> {
            preferences.decode<Long>("count")
        }
    }

    @Test
    fun getFloat() = runTest {
        checkAll(Arb.float()) { expected ->
            sharedPreferences.edit().putFloat("wallet", expected).apply()

            val actual = preferences.decode<Float>("wallet")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun failOnNotStoredFloat() {
        assertFailsWith<SerializationException> {
            preferences.decode<Float>("wallet")
        }
    }

    @Test
    fun getDoubleAsFloat() = runTest {
        checkAll(Arb.double()) { expected ->
            preferences = Preferences(sharedPreferences) {
                doubleRepresentation = DoubleRepresentation.FLOAT
            }
            sharedPreferences.edit().putFloat("a_value", expected.toFloat()).apply()

            val actual = preferences.decode<Double>("a_value")

            assertEquals(expected.toFloat().toDouble(), actual)
        }
    }

    @Test
    fun getDoubleAsLongBits() = runTest {
        checkAll(Arb.double()) { expected ->
            preferences = Preferences(sharedPreferences) {
                doubleRepresentation = DoubleRepresentation.LONG_BITS
            }
            sharedPreferences.edit()
                .putLong("a_value", expected.toBits()).apply()
            val actual = preferences.decode<Double>("a_value")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun getDoubleAsString() = runTest {
        checkAll(Arb.double()) { expected ->
            preferences = Preferences(sharedPreferences) {
                doubleRepresentation = DoubleRepresentation.STRING
            }
            sharedPreferences.edit().putString("a_value", expected.toString()).apply()

            val actual = preferences.decode<Double>("a_value")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun failOnNotStoredDouble() {
        assertFailsWith<SerializationException> {
            preferences.decode<Double>("a_value")
        }
    }

    @Test
    fun getChar() = runTest {
        checkAll(Arb.char()) { expected ->
            sharedPreferences.edit().putString("letter", expected.toString()).apply()

            val actual = preferences.decode<Char>("letter")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun failOnNotStoredChar() {
        assertFailsWith<SerializationException> {
            preferences.decode<Char>("letter")
        }
    }

    @Test
    fun getString() = runTest {
        checkAll(Arb.string()) { expected ->
            sharedPreferences.edit().putString("theText", expected).apply()

            val actual = preferences.decode<String>("theText")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun failOnNotStoredString() {
        assertFailsWith<SerializationException> {
            preferences.decode<String>("theText")
        }
    }

    @Test
    fun decodeEnum() = runTest {
        checkAll(Exhaustive.enum<Weekday>()) { expected ->
            sharedPreferences.edit().putString("enum", expected.name).apply()

            val actual = preferences.decode<Weekday>("enum")

            assertSame(expected, actual)
        }
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
