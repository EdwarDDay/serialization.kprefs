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
import kotlinx.serialization.Contextual
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.edwardday.serialization.preferences.encoding.PreferenceDecoder
import net.edwardday.serialization.preferences.encoding.PreferenceEncoder

/* Knit setup
<!--- INCLUDE .*-preferences-.*
import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*

class PreferencesTest {

    val sharedPreferences = TestablePreferences()

    @Test
    fun test() {
----- SUFFIX .*-preferences-.*
    }
}
-->
*/

/**
 * Serializes and deserializes class properties into [SharedPreferences] consisting of string keys and primitive type
 * values.
 *
 * ```kotlin
 * @Serializable
 * data class Person(val name: String, val age: Int)
 * val preferences = Preferences(sharedPreferences)
 * val abby = Person("Abby", 20)
 *
 * preferences.encode("person", abby)
 *
 * assertEquals("Abby", sharedPreferences.getString("person.name", null))
 * assertEquals(20, sharedPreferences.getInt("person.age", 0))
 * ```
 */
// <!--- KNIT example-preferences-01.kt -->
@OptIn(ExperimentalSerializationApi::class)
public sealed class Preferences(internal val conf: PreferenceConf) : SerialFormat {

    /**
     * Contains all serializers registered by format user for [Contextual] and [Polymorphic] serialization.
     *
     * @see SerialFormat.serializersModule
     */
    override val serializersModule: SerializersModule
        get() = conf.serializersModule

    /**
     * Serializes and encodes the given [value] into the [SharedPreferences] at the specified [tag] using the given
     * [serializer].
     *
     * @param serializer strategy used to encode the data
     * @param tag key to encode data to
     * @param value value to encode
     */
    public fun <T> encode(serializer: SerializationStrategy<T>, tag: String, value: T) {
        val editor = conf.sharedPreferences.edit()
        val encoder = PreferenceEncoder(this, editor, conf.sharedPreferences)
        encoder.pushInitialTag(tag)
        encoder.encodeSerializableValue(serializer, value)
        editor.apply()
    }

    /**
     * Decodes and deserializes from the [SharedPreferences] at the specified [tag] to the value of type [T] using the
     * given [deserializer]
     *
     * @param deserializer strategy used to decode the data
     * @param tag key to decode data from
     */
    public fun <T> decode(deserializer: DeserializationStrategy<T>, tag: String): T {
        val decoder = PreferenceDecoder(this, deserializer.descriptor)
        decoder.pushInitialTag(tag)
        return decoder.decodeSerializableValue(deserializer)
    }
}

/**
 * Serializes and encodes the given [value] into the [SharedPreferences] at the specified [tag] using serializer
 * retrieved from the reified type parameter.
 *
 * @param tag key to encode data to
 * @param value value to encode
 */
public inline fun <reified T> Preferences.encode(tag: String, value: T) {
    encode(serializersModule.serializer(), tag, value)
}

/**
 * Decodes and deserializes from the [SharedPreferences] at the specified [tag] to the value of type [T] using
 * deserializer retrieved from the reified type parameter.
 *
 * @param tag key to decode data from
 */
public inline fun <reified T> Preferences.decode(tag: String): T = decode(serializersModule.serializer(), tag)

/**
 * Creates an instance of [Preferences] encoding and decoding data from the given
 * [SharedPreferences][sharedPreferences] and adjusted with [builderAction].
 *
 * @param sharedPreferences the storage to encode data into and decode data from
 * @param builderAction builder to change the behavior of the [Preferences] format
 */
@Suppress("FunctionName")
public fun Preferences(
    sharedPreferences: SharedPreferences,
    builderAction: PreferencesBuilder.() -> Unit = {}
): Preferences = generatePreferences(PreferenceConf(sharedPreferences), builderAction)

/**
 * Creates an instance of [Preferences] using the configuration of the previous created
 * [Preferences][preferences] and adjusted with [builderAction].
 *
 * @param preferences format to copy the configuration from
 * @param builderAction builder to change the behavior of the [Preferences] format
 */
@Suppress("FunctionName")
public fun Preferences(
    preferences: Preferences,
    builderAction: PreferencesBuilder.() -> Unit = {}
): Preferences = generatePreferences(preferences.conf, builderAction)

