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
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.NamedValueEncoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Suppress("TooManyFunctions")
internal class PreferenceEncoder(
    private val preferences: Preferences,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
) : NamedValueEncoder() {

    override val serializersModule: SerializersModule = preferences.serializersModule

    internal fun pushInitialTag(name: String) {
        val tag = nested(name)
        pushTag(tag)
        editor.remove(tag)
        sharedPreferences.all.keys
            .filter { it.startsWith("$tag.") }
            .forEach { editor.remove(it) }
    }

    override fun encodeTaggedNull(tag: String) {
        // null is supported, so do not throw an exception
    }

    override fun encodeTaggedEnum(tag: String, enumDescriptor: SerialDescriptor, ordinal: Int) {
        editor.putString(tag, enumDescriptor.getElementName(ordinal))
    }

    override fun encodeTaggedBoolean(tag: String, value: Boolean) {
        editor.putBoolean(tag, value)
    }

    override fun encodeTaggedByte(tag: String, value: Byte) {
        editor.putInt(tag, value.toInt())
    }

    override fun encodeTaggedShort(tag: String, value: Short) {
        editor.putInt(tag, value.toInt())
    }

    override fun encodeTaggedInt(tag: String, value: Int) {
        editor.putInt(tag, value)
    }

    override fun encodeTaggedLong(tag: String, value: Long) {
        editor.putLong(tag, value)
    }

    override fun encodeTaggedFloat(tag: String, value: Float) {
        editor.putFloat(tag, value)
    }

    override fun encodeTaggedDouble(tag: String, value: Double) {
        when (preferences.conf.doubleRepresentation) {
            DoubleRepresentation.FLOAT -> encodeTaggedFloat(tag, value.toFloat())
            DoubleRepresentation.LONG_BITS -> encodeTaggedLong(tag, value.toBits())
            DoubleRepresentation.STRING -> encodeTaggedString(tag, value.toString())
        }
    }

    override fun encodeTaggedChar(tag: String, value: Char) {
        encodeTaggedString(tag, value.toString())
    }

    override fun encodeTaggedString(tag: String, value: String) {
        editor.putString(tag, value)
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        if (collectionSize == 0) {
            encodeEmptyStructureStart(descriptor)
        }
        return super.beginCollection(descriptor, collectionSize)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (descriptor.elementsCount == 0 && descriptor.kind !is PrimitiveKind) {
            encodeEmptyStructureStart(descriptor)
        }
        return super.beginStructure(descriptor)
    }

    private fun encodeEmptyStructureStart(descriptor: SerialDescriptor) {
        if (preferences.conf.encodeObjectStarts) {
            editor.putBoolean(currentTag, true)
        } else {
            throw SerializationException(
                "cannot encode empty structure ${descriptor.serialName} at $currentTag " +
                    "(use encodeObjectStarts=true on Preferences creation to change this behavior)"
            )
        }
    }
}
