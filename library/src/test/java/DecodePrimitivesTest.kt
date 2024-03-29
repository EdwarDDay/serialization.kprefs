// SPDX-FileCopyrightText: 2020-2024 Eduard Wolf
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
import io.kotest.property.exhaustive.andNull
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
    fun getStoredNullableBoolean() = runTest {
        checkAll(Exhaustive.boolean().andNull()) { expected ->
            if (expected != null) {
                sharedPreferences.edit().putBoolean("useFancyFeature", expected).apply()
            }

            val actual = preferences.decodeOrDefault<Boolean?>("useFancyFeature", null)

            assertEquals(expected, actual)

            sharedPreferences.edit().clear().apply()
        }
    }

    @Test
    fun getBooleanNullable() = runTest {
        checkAll(Exhaustive.boolean().andNull()) { expected ->
            sharedPreferences.edit().apply {
                putBoolean("useFancyFeature.\$isNotNull", expected != null)
                if (expected != null) {
                    putBoolean("useFancyFeature", expected)
                }
            }.apply()

            val actual = preferences.decode<Boolean?>("useFancyFeature")

            assertEquals(expected, actual)
            sharedPreferences.edit().clear().apply()
        }
    }

    @Test
    fun failOnNotStoredBoolean() {
        assertFailsWith<SerializationException> {
            preferences.decode<Boolean>("useFancyFeature")
        }
    }

    @Test
    fun failOnNotStoredNullableBoolean() {
        assertFailsWith<SerializationException> {
            preferences.decode<Boolean?>("useFancyFeature")
        }
    }

    @Test
    fun getBooleanDefault() = runTest {
        checkAll(Exhaustive.boolean()) { expected ->
            val actual = preferences.decodeOrDefault("useFancyFeature", expected)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun getBooleanNullableOrDefault() {
        val actual = preferences.decodeOrDefault<Boolean?>("useFancyFeature", null)

        assertNull(actual)
    }

    @Test
    fun getBooleanValueAndNotDefault() = runTest {
        checkAll(Exhaustive.boolean()) { expected ->
            sharedPreferences.edit().putBoolean("useFancyFeature", expected).apply()

            val actual = preferences.decodeOrDefault("useFancyFeature", !expected)

            assertEquals(expected, actual)
        }
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
    fun getByteDefault() = runTest {
        checkAll(Exhaustive.bytes()) { expected ->
            val actual = preferences.decodeOrDefault("windowFlags", expected)

            assertEquals(expected, actual)
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
    fun getShortDefault() = runTest {
        checkAll(Arb.short()) { expected ->
            val actual = preferences.decodeOrDefault("age", expected)

            assertEquals(expected, actual)
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
    fun getIntDefault() = runTest {
        checkAll(Arb.int()) { expected ->
            val actual = preferences.decodeOrDefault("amount", expected)

            assertEquals(expected, actual)
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
    fun getLongDefault() = runTest {
        checkAll(Arb.long()) { expected ->
            val actual = preferences.decodeOrDefault("count", expected)

            assertEquals(expected, actual)
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
    fun getFloatDefault() = runTest {
        checkAll(Arb.float()) { expected ->
            val actual = preferences.decodeOrDefault("wallet", expected)

            assertEquals(expected, actual)
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
    fun getDoubleDefault() = runTest {
        checkAll(Arb.double()) { expected ->
            val actual = preferences.decodeOrDefault("a_value", expected)

            assertEquals(expected, actual)
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
    fun getCharDefault() = runTest {
        checkAll(Arb.char()) { expected ->
            val actual = preferences.decodeOrDefault("a_value", expected)

            assertEquals(expected, actual)
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
    fun getStringDefault() = runTest {
        checkAll(Arb.string()) { expected ->
            val actual = preferences.decodeOrDefault("theText", expected)

            assertEquals(expected, actual)
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

    @Test
    fun getEnumDefault() = runTest {
        checkAll(Exhaustive.enum<Weekday>()) { expected ->
            val actual = preferences.decodeOrDefault("enum", expected)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun getEnumValueAndNotDefault() = runTest {
        checkAll(Exhaustive.enum<Weekday>(), Exhaustive.enum<Weekday>()) { expected, default ->
            sharedPreferences.edit().putString("enum", expected.name).apply()

            val actual = preferences.decodeOrDefault("enum", default)

            assertEquals(expected, actual)
        }
    }
}