private inline fun generatePreferences(
    preferenceConf: PreferenceConf,
    builderAction: PreferencesBuilder.() -> Unit
): Preferences {
    val builder = PreferencesBuilder(preferenceConf)
    builder.builderAction()
    val conf = builder.build()
    return PreferencesImpl(conf)
}

/**
 * Builder of the [Preferences] instance provided by `Preferences(sharedPreferences) { ... }` factory function.
 */
public class PreferencesBuilder internal constructor(conf: PreferenceConf) {
    private val previousStringSetDescriptorNames = conf.stringSetDescriptorNames

    /**
     * Specifies the [SharedPreferences] where everything will be encoded to and decoded from.
     */
    public var sharedPreferences: SharedPreferences = conf.sharedPreferences

    /**
     * Specifies how [Double] fields will be encoded.
     * [DoubleRepresentation.LONG_BITS] by default
     */
    public var doubleRepresentation: DoubleRepresentation = conf.doubleRepresentation

    /**
     * Specifies whether objects, empty classes and empty collections will be serialized by
     * encoding a marker at the position.
     *
     * ```kotlin
     * @Serializable
     * data class PrefTest(val u: Unit)
     *
     * val pref = Preferences(sharedPreferences) { encodeObjectStarts = true }
     * pref.encode(PrefTest.serializer(), "test", PrefTest(Unit))
     *
     * assertTrue(sharedPreferences.getBoolean("test.u", false))
     * ```
     *
     * `true` by default
     */
    // <!--- KNIT example-preferences-02.kt -->
    public var encodeObjectStarts: Boolean = conf.encodeObjectStarts

    /**
     * Specifies whether [Set]s of [String], [Char] and [Enum] will be encoded with
     * [putStringSet][SharedPreferences.Editor.putStringSet] or not.
     *
     * `true` by default
     */
    public var encodeStringSetNatively: Boolean = conf.encodeStringSetNatively

    /**
     * Specifies the names of the [SerialDescriptor] which are used to detect Set<String> to encode these natively.
     *
     * `true` by default
     */
    public val stringSetDescriptorNames: MutableList<String> = conf.stringSetDescriptorNames.toMutableList()

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [Preferences] instance.
     */
    public var serializersModule: SerializersModule = conf.serializersModule

    @OptIn(ExperimentalSerializationApi::class)
    internal fun build(): PreferenceConf {
        if (stringSetDescriptorNames != previousStringSetDescriptorNames) {
            require(encodeStringSetNatively) {
                "stringSetDescriptorNames is only used when encodeStringSetNatively is enabled"
            }
        }
        return PreferenceConf(
            sharedPreferences = sharedPreferences,
            serializersModule = serializersModule,
            doubleRepresentation = doubleRepresentation,
            encodeObjectStarts = encodeObjectStarts,
            encodeStringSetNatively = encodeStringSetNatively,
            stringSetDescriptorNames = stringSetDescriptorNames
        )
    }
}

/**
 * Representation possibilities for [Double], because [SharedPreferences] don't have `getDouble` or `putDouble` methods.
 */
public enum class DoubleRepresentation {

    /**
     * [Double] will be encoded as and decoded from [Float]. Note, that precision will be lost.
     */
    FLOAT,

    /**
     * [Double] will be encoded as and decoded from [Long] using [Double.toBits] and
     * [Double.Companion.fromBits]
     */
    LONG_BITS,

    /**
     * [Double] will be encoded as and decoded from [String] using [Double.toString] and
     * [String.toDouble]
     */
    STRING;
}

private class PreferencesImpl(conf: PreferenceConf) : Preferences(conf)

@OptIn(ExperimentalSerializationApi::class)
internal data class PreferenceConf(
    val sharedPreferences: SharedPreferences,
    val serializersModule: SerializersModule = EmptySerializersModule,
    val doubleRepresentation: DoubleRepresentation = DoubleRepresentation.LONG_BITS,
    val encodeObjectStarts: Boolean = true,
    val encodeStringSetNatively: Boolean = true,
    val stringSetDescriptorNames: List<String> =
        listOf("kotlin.collections.HashSet", "kotlin.collections.LinkedHashSet")
)
