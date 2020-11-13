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
import kotlinx.serialization.SerializationException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
class EncodingTest {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferences: Preferences

    @BeforeTest
    fun setup() {
        sharedPreferences = TestablePreferences()
        preferences = Preferences(sharedPreferences)
    }

    @Test
    fun testPrimitivesContainer() {
        val data = SimplePrimitivesContainer(
            a = true, b = 4, c = 8, d = -12, e = -1234, f = 12.6f, g = 12e3, h = '?', i = "foobar"
        )
        preferences.encode("container", data)
        val actual = preferences.decode<SimplePrimitivesContainer>("container")

        assertEquals(data, actual)
        assertEquals(
            expected = setOf(
                "container.a",
                "container.b",
                "container.c",
                "container.d",
                "container.e",
                "container.f",
                "container.g",
                "container.h",
                "container.i"
            ),
            actual = sharedPreferences.all.keys
        )
        assertTrue(sharedPreferences.getBoolean("container.a", false))
        assertEquals(4, sharedPreferences.getInt("container.b", 0))
        assertEquals(8, sharedPreferences.getInt("container.c", 0))
        assertEquals(-12, sharedPreferences.getInt("container.d", 0))
        assertEquals(-1234, sharedPreferences.getLong("container.e", 0))
        assertEquals(12.6f, sharedPreferences.getFloat("container.f", 0f))
        assertEquals(12e3, Double.fromBits(sharedPreferences.getLong("container.g", 0)))
        assertEquals("?", sharedPreferences.getString("container.h", null))
        assertEquals("foobar", sharedPreferences.getString("container.i", null))
    }

    @Test
    fun testComplexData() {
        val data = Complex(simple = SimpleContainer(bar = 9), optional = DateWithOptional(foo = "hello"))
        preferences.encode("complex", data)
        val actual = preferences.decode<Complex>("complex")

        assertEquals(data, actual)
        assertEquals(
            expected = setOf(
                "complex.simple.bar",
                "complex.optional.foo"
            ),
            actual = sharedPreferences.all.keys
        )
        assertEquals(9, sharedPreferences.getInt("complex.simple.bar", 0))
        assertEquals("hello", sharedPreferences.getString("complex.optional.foo", null))
    }

    @Test
    fun testNonNullDecoding() {
        val data = Complex(simple = SimpleContainer(bar = 42), optional = DateWithOptional(foo = "World"))
        preferences.encode("complex", data)
        val actual = preferences.decode<Complex?>("complex")

        assertEquals(data, actual)
        assertEquals(
            expected = setOf(
                "complex.simple.bar",
                "complex.optional.foo"
            ),
            actual = sharedPreferences.all.keys
        )
        assertEquals(42, sharedPreferences.getInt("complex.simple.bar", 0))
        assertEquals("World", sharedPreferences.getString("complex.optional.foo", null))
    }

