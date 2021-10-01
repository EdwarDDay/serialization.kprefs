// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

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
 *
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
public sealed class Preferences(internal val configuration: PreferenceConfiguration) : SerialFormat {

    /**
     * Contains all serializers registered by format user for [Contextual] and [Polymorphic] serialization.
     *
     * @see SerialFormat.serializersModule
     */
    override val serializersModule: SerializersModule
        get() = configuration.serializersModule

    /**
     * Serializes and encodes the given [value] into the [SharedPreferences] at the specified [tag] using the given
     * [serializer].
     *
     * @param serializer strategy used to encode the data
     * @param tag key to encode data to
     * @param value value to encode
     */
    public fun <T> encode(serializer: SerializationStrategy<T>, tag: String, value: T) {
        val editor = configuration.sharedPreferences.edit()
        val encoder = PreferenceEncoder(this, editor, configuration.sharedPreferences)
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
    builderAction: PreferencesBuilder.() -> Unit = {},
): Preferences = generatePreferences(PreferenceConfiguration(sharedPreferences), builderAction)

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
    builderAction: PreferencesBuilder.() -> Unit = {},
): Preferences = generatePreferences(preferences.configuration, builderAction)

private inline fun generatePreferences(
    preferenceConfiguration: PreferenceConfiguration,
    builderAction: PreferencesBuilder.() -> Unit,
): Preferences {
    val builder = PreferencesBuilder(preferenceConfiguration)
    builder.builderAction()
    val conf = builder.build()
    return PreferencesImpl(conf)
}

/**
 * Builder of the [Preferences] instance provided by `Preferences(sharedPreferences) { ... }` factory function.
 */
public class PreferencesBuilder internal constructor(configuration: PreferenceConfiguration) {
    private val previousStringSetDescriptorNames = configuration.stringSetDescriptorNames

    /**
     * Specifies the [SharedPreferences] where everything will be encoded to and decoded from.
     */
    public var sharedPreferences: SharedPreferences = configuration.sharedPreferences

    /**
     * Specifies how [Double] fields will be encoded.
     * [DoubleRepresentation.LONG_BITS] by default
     */
    public var doubleRepresentation: DoubleRepresentation = configuration.doubleRepresentation

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
    public var encodeObjectStarts: Boolean = configuration.encodeObjectStarts

    /**
     * Specifies whether [Set]s of [String], [Char] and [Enum] will be encoded with
     * [putStringSet][SharedPreferences.Editor.putStringSet] or not.
     *
     * `true` by default
     */
    public var encodeStringSetNatively: Boolean = configuration.encodeStringSetNatively

    /**
     * Specifies the names of the [SerialDescriptor] which are used to detect Set<String> to encode these natively.
     *
     * `true` by default
     */
    public val stringSetDescriptorNames: MutableList<String> = configuration.stringSetDescriptorNames.toMutableList()

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [Preferences] instance.
     */
    public var serializersModule: SerializersModule = configuration.serializersModule

    internal fun build(): PreferenceConfiguration {
        if (stringSetDescriptorNames != previousStringSetDescriptorNames) {
            require(encodeStringSetNatively) {
                "stringSetDescriptorNames is only used when encodeStringSetNatively is enabled"
            }
        }
        return PreferenceConfiguration(
            sharedPreferences = sharedPreferences,
            serializersModule = serializersModule,
            doubleRepresentation = doubleRepresentation,
            encodeObjectStarts = encodeObjectStarts,
            encodeStringSetNatively = encodeStringSetNatively,
            stringSetDescriptorNames = stringSetDescriptorNames,
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

private class PreferencesImpl(configuration: PreferenceConfiguration) : Preferences(configuration)

@OptIn(ExperimentalSerializationApi::class)
internal data class PreferenceConfiguration(
    val sharedPreferences: SharedPreferences,
    val serializersModule: SerializersModule = EmptySerializersModule,
    val doubleRepresentation: DoubleRepresentation = DoubleRepresentation.LONG_BITS,
    val encodeObjectStarts: Boolean = true,
    val encodeStringSetNatively: Boolean = true,
    val stringSetDescriptorNames: List<String> =
        listOf("kotlin.collections.HashSet", "kotlin.collections.LinkedHashSet"),
)
