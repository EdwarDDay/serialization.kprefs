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

package it.edwardday.serialization.preferences.encoding

import android.content.SharedPreferences
import it.edwardday.serialization.preferences.DoubleRepresentation
import it.edwardday.serialization.preferences.Preferences
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Suppress("TooManyFunctions")
internal class PreferenceDecoder(
    private val preferences: Preferences,
    descriptor: SerialDescriptor
) : NamedValueDecoder() {

    override val serializersModule: SerializersModule = preferences.conf.serializersModule

    private val sharedPreferences: SharedPreferences get() = preferences.conf.sharedPreferences

    private var currentIndex = 0
    private val isCollection = descriptor.kind == StructureKind.LIST || descriptor.kind == StructureKind.MAP
    private val size = if (isCollection) Int.MAX_VALUE else descriptor.elementsCount

    internal fun pushInitialTag(name: String) {
        pushTag(nested(name))
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return PreferenceDecoder(preferences, descriptor).also { copyTagsTo(it) }
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (currentIndex < size) {
            val name = descriptor.getTag(currentIndex)
            val childDescriptor = descriptor.getElementDescriptor(currentIndex++)
            if (
                name in sharedPreferences || // found key
                sharedPreferences.all.any { it.key.startsWith("$name.") } || // found key of child
                childDescriptor.isNullable // doesn't encode null, so could be null
            )
                return currentIndex - 1
            if (isCollection) {
                // if map does not contain key we look for, then indices in collection have ended
                break
            }
        }
        return CompositeDecoder.DECODE_DONE
    }

    override fun decodeTaggedEnum(tag: String, enumDescriptor: SerialDescriptor): Int {
        checkTagIsStored(tag)
        return try {
            val value = sharedPreferences.getString(tag, null)
                ?: throw SerializationException("Value '$tag' is not stored")
            enumDescriptor.getElementIndex(value)
        } catch (_: ClassCastException) {
            try {
                sharedPreferences.getInt(tag, -1).takeIf { it >= 0 }
                    ?: throw SerializationException("Value '$tag' is not stored")
            } catch (_: ClassCastException) {
                throw SerializationException("Value of enum entry '$tag' is neither an Int, nor a String")
            }
        }
    }

    override fun decodeTaggedNotNullMark(tag: String): Boolean =
        tag in sharedPreferences || sharedPreferences.all.any { it.key.startsWith("$tag.") }

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

    override fun decodeTaggedDouble(tag: String): Double =
        when (preferences.conf.doubleRepresentation) {
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