    @Test
    fun testNullDecoding() {
        preferences.encode<Complex?>("complex", null)
        val actual = preferences.decode<Complex?>("complex")

        assertNull(actual)
        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun testList() {
        val data = listOf(SimpleContainer(bar = 1), SimpleContainer(bar = 2))

        preferences.encode("list", data)
        val actual = preferences.decode<List<SimpleContainer>>("list")

        assertEquals(data, actual)
        assertEquals(
            expected = setOf(
                "list.0.bar",
                "list.1.bar"
            ),
            actual = sharedPreferences.all.keys
        )
        assertEquals(1, sharedPreferences.getInt("list.0.bar", 0))
        assertEquals(2, sharedPreferences.getInt("list.1.bar", 0))
    }

    @Test
    fun testEmptyList() {
        preferences.encode<List<SimpleContainer>>("list", emptyList())
        val actual = preferences.decode<List<SimpleContainer>>("list")

        assertEquals(emptyList(), actual)
        assertEquals(
            expected = setOf("list"),
            actual = sharedPreferences.all.keys
        )
        assertTrue(sharedPreferences.getBoolean("list", false))
    }

    @Test
    fun testEmptyListException() {
        preferences = Preferences(sharedPreferences) {
            encodeObjectStarts = false
        }

        assertFailsWith<SerializationException> {
            preferences.encode<List<SimpleContainer>>("list", emptyList())
        }
        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun testEmptyArray() {
        preferences.encode<Array<SimpleContainer>>("array", emptyArray())
        val actual = preferences.decode<Array<SimpleContainer>>("array")

        assertEquals(0, actual.size)
        assertEquals(
            expected = setOf("array"),
            actual = sharedPreferences.all.keys
        )
        assertTrue(sharedPreferences.getBoolean("array", false))
    }

    @Test
    fun testEmptyArrayException() {
        preferences = Preferences(sharedPreferences) {
            encodeObjectStarts = false
        }

        assertFailsWith<SerializationException> {
            preferences.encode<Array<SimpleContainer>>("array", emptyArray())
        }
        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun testMap() {
        val data = mapOf("a" to SimpleContainer(bar = 1), "b" to SimpleContainer(bar = 2))
        preferences.encode("map", data)
        val actual = preferences.decode<Map<String, SimpleContainer>>("map")

        assertEquals(data, actual)
        assertEquals(
            expected = setOf(
                "map.0",
                "map.1.bar",
                "map.2",
                "map.3.bar",
            ),
            actual = sharedPreferences.all.keys
        )
        assertEquals("a", sharedPreferences.getString("map.0", null))
        assertEquals(1, sharedPreferences.getInt("map.1.bar", 0))
        assertEquals("b", sharedPreferences.getString("map.2", null))
        assertEquals(2, sharedPreferences.getInt("map.3.bar", 0))
    }

    @Test
    fun testEmptyMap() {
        preferences.encode<Map<String, SimpleContainer>>("map", emptyMap())
        val actual = preferences.decode<Map<String, SimpleContainer>>("map")

        assertEquals(emptyMap(), actual)
        assertEquals(
            expected = setOf("map"),
            actual = sharedPreferences.all.keys
        )
        assertTrue(sharedPreferences.getBoolean("map", false))
    }

    @Test
    fun testEmptyMapException() {
        preferences = Preferences(sharedPreferences) {
            encodeObjectStarts = false
        }

        assertFailsWith<SerializationException> {
            preferences.encode<Map<String, SimpleContainer>>("map", emptyMap())
        }
        assertTrue(sharedPreferences.all.isEmpty())
    }

    @Test
    fun testComplexMap() {
        val data = mapOf(
            SimpleContainer(bar = 1) to DateWithOptional(),
            SimpleContainer(bar = 2) to DateWithOptional(foo = "bar")
        )
        preferences.encode("map", data)
        val actual = preferences.decode<Map<SimpleContainer, DateWithOptional>>("map")

        assertEquals(data, actual)
        assertEquals(
            expected = setOf(
                "map.0.bar",
                "map.1.foo",
                "map.2.bar",
                "map.3.foo",
            ),
            actual = sharedPreferences.all.keys
        )
        assertEquals(1, sharedPreferences.getInt("map.0.bar", 0))
        assertEquals("optional", sharedPreferences.getString("map.1.foo", null))
        assertEquals(2, sharedPreferences.getInt("map.2.bar", 0))
        assertEquals("bar", sharedPreferences.getString("map.3.foo", null))
    }

    @Test
    fun testNestedMap() {
        val data: Map<String, List<Map<String, Map<String, Int>>>> = mapOf(
            "foo" to emptyList(),
            "bar" to listOf(emptyMap()),
            "foobar" to listOf(mapOf("foobarbar" to emptyMap())),
            "foofoo" to listOf(
                emptyMap(),
                mapOf("foofoobar" to mapOf("1" to 1)),
                mapOf("foofoobarbar" to mapOf("1" to 1, "2" to 2))
            )
        )

        preferences.encode("nestedMap", data)
        val actual = preferences.decode<Map<String, List<Map<String, Map<String, Int>>>>>("nestedMap")

        assertEquals(data, actual)
    }

    @Test
    fun testSealedClass() {
        val data = listOf(SealedDate.Sealed1(false), SealedDate.Sealed2(2), SealedDate.Sealed3)
        preferences.encode("sealed", data)
        val actual = preferences.decode<List<SealedDate>>("sealed")

        assertEquals(data, actual)
        assertEquals(
            expected = setOf(
                "sealed.0.type",
                "sealed.0.value.a",
                "sealed.1.type",
                "sealed.1.value.b",
                "sealed.2.type",
                "sealed.2.value"
            ),
            actual = sharedPreferences.all.keys
        )
        assertEquals("sealed1", sharedPreferences.getString("sealed.0.type", null))
        assertFalse(sharedPreferences.getBoolean("sealed.0.value.a", true))
        assertEquals("sealed2", sharedPreferences.getString("sealed.1.type", null))
        assertEquals(2, sharedPreferences.getInt("sealed.1.value.b", 0))
        assertEquals("sealed3", sharedPreferences.getString("sealed.2.type", null))
        assertTrue(sharedPreferences.getBoolean("sealed.2.value", false))
    }

    @Test
    fun testUseOptionalData() {
        val actual = preferences.decode<DateWithOptional>("optional")

        assertEquals(DateWithOptional(), actual)
    }

    @Test
    fun testOverwriteOptionalData() {
        sharedPreferences.edit().putString("optional.foo", "bar").apply()

        val actual = preferences.decode<DateWithOptional>("optional")

        assertEquals(DateWithOptional(foo = "bar"), actual)
    }

    @Test
    fun testObjectWithNullField() {
        preferences.encode("nullable", DateWithNullable(null))

        val actual = preferences.decode<DateWithNullable>("nullable")

        assertEquals(DateWithNullable(null), actual)
    }

    @Test
    fun testExceptionOnEmptyObjectCreation() {
        preferences = Preferences(sharedPreferences) {
            encodeObjectStarts = false
        }

        assertFailsWith<SerializationException> {
            preferences.encode("unit", Unit)
        }
    }

    @Test
    fun testPolymorphicSerialization() {
        preferences = Preferences(sharedPreferences) {
            serializersModule = interfaceModule
        }
        val data = listOf(InterfaceClassOne(a = 5), InterfaceClassTwo(b = 4))

        preferences.encode("interfaces", data)
        val actual = preferences.decode<List<AnInterface>>("interfaces")

        assertEquals(data, actual)
    }

    @Test
    fun testChangeSharedPreferences() {
        val otherSharedPreferences = TestablePreferences()
        val otherPreferences = Preferences(preferences) {
            sharedPreferences = otherSharedPreferences
        }
        preferences.encode("foo", 10)
        otherPreferences.encode("bar", 20)

        assertEquals(10, sharedPreferences.getInt("foo", 0))
        assertEquals(0, sharedPreferences.getInt("bar", 0))
        assertEquals(0, otherSharedPreferences.getInt("foo", 0))
        assertEquals(20, otherSharedPreferences.getInt("bar", 0))
    }

    @Test
    fun testNoPreferenceAfterDeletion() {
        val data = SimpleContainer(5)
        preferences.encode("foo", data)
        preferences.encode<SimpleContainer?>("foo", null)
        val actual = preferences.decode<SimpleContainer?>("foo")

        assertNull(actual)
        assertEquals(0, sharedPreferences.getInt("foo.bar", 0))
    }

    @Test
    fun testNoPreferenceAfterSavingSmallerList() {
        val longData = listOf(1, 2, 3, 4, 5)
        preferences.encode("list", longData)
        val data = listOf(6, 7)
        preferences.encode("list", data)
        val actual = preferences.decode<List<Int>>("list")

        assertEquals(data, actual)
    }

    @Test
    fun testListOfEnums() {
        val data = listOf(Weekday.TUESDAY, Weekday.SATURDAY, Weekday.THURSDAY, Weekday.FRIDAY)
        preferences.encode("enums", data)
        val actual = preferences.decode<List<Weekday>>("enums")

        assertEquals(data, actual)
    }

    @Test
    fun testNativeStringSet() {
        val data = setOf("foo", "bar")
        preferences.encode("set", data)
        val actual = preferences.decode<Set<String>>("set")

        assertEquals(data, actual)
        assertEquals(setOf("set"), sharedPreferences.all.keys)
        assertEquals(data, sharedPreferences.getStringSet("set", null))
    }

    @Test
    fun testNativeStringSetThrowOnNotFound() {
        assertFailsWith<SerializationException> {
            preferences.decode<Set<String>>("set")
        }
    }

    @Test
    fun testNativeStringSetWithNull() {
        val data = setOf("foo", null, "bar")
        preferences.encode("set", data)
        val actual = preferences.decode<Set<String?>>("set")

        assertEquals(data, actual)
        assertEquals(setOf("set"), sharedPreferences.all.keys)
        assertEquals(data, sharedPreferences.getStringSet("set", null))
    }

    @Test
    fun testNonNativeStringSet() {
        val preferences = Preferences(preferences) {
            encodeStringSetNatively = false
        }
        val data = setOf("foo", "bar")
        preferences.encode("set", data)
        val actual = preferences.decode<Set<String>>("set")

        assertEquals(data, actual)
        assertEquals(setOf("set.0", "set.1"), sharedPreferences.all.keys)
    }

    @Test
    fun testNativeCharSet() {
        val data = setOf('a', 'b', 'c')
        preferences.encode("set", data)
        val actual = preferences.decode<Set<Char>>("set")

        assertEquals(data, actual)
        assertEquals(setOf("set"), sharedPreferences.all.keys)
        assertEquals(data.mapTo(mutableSetOf(), Char::toString), sharedPreferences.getStringSet("set", null))
    }

    @Test
    fun testNonNativeCharSet() {
        val preferences = Preferences(preferences) {
            encodeStringSetNatively = false
        }
        val data = setOf('a', 'b', 'c')
        preferences.encode("set", data)
        val actual = preferences.decode<Set<Char>>("set")

        assertEquals(data, actual)
        assertEquals(setOf("set.0", "set.1", "set.2"), sharedPreferences.all.keys)
    }

    @Test
    fun testNativeEnumSet() {
        val data = setOf(Weekday.THURSDAY, Weekday.MONDAY)
        preferences.encode("set", data)
        val actual = preferences.decode<Set<Weekday>>("set")

        assertEquals(data, actual)
        assertEquals(setOf("set"), sharedPreferences.all.keys)
        assertEquals(data.mapTo(mutableSetOf(), Weekday::name), sharedPreferences.getStringSet("set", null))
    }

    @Test
    fun testNonNativeIntSet() {
        val data = setOf(1, 2, 3)
        preferences.encode("set", data)
        val actual = preferences.decode<Set<Int>>("set")

        assertEquals(data, actual)
        assertEquals(setOf("set.0", "set.1", "set.2"), sharedPreferences.all.keys)
    }

    @Test
    fun testNonNativeEnumSet() {
        val preferences = Preferences(preferences) {
            encodeStringSetNatively = false
        }
        val data = setOf(Weekday.THURSDAY, Weekday.MONDAY)
        preferences.encode("set", data)
        val actual = preferences.decode<Set<Weekday>>("set")

        assertEquals(data, actual)
        assertEquals(setOf("set.0", "set.1"), sharedPreferences.all.keys)
    }

    @Test
    fun testWrappedStringSet() {
        val data = listOf(setOf("1", "2"), setOf("foo", null, "bar"))

        preferences.encode("wrapper", data)
        val actual = preferences.decode<List<Set<String?>>>("wrapper")

        assertEquals(data, actual)
        assertEquals(setOf("wrapper.0", "wrapper.1"), sharedPreferences.all.keys)
    }

    @Test
    fun testNativeOnlyListDescriptor() {
        preferences = Preferences(preferences) {
            stringSetDescriptorNames.clear()
            stringSetDescriptorNames += "kotlin.collections.ArrayList"
        }
        val data = StringSetWrapper(setOf("1", "2"), listOf("foo", "bar"))

        preferences.encode("wrapper", data)
        val actual = preferences.decode<StringSetWrapper>("wrapper")

        assertEquals(data, actual)
        assertEquals(
            setOf("wrapper.kotlinSet.0", "wrapper.kotlinSet.1", "wrapper.customSet"),
            sharedPreferences.all.keys
        )
    }
}
