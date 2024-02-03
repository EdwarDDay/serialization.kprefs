// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences.encoding

import android.content.SharedPreferences
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.modules.SerializersModule
import net.edwardday.serialization.preferences.DoubleRepresentation
import net.edwardday.serialization.preferences.Preferences

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Suppress("TooManyFunctions")
internal class PreferenceDecoder(
    private val preferences: Preferences,
    descriptor: SerialDescriptor,
) : NamedValueDecoder() {

    override val serializersModule: SerializersModule = preferences.configuration.serializersModule

    private val sharedPreferences: SharedPreferences get() = preferences.configuration.sharedPreferences

    private var currentIndex = 0
    private val isCollection = descriptor.kind == StructureKind.LIST || descriptor.kind == StructureKind.MAP
    private val size: Int by lazy(LazyThreadSafetyMode.NONE) {
        if (isCollection) collectionSize() else descriptor.elementsCount
    }

    private fun collectionSize(): Int {
        val currentTag = composeName(currentTag, "")
        val indices = sharedPreferences.all.keys
            .filter { it.startsWith(currentTag) }
            .map { key -> key.drop(currentTag.length).takeWhile { it != '.' } }
        return indices.mapNotNull { it.toIntOrNull() }.maxOrNull()?.let { it + 1 } ?: 0
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = size

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (preferences.configuration.shouldSerializeStringSet(descriptor)) {
            val stringSet: Set<String?> = sharedPreferences.getStringSet(currentTag, null)
                ?: throw SerializationException("missing property $currentTag")
            return PreferencesStringSetDecoder(preferences, stringSet)
        }
        return PreferenceDecoder(preferences, descriptor).also { copyTagsTo(it) }
    }

    private fun couldBeInPreferences(tag: String): Boolean = tag in sharedPreferences || // found key
        sharedPreferences.all.any { it.key.startsWith("$tag.") } // found key of child

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (currentIndex < size) {
            val name = descriptor.getTag(currentIndex++)
            if (couldBeInPreferences(name)) {
                return currentIndex - 1
            }
            if (isCollection) {
                // if map does not contain key we look for, then indices in collection have ended
                break
            }
        }
        return CompositeDecoder.DECODE_DONE
    }

    override fun decodeTaggedEnum(tag: String, enumDescriptor: SerialDescriptor): Int {
        val value = decodeTaggedString(tag)
        return enumDescriptor.getElementIndexOrThrow(value)
    }

    override fun decodeTaggedNotNullMark(tag: String): Boolean {
        val notNullMarkTag = nested(NOT_NULL_MARK_TAG_NAME)
        return if (notNullMarkTag in sharedPreferences) {
            decodeTaggedBoolean(notNullMarkTag)
        } else {
            // still read old behavior
            couldBeInPreferences(tag)
        }
    }

    override fun decodeTaggedBoolean(tag: String): Boolean {
        checkTagIsStored(tag)
        return sharedPreferences.getBoolean(tag, false)
    }

    override fun decodeTaggedByte(tag: String): Byte = decodeTaggedInt(tag).toByte()
    override fun decodeTaggedShort(tag: String): Short = decodeTaggedInt(tag).toShort()
    override fun decodeTaggedInt(tag: String): Int {
        checkTagIsStored(tag)
        return sharedPreferences.getInt(tag, 0)
    }

    override fun decodeTaggedLong(tag: String): Long {
        checkTagIsStored(tag)
        return sharedPreferences.getLong(tag, 0)
    }

    override fun decodeTaggedFloat(tag: String): Float {
        checkTagIsStored(tag)
        return sharedPreferences.getFloat(tag, 0f)
    }

    override fun decodeTaggedDouble(tag: String): Double = when (preferences.configuration.doubleRepresentation) {
        DoubleRepresentation.FLOAT -> decodeTaggedFloat(tag).toDouble()
        DoubleRepresentation.LONG_BITS -> decodeTaggedLong(tag).let(Double.Companion::fromBits)
        DoubleRepresentation.STRING -> decodeTaggedString(tag).toDouble()
    }

    override fun decodeTaggedChar(tag: String): Char = decodeTaggedString(tag).first()

    override fun decodeTaggedString(tag: String): String {
        return sharedPreferences.getString(tag, null) ?: throw SerializationException("missing property $tag")
    }

    private fun checkTagIsStored(tag: String) {
        if (tag !in sharedPreferences) throw SerializationException("missing property $tag")
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal class PreferencesStringSetDecoder(
    private val preferences: Preferences,
    set: Set<String?>,
) : AbstractDecoder() {

    override val serializersModule: SerializersModule get() = preferences.serializersModule
    private val values = set.iterator()
    private val size = set.size
    private var currentIndex = -1
    private var useCachedValue = false
    private var cache: String? = null

    override fun decodeSequentially(): Boolean = true

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = size

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        if (values.hasNext()) ++currentIndex else CompositeDecoder.DECODE_DONE

    override fun decodeChar(): Char = decodeString().first()

    override fun decodeString(): String {
        return if (useCachedValue) {
            useCachedValue = false
            cache!!
        } else {
            values.next()!!
        }
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        enumDescriptor.getElementIndexOrThrow(decodeString())

    override fun decodeNotNullMark(): Boolean {
        useCachedValue = true
        cache = values.next()
        return cache != null
    }
}
