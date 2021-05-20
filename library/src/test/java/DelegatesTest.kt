// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.SharedPreferences
import kotlinx.serialization.SerializationException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("VarCouldBeVal")
class DelegatesTest {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferences: Preferences

    @BeforeTest
    fun setup() {
        sharedPreferences = TestablePreferences()
        preferences = Preferences(sharedPreferences)
    }

    @Test
    fun booleanDelegate() {
        val wrapper = object {
            var test: Boolean by preferences.asProperty("boolean")
        }

        wrapper.test = false
        assertFalse(wrapper.test)
        assertFalse(sharedPreferences.getBoolean("boolean", true))
        wrapper.test = true
        assertTrue(wrapper.test)
        assertTrue(sharedPreferences.getBoolean("boolean", false))
    }

    @Test
    fun byteDelegate() {
        val wrapper = object {
            var test: Byte by preferences.asProperty("byte")
        }

        wrapper.test = 4
        assertEquals(4, wrapper.test)
        assertEquals(4, sharedPreferences.getInt("byte", 0))
        wrapper.test = 10
        assertEquals(10, wrapper.test)
        assertEquals(10, sharedPreferences.getInt("byte", 0))
    }

    @Test
    fun shortDelegate() {
        val wrapper = object {
            var test: Short by preferences.asProperty("short")
        }

        wrapper.test = 129
        assertEquals(129, wrapper.test)
        assertEquals(129, sharedPreferences.getInt("short", 0))
        wrapper.test = -200
        assertEquals(-200, wrapper.test)
        assertEquals(-200, sharedPreferences.getInt("short", 0))
    }

    @Test
    fun intDelegate() {
        val wrapper = object {
            var test: Int by preferences.asProperty("int")
        }

        wrapper.test = 10000
        assertEquals(10000, wrapper.test)
        assertEquals(10000, sharedPreferences.getInt("int", 0))
        wrapper.test = -123456
        assertEquals(-123456, wrapper.test)
        assertEquals(-123456, sharedPreferences.getInt("int", 0))
    }

    @Test
    fun longDelegate() {
        val wrapper = object {
            var test: Long by preferences.asProperty("long")
        }

        wrapper.test = 10000000001
        assertEquals(10000000001, wrapper.test)
        assertEquals(10000000001, sharedPreferences.getLong("long", 0))
        wrapper.test = -12345678910
        assertEquals(-12345678910, wrapper.test)
        assertEquals(-12345678910, sharedPreferences.getLong("long", 0))
    }

    @Test
    fun floatDelegate() {
        val wrapper = object {
            var test: Float by preferences.asProperty("float")
        }

        wrapper.test = 1.234f
        assertEquals(1.234f, wrapper.test)
        assertEquals(1.234f, sharedPreferences.getFloat("float", 0F))
        wrapper.test = -2e10f
        assertEquals(-2e10f, wrapper.test)
        assertEquals(-2e10f, sharedPreferences.getFloat("float", 0F))
    }

    @Test
    fun doubleDelegate() {
        val wrapper = object {
            var test: Double by preferences.asProperty("double")
        }

        wrapper.test = 1.234
        assertEquals(1.234, wrapper.test)
        assertEquals(1.234.toBits(), sharedPreferences.getLong("double", 0))
        wrapper.test = -2e10
        assertEquals(-2e10, wrapper.test)
        assertEquals((-2e10).toBits(), sharedPreferences.getLong("double", 0))
    }

    @Test
    fun charDelegate() {
        val wrapper = object {
            var test: Char by preferences.asProperty("char")
        }

        wrapper.test = 'x'
        assertEquals('x', wrapper.test)
        assertEquals('x', sharedPreferences.getString("char", null)!!.first())
        wrapper.test = '?'
        assertEquals('?', wrapper.test)
        assertEquals('?', sharedPreferences.getString("char", null)!!.first())
    }

    @Test
    fun stringDelegate() {
        val wrapper = object {
            var test: String by preferences.asProperty("string")
        }

        wrapper.test = "test text"
        assertEquals("test text", wrapper.test)
        assertEquals("test text", sharedPreferences.getString("string", null))
        wrapper.test = "foobar"
        assertEquals("foobar", wrapper.test)
        assertEquals("foobar", sharedPreferences.getString("string", null))
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
    fun classDelegate() {
        val wrapper = object {
            var test: Complex by preferences.asProperty()
        }

        val data = Complex(
            simple = SimpleContainer(5),
            optional = DateWithOptional("bar")
        )
        wrapper.test = data
        assertEquals(expected = data, actual = wrapper.test)
        assertEquals(5, sharedPreferences.getInt("test.simple.bar", 0))
        assertEquals("bar", sharedPreferences.getString("test.optional.foo", null))
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
}
