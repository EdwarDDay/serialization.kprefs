// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.Context
import android.content.SharedPreferences
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.andNull
import io.kotest.property.exhaustive.enum
import io.kotest.property.resolution.default
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class EncodingTest {

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
    fun testPrimitivesContainer() = runTest {
        checkAll(Arb.default<SimplePrimitivesContainer>()) { expected ->
            preferences.encode("container", expected)
            val actual = preferences.decode<SimplePrimitivesContainer>("container")

            assertEquals(expected, actual)
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
                    "container.i",
                ),
                actual = sharedPreferences.all.keys,
            )
            assertEquals(expected.a, sharedPreferences.getBoolean("container.a", !expected.a))
            assertEquals(expected.b.toInt(), sharedPreferences.getInt("container.b", 0))
            assertEquals(expected.c.toInt(), sharedPreferences.getInt("container.c", 0))
            assertEquals(expected.d, sharedPreferences.getInt("container.d", 0))
            assertEquals(expected.e, sharedPreferences.getLong("container.e", 0))
            assertEquals(expected.f, sharedPreferences.getFloat("container.f", 0f))
            assertEquals(expected.g, Double.fromBits(sharedPreferences.getLong("container.g", 0)))
            assertEquals(expected.h.toString(), sharedPreferences.getString("container.h", null))
            assertEquals(expected.i, sharedPreferences.getString("container.i", null))
        }
    }

    @Test
    fun testComplexData() = runTest {
        checkAll(Arb.default<Complex>()) { expected ->
            preferences.encode("complex", expected)
            val actual = preferences.decode<Complex>("complex")

            assertEquals(expected, actual)
            assertEquals(
                expected = setOf(
                    "complex.simple.bar",
                    "complex.optional.foo",
                ),
                actual = sharedPreferences.all.keys,
            )
            assertEquals(expected.simple.bar, sharedPreferences.getInt("complex.simple.bar", 0))
            assertEquals(expected.optional.foo, sharedPreferences.getString("complex.optional.foo", null))
        }
    }

    @Test
    fun testNonNullDecoding() = runTest {
        checkAll(Arb.default<Complex>().orNull(0.01)) { expected ->
            preferences.encode("complex", expected)
            val actual = preferences.decode<Complex?>("complex")

            assertEquals(expected, actual)
            if (expected == null) {
                assertEquals(setOf("complex.\$isNotNull"), sharedPreferences.all.keys)
                assertFalse(sharedPreferences.getBoolean("complex.\$isNotNull", true))
            } else {
                val expectedKeys = setOf("complex.\$isNotNull", "complex.simple.bar", "complex.optional.foo")
                assertEquals(expectedKeys, sharedPreferences.all.keys)
                assertTrue(sharedPreferences.getBoolean("complex.\$isNotNull", false))
                assertEquals(expected.simple.bar, sharedPreferences.getInt("complex.simple.bar", 0))
                assertEquals(expected.optional.foo, sharedPreferences.getString("complex.optional.foo", null))
            }
            sharedPreferences.edit().clear().apply()
        }
    }

    @Test
    fun testList() = runTest {
        checkAll(Arb.list(Arb.default<SimpleContainer>(), range = 1..100)) { expected ->
            preferences.encode("list", expected)
            val actual = preferences.decode<List<SimpleContainer>>("list")

            assertEquals(expected, actual)
            val expectedKeys = expected.indices.mapTo(mutableSetOf()) { "list.$it.bar" }
            assertEquals(expectedKeys, sharedPreferences.all.keys)
            expected.forEachIndexed { index, value ->
                assertEquals(value.bar, sharedPreferences.getInt("list.$index.bar", 0))
            }
        }
    }

    @Test
    fun testNullableList() = runTest {
        checkAll(Arb.list(Arb.default<SimpleContainer>(), range = 1..100).orNull(0.01)) { expected ->
            preferences.encode("list", expected)
            val actual = preferences.decode<List<SimpleContainer>?>("list")

            assertEquals(expected, actual)
            val expectedKeys = expected?.indices?.map { "list.$it.bar" }.orEmpty().toSet() + "list.\$isNotNull"
            assertEquals(expectedKeys, sharedPreferences.all.keys)
            if (expected != null) {
                assertTrue(sharedPreferences.getBoolean("list.\$isNotNull", false))
                expected.forEachIndexed { index, value ->
                    assertEquals(value.bar, sharedPreferences.getInt("list.$index.bar", 0))
                }
            } else {
                assertFalse(sharedPreferences.getBoolean("list.\$isNotNull", true))
            }
        }
    }

    @Test
    fun testNullablePrimitiveList() = runTest {
        checkAll(Arb.list(Arb.int(min = 1).orNull(0.01), range = 1..100).orNull(0.01)) { expected ->
            preferences.encode("list", expected)
            val actual = preferences.decode<List<Int?>?>("list")

            assertEquals(expected, actual)
            val expectedKeys = expected?.flatMapIndexed { index, element ->
                buildList {
                    if (element != null) add("list.$index")
                    add("list.$index.\$isNotNull")
                }
            }.orEmpty().toSet() + "list.\$isNotNull"
            assertEquals(expectedKeys, sharedPreferences.all.keys)
            if (expected != null) {
                assertTrue(sharedPreferences.getBoolean("list.\$isNotNull", false))
                expected.forEachIndexed { index, value ->
                    if (value != null) {
                        assertTrue(sharedPreferences.getBoolean("list.$index.\$isNotNull", false))
                        assertEquals(value, sharedPreferences.getInt("list.$index", 0))
                    } else {
                        assertFalse(sharedPreferences.getBoolean("list.$index.\$isNotNull", true))
                    }
                }
            } else {
                assertFalse(sharedPreferences.getBoolean("list.\$isNotNull", true))
            }
        }
    }

    @Test
    fun testEmptyList() {
        preferences.encode<List<SimpleContainer>>("list", emptyList())
        val actual = preferences.decode<List<SimpleContainer>>("list")

        assertEquals(emptyList(), actual)
        assertEquals(
            expected = setOf("list"),
            actual = sharedPreferences.all.keys,
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
            actual = sharedPreferences.all.keys,
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
    fun testMap() = runTest {
        checkAll(Arb.map(Arb.string(), Arb.default<SimpleContainer>())) { expected ->
            preferences.encode("map", expected)
            val actual = preferences.decode<Map<String, SimpleContainer>>("map")

            assertEquals(expected, actual)
            val expectedKeys = expected.entries.indices.flatMapTo(mutableSetOf()) { entryIndex ->
                val keyIndex = entryIndex * 2
                val valueIndex = keyIndex + 1
                listOf("map.$keyIndex", "map.$valueIndex.bar")
            }
            assertEquals(expectedKeys, sharedPreferences.all.keys)
            expected.entries.forEachIndexed { entryIndex, (key, value) ->
                val keyIndex = entryIndex * 2
                val valueIndex = keyIndex + 1
                assertEquals(key, sharedPreferences.getString("map.$keyIndex", null))
                assertEquals(value.bar, sharedPreferences.getInt("map.$valueIndex.bar", 0))
            }
        }
    }

    @Test
    fun testEmptyMap() {
        preferences.encode<Map<String, SimpleContainer>>("map", emptyMap())
        val actual = preferences.decode<Map<String, SimpleContainer>>("map")

        assertEquals(emptyMap(), actual)
        assertEquals(
            expected = setOf("map"),
            actual = sharedPreferences.all.keys,
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
    fun testComplexMap() = runTest {
        checkAll(Arb.map(Arb.default<SimpleContainer>(), Arb.default<DateWithOptional>())) { expected ->
            preferences.encode("map", expected)
            val actual = preferences.decode<Map<SimpleContainer, DateWithOptional>>("map")

            assertEquals(expected, actual)
            val expectedKeys = expected.entries.indices.flatMapTo(mutableSetOf()) { entryIndex ->
                val keyIndex = entryIndex * 2
                val valueIndex = keyIndex + 1
                listOf("map.$keyIndex.bar", "map.$valueIndex.foo")
            }
            assertEquals(expectedKeys, sharedPreferences.all.keys)
            expected.entries.forEachIndexed { entryIndex, (key, value) ->
                val keyIndex = entryIndex * 2
                val valueIndex = keyIndex + 1
                assertEquals(key.bar, sharedPreferences.getInt("map.$keyIndex.bar", 0))
                assertEquals(value.foo, sharedPreferences.getString("map.$valueIndex.foo", null))
            }
        }
    }

    @Test
    fun testNestedMap() = runTest {
        checkAll(
            iterations = 100,
            genA = Arb.map(
                keyArb = Arb.string(),
                valueArb = Arb.list(
                    gen = Arb.map(
                        keyArb = Arb.string(),
                        valueArb = Arb.map(keyArb = Arb.string(), valueArb = Arb.int(), maxSize = 10),
                        maxSize = 10,
                    ),
                    range = 1..10,
                ),
                maxSize = 10,
            ),
        ) { expected ->
            preferences.encode("nestedMap", expected)
            val actual = preferences.decode<Map<String, List<Map<String, Map<String, Int>>>>>("nestedMap")

            assertEquals(expected, actual)
        }
    }

    private fun Arb.Companion.sealedDate(): Arb<SealedDate> = arbitrary {
        when (int(0..2).bind()) {
            0 -> SealedDate.Sealed1(boolean().bind())
            1 -> SealedDate.Sealed2(int().bind())
            else -> SealedDate.Sealed3
        }
    }

    @Test
    fun testSealedClass() = runTest {
        checkAll(Arb.sealedDate()) { expected ->
            preferences.encode("sealed", listOf(expected))
            val actual = preferences.decode<List<SealedDate>>("sealed")

            assertEquals(listOf(expected), actual)
            when (expected) {
                is SealedDate.Sealed1 -> {
                    assertEquals(setOf("sealed.0.type", "sealed.0.value.a"), sharedPreferences.all.keys)
                    assertEquals("sealed1", sharedPreferences.getString("sealed.0.type", null))
                    assertEquals(expected.a, sharedPreferences.getBoolean("sealed.0.value.a", !expected.a))
                }
                is SealedDate.Sealed2 -> {
                    assertEquals(setOf("sealed.0.type", "sealed.0.value.b"), sharedPreferences.all.keys)
                    assertEquals("sealed2", sharedPreferences.getString("sealed.0.type", null))
                    assertEquals(expected.b, sharedPreferences.getInt("sealed.0.value.b", 0))
                }
                SealedDate.Sealed3 -> {
                    assertEquals(setOf("sealed.0.type", "sealed.0.value"), sharedPreferences.all.keys)
                    assertEquals("sealed3", sharedPreferences.getString("sealed.0.type", null))
                    assertTrue(sharedPreferences.getBoolean("sealed.0.value", false))
                }
            }
        }
    }

    @Test
    fun testUseOptionalData() {
        sharedPreferences.edit().putString("optional", "unused").apply()

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

    private fun Arb.Companion.anInterface(): Arb<AnInterface> = arbitrary {
        if (boolean().bind()) {
            InterfaceClassOne(int().bind())
        } else {
            InterfaceClassTwo(byte().bind())
        }
    }

    @Test
    fun testPolymorphicSerialization() = runTest {
        preferences = Preferences(sharedPreferences) {
            serializersModule = interfaceModule
        }
        checkAll(Arb.anInterface()) { expected ->
            preferences.encode("interfaces", listOf(expected))
            val actual = preferences.decode<List<AnInterface>>("interfaces")

            assertEquals(listOf(expected), actual)
        }
    }

    @Test
    fun testChangeSharedPreferences() {
        val otherSharedPreferences = createContext().getSharedPreferences("other_preferences", Context.MODE_PRIVATE)
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
    fun testListOfEnums() = runTest {
        checkAll(Arb.list(Arb.enum<Weekday>(), range = 1..10)) { expected ->
            preferences.encode("enums", expected)
            val actual = preferences.decode<List<Weekday>>("enums")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun testNativeStringSet() = runTest {
        checkAll(Arb.set(Arb.string().orNull(0.1), range = 1..10)) { expected ->
            preferences.encode("set", expected)
            val actual = preferences.decode<Set<String?>>("set")

            assertEquals(expected, actual)
            assertEquals(setOf("set"), sharedPreferences.all.keys)
            assertEquals(expected, sharedPreferences.getStringSet("set", null))
        }
    }

    @Test
    fun testNativeStringSetThrowOnNotFound() {
        assertFailsWith<SerializationException> {
            preferences.decode<Set<String>>("set")
        }
    }

    @Test
    fun testNonNativeStringSet() = runTest {
        val preferences = Preferences(preferences) {
            encodeStringSetNatively = false
        }
        checkAll(Arb.set(Arb.string().orNull(0.1), range = 1..10)) { expected ->
            preferences.encode("set", expected)
            val actual = preferences.decode<Set<String?>>("set")

            assertEquals(expected, actual)
            val expectedKeys = expected.extractKeys("set")
            assertEquals(expectedKeys, sharedPreferences.all.keys)
        }
    }

    @Test
    fun testNativeCharSet() = runTest {
        checkAll(Arb.set(Arb.char().orNull(0.1), range = 1..10)) { expected ->
            preferences.encode("set", expected)
            val actual = preferences.decode<Set<Char?>>("set")

            assertEquals(expected, actual)
            assertEquals(setOf("set"), sharedPreferences.all.keys)
            assertEquals(expected.mapTo(mutableSetOf()) { it?.toString() }, sharedPreferences.getStringSet("set", null))
        }
    }

    @Test
    fun testNonNativeCharSet() = runTest {
        val preferences = Preferences(preferences) {
            encodeStringSetNatively = false
        }
        checkAll(Arb.set(Arb.char().orNull(0.1), range = 1..10)) { expected ->
            preferences.encode("set", expected)
            val actual = preferences.decode<Set<Char?>>("set")

            assertEquals(expected, actual)
            val expectedKeys = expected.extractKeys("set")
            assertEquals(expectedKeys, sharedPreferences.all.keys)
        }
    }

    @Test
    fun testNativeEnumSet() = runTest {
        checkAll(Arb.set(Exhaustive.enum<Weekday>().andNull(), range = 1..8)) { expected ->
            preferences.encode("set", expected)
            val actual = preferences.decode<Set<Weekday?>>("set")

            assertEquals(expected, actual)
            assertEquals(setOf("set"), sharedPreferences.all.keys)
            assertEquals(expected.mapTo(mutableSetOf()) { it?.name }, sharedPreferences.getStringSet("set", null))
        }
    }

    @Test
    fun testNonNativeIntSet() = runTest {
        checkAll(Arb.set(Arb.int().orNull(0.1), range = 1..10)) { expected ->
            preferences.encode("set", expected)
            val actual = preferences.decode<Set<Int?>>("set")

            assertEquals(expected, actual)
            val expectedKeys = expected.extractKeys("set")
            assertEquals(expectedKeys, sharedPreferences.all.keys)
        }
    }

    @Test
    fun testNonNativeEnumSet() = runTest {
        val preferences = Preferences(preferences) {
            encodeStringSetNatively = false
        }
        checkAll(Arb.set(Arb.enum<Weekday>().orNull(0.1), range = 1..8)) { expected ->
            preferences.encode("set", expected)
            val actual = preferences.decode<Set<Weekday?>>("set")

            assertEquals(expected, actual)
            val expectedKeys = expected.extractKeys("set")
            assertEquals(expectedKeys, sharedPreferences.all.keys)
        }
    }

    @Test
    fun testWrappedStringSet() = runTest {
        checkAll(Arb.list(Arb.set(Arb.string().orNull(0.1), range = 1..10), range = 1..4)) { expected ->
            preferences.encode("wrapper", expected)
            val actual = preferences.decode<List<Set<String?>>>("wrapper")

            assertEquals(expected, actual)
            val expectedKeys = List(expected.size) { "wrapper.$it" }.toSet()
            assertEquals(expectedKeys, sharedPreferences.all.keys)
        }
    }

    @Test
    fun testNativeOnlyListDescriptor() = runTest {
        preferences = Preferences(preferences) {
            stringSetDescriptorNames.clear()
            stringSetDescriptorNames += "kotlin.collections.ArrayList"
        }
        checkAll(Arb.default<StringSetWrapper>().filter { it.kotlinSet.isNotEmpty() }) { expected ->
            preferences.encode("wrapper", expected)
            val actual = preferences.decode<StringSetWrapper>("wrapper")

            assertEquals(expected.kotlinSet, actual.kotlinSet)
            assertEquals(expected.customSet.toSet(), actual.customSet.toSet())
            val expectedKeys = List(expected.kotlinSet.size) { "wrapper.kotlinSet.$it" } + "wrapper.customSet"
            assertEquals(expectedKeys.toSet(), sharedPreferences.all.keys)
        }
    }

    @Test
    fun testUseDefault() = runTest {
        checkAll(Arb.default<SimpleContainer>()) { expected ->
            val actual = preferences.decodeOrDefault("foo", expected)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun testUseValueAndNotDefault() = runTest {
        checkAll(Arb.default<SimpleContainer>(), Arb.default<SimpleContainer>()) { expected, default ->
            sharedPreferences.edit().putInt("foo.bar", expected.bar).apply()

            val actual = preferences.decodeOrDefault("foo", default)

            assertEquals(expected, actual)
        }
    }

    private fun Iterable<Any?>.extractKeys(root: String): Set<String> =
        flatMapIndexedTo(mutableSetOf()) { index, element ->
            buildList {
                add("$root.$index.\$isNotNull")
                if (element != null) {
                    add("$root.$index")
                }
            }
        }
}
