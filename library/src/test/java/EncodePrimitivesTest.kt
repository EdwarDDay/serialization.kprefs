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
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.andNull
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.bytes
import io.kotest.property.exhaustive.enum
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class EncodePrimitivesTest {

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
    fun putBoolean() = runTest {
        checkAll(Exhaustive.boolean()) { expected ->
            preferences.encode("useFancyFeature", expected)

            val actual = sharedPreferences.getBoolean("useFancyFeature", !expected)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun putBooleanNullable() = runTest {
        checkAll(Exhaustive.boolean().andNull()) { expected ->
            preferences.encode("useFancyFeature", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                val actual = sharedPreferences.getBoolean("useFancyFeature", false)
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun putByte() = runTest {
        checkAll(Exhaustive.bytes()) { expected ->
            preferences.encode("windowFlags", expected)

            val actual = sharedPreferences.getInt("windowFlags", 0)
            assertEquals(expected.toInt(), actual)
        }
    }

    @Test
    fun putByteNullable() = runTest {
        checkAll(Exhaustive.bytes().andNull()) { expected ->
            preferences.encode("windowFlags", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                val actual = sharedPreferences.getInt("windowFlags", 0)
                assertEquals(expected.toInt(), actual)
            }
        }
    }

    @Test
    fun putShort() = runTest {
        checkAll(Arb.short()) { expected ->
            preferences.encode("age", expected)

            val actual = sharedPreferences.getInt("age", 0)

            assertEquals(expected.toInt(), actual)
        }
    }

    @Test
    fun putShortNullable() = runTest {
        checkAll(Arb.short().orNull(0.01)) { expected ->
            preferences.encode("age", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                val actual = sharedPreferences.getInt("age", 0)
                assertEquals(expected.toInt(), actual)
            }
        }
    }

    @Test
    fun putInt() = runTest {
        checkAll(Arb.int()) { expected ->
            preferences.encode("amount", expected)

            val actual = sharedPreferences.getInt("amount", 0)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun putIntNullable() = runTest {
        checkAll(Arb.int().orNull(0.01)) { expected ->
            preferences.encode("amount", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                val actual = sharedPreferences.getInt("amount", 0)
                assertEquals(expected.toInt(), actual)
            }
        }
    }

    @Test
    fun putLong() = runTest {
        checkAll(Arb.long()) { expected ->
            preferences.encode("count", expected)

            val actual = sharedPreferences.getLong("count", 0)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun putLongNullable() = runTest {
        checkAll(Arb.long().orNull(0.01)) { expected ->
            preferences.encode("count", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                val actual = sharedPreferences.getLong("count", 0)
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun putFloat() = runTest {
        checkAll(Arb.float()) { expected ->
            preferences.encode("wallet", expected)

            val actual = sharedPreferences.getFloat("wallet", 0f)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun putFloatNullable() = runTest {
        checkAll(Arb.float().orNull(0.01)) { expected ->
            preferences.encode("wallet", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                val actual = sharedPreferences.getFloat("wallet", 0f)
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun putDoubleAsFloat() = runTest {
        checkAll(Arb.double()) { expected ->
            preferences = Preferences(sharedPreferences) {
                doubleRepresentation = DoubleRepresentation.FLOAT
            }
            preferences.encode("a_value", expected)

            val actual = sharedPreferences.getFloat("a_value", 0f).toDouble()
            assertEquals(expected.toFloat().toDouble(), actual)
        }
    }

    @Test
    fun putDoubleAsLongBits() = runTest {
        checkAll(Arb.double()) { expected ->
            preferences = Preferences(sharedPreferences) {
                doubleRepresentation = DoubleRepresentation.LONG_BITS
            }
            preferences.encode("a_value", expected)

            val actual = Double.fromBits(sharedPreferences.getLong("a_value", 0))
            assertEquals(expected, actual)
        }
    }

    @Test
    fun putDoubleAsString() = runTest {
        checkAll(Arb.double()) { expected ->
            preferences = Preferences(sharedPreferences) {
                doubleRepresentation = DoubleRepresentation.STRING
            }
            preferences.encode("a_value", expected)

            val actual = sharedPreferences.getString("a_value", null)!!.toDouble()

            assertEquals(expected, actual)
        }
    }

    @Test
    fun putChar() = runTest {
        checkAll(Arb.char()) { expected ->
            preferences.encode("letter", expected)

            val actual = sharedPreferences.getString("letter", null)!!
            assertEquals(expected.toString(), actual)
        }
    }

    @Test
    fun putCharNullable() = runTest {
        checkAll(Arb.char().orNull()) { expected ->
            preferences.encode("letter", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                val actual = sharedPreferences.getString("letter", null)
                assertEquals(expected.toString(), actual)
            }
        }
    }

    @Test
    fun putString() = runTest {
        checkAll(Arb.string()) { expected ->
            preferences.encode("theText", expected)

            val actual = sharedPreferences.getString("theText", null)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun putStringNullable() = runTest {
        checkAll(Arb.string().orNull(0.01)) { expected ->
            preferences.encode("theText", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                val actual = sharedPreferences.getString("theText", null)
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun putEnum() = runTest {
        checkAll(Exhaustive.enum<Weekday>()) { expected ->
            preferences.encode("enum", expected)

            assertEquals(expected.name, sharedPreferences.getString("enum", null))
        }
    }

    @Test
    fun putEnumNullable() = runTest {
        checkAll(Exhaustive.enum<Weekday>().andNull()) { expected ->
            preferences.encode("enum", expected)

            if (expected == null) {
                assertTrue(sharedPreferences.all.isEmpty())
            } else {
                assertEquals(expected.name, sharedPreferences.getString("enum", null))
            }
        }
    }
}
