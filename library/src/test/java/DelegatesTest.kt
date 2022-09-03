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
import io.kotest.property.arbitrary.forClass
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.bytes
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
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class DelegatesTest {

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
    fun booleanDelegate() = runTest {
        checkAll(Exhaustive.boolean()) { expected ->
            val wrapper = object {
                var test: Boolean by preferences.asProperty("boolean")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected, sharedPreferences.getBoolean("boolean", true))
        }
    }

    @Test
    fun byteDelegate() = runTest {
        checkAll(Exhaustive.bytes()) { expected ->
            val wrapper = object {
                var test: Byte by preferences.asProperty("byte")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected.toInt(), sharedPreferences.getInt("byte", 0))
        }
    }

    @Test
    fun shortDelegate() = runTest {
        checkAll(Arb.short()) { expected ->
            val wrapper = object {
                var test: Short by preferences.asProperty("short")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected.toInt(), sharedPreferences.getInt("short", 0))
        }
    }

    @Test
    fun intDelegate() = runTest {
        checkAll(Arb.int()) { expected ->
            val wrapper = object {
                var test: Int by preferences.asProperty("int")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected, sharedPreferences.getInt("int", 0))
        }
    }

    @Test
    fun longDelegate() = runTest {
        checkAll(Arb.long()) { expected ->
            val wrapper = object {
                var test: Long by preferences.asProperty("long")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected, sharedPreferences.getLong("long", 0))
        }
    }

    @Test
    fun floatDelegate() = runTest {
        checkAll(Arb.float()) { expected ->
            val wrapper = object {
                var test: Float by preferences.asProperty("float")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected, sharedPreferences.getFloat("float", 0F))
        }
    }

    @Test
    fun doubleDelegate() = runTest {
        checkAll(Arb.double()) { expected ->
            val wrapper = object {
                var test: Double by preferences.asProperty("double")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected.toBits(), sharedPreferences.getLong("double", 0))
        }
    }

    @Test
    fun charDelegate() = runTest {
        checkAll(Arb.char()) { expected ->
            val wrapper = object {
                var test: Char by preferences.asProperty("char")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected.toString(), sharedPreferences.getString("char", null))
        }
    }

    @Test
    fun stringDelegate() = runTest {
        checkAll(Arb.string()) { expected ->
            val wrapper = object {
                var test: String by preferences.asProperty("string")
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected, sharedPreferences.getString("string", null))
        }
    }

    @Test
    fun delegateFailsOnNotInitialized() {
        val wrapper = object {
            var test: Boolean by preferences.asProperty("boolean")
        }

        assertFailsWith<SerializationException> {
            wrapper.test
        }
    }

    @Test
    fun delegateUsesPropertyName() {
        val wrapper = object {
            var test: Boolean by preferences.asProperty()
        }

        wrapper.test = true
        assertTrue(sharedPreferences.getBoolean("test", false))
    }

    @Test
    fun classDelegate() = runTest {
        checkAll(Arb.forClass<Complex>(Complex::class)) { expected ->
            val wrapper = object {
                var test: Complex by preferences.asProperty()
            }

            wrapper.test = expected
            assertEquals(expected, wrapper.test)
            assertEquals(expected.simple.bar, sharedPreferences.getInt("test.simple.bar", 0))
            assertEquals(expected.optional.foo, sharedPreferences.getString("test.optional.foo", null))
        }
    }

    @Test
    fun classDelegateWithDefault() = runTest {
        checkAll(Arb.forClass<Complex>(Complex::class)) { expected ->
            val wrapper = object {
                var test: Complex by preferences.asProperty(default = expected)
            }

            assertEquals(expected = expected, actual = wrapper.test)
            assertTrue(sharedPreferences.all.isEmpty())
        }
    }

    @Test
    fun nullableIntDelegate() {
        val wrapper = object {
            var test: Int? by preferences.asProperty()
        }

        assertNull(wrapper.test)
        assertTrue(sharedPreferences.all.isEmpty())
        wrapper.test = null
        assertTrue(sharedPreferences.all.isEmpty())
        wrapper.test = 5
        assertEquals(5, sharedPreferences.getInt("test", 0))
    }

    @Test
    fun intDelegateWithDefault() {
        val wrapper = object {
            var test: Int by preferences.asProperty(default = 5)
        }

        assertEquals(5, wrapper.test)
        assertTrue(sharedPreferences.all.isEmpty())
        wrapper.test = 4
        assertEquals(4, sharedPreferences.getInt("test", 0))
        sharedPreferences.edit().putInt("test", 3).apply()
        assertEquals(3, wrapper.test)
        sharedPreferences.edit().clear().apply()
        assertEquals(5, wrapper.test)
    }
}
