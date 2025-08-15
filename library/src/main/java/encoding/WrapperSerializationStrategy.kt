// SPDX-FileCopyrightText: 2020-2024 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences.encoding

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

private val NOT_SET = Any()

private fun buildWrapperDescriptor(descriptor: SerialDescriptor, tag: String, default: Any?): SerialDescriptor =
    buildClassSerialDescriptor(
        serialName = "net.edwardday.serialization.preferences.encoding.WrapperSerializer",
        typeParameters = arrayOf(descriptor),
    ) {
        element(tag, descriptor, isOptional = default !== NOT_SET)
    }

internal class WrapperSerializationStrategy<T>(
    private val serializer: SerializationStrategy<T>,
    tag: String,
    default: Any? = NOT_SET,
) : SerializationStrategy<SerializationWrapper<T>> {

    override val descriptor: SerialDescriptor = buildWrapperDescriptor(serializer.descriptor, tag, default)

    override fun serialize(encoder: Encoder, value: SerializationWrapper<T>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, serializer, value.value)
        }
    }
}

internal class WrapperDeserializationStrategy<T>(
    private val serializer: DeserializationStrategy<T>,
    tag: String,
    private val default: Any? = NOT_SET,
) : DeserializationStrategy<SerializationWrapper<T>> {

    override val descriptor: SerialDescriptor = buildWrapperDescriptor(serializer.descriptor, tag, default)

    override fun deserialize(decoder: Decoder): SerializationWrapper<T> = decoder.decodeStructure(descriptor) {
        var value: Any? = NOT_SET

        mainLoop@ while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> {
                    break@mainLoop
                }
                0 -> {
                    value = decodeSerializableElement(descriptor, index, serializer)
                }
                else -> throw SerializationException(
                    "Invalid index in wrapper deserialization. Expected 0 or DECODE_DONE(-1), but found $index",
                )
            }
        }
        @Suppress("UNCHECKED_CAST")
        val element = when {
            value !== NOT_SET -> value as T
            default !== NOT_SET -> default as T
            else -> throw SerializationException("Value has not been read")
        }
        SerializationWrapper(element)
    }
}

internal class SerializationWrapper<T>(val value: T)
