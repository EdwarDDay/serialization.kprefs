// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences.encoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import net.edwardday.serialization.preferences.PreferenceConf

@OptIn(ExperimentalSerializationApi::class)
internal fun PreferenceConf.shouldSerializeStringSet(descriptor: SerialDescriptor): Boolean {
    return if (
        encodeStringSetNatively &&
        descriptor.kind === StructureKind.LIST &&
        descriptor.serialName in stringSetDescriptorNames
    ) {
        val elementKind = descriptor.getElementDescriptor(0).kind
        elementKind === PrimitiveKind.STRING || elementKind === PrimitiveKind.CHAR || elementKind === SerialKind.ENUM
    } else {
        false
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.getElementIndexOrThrow(name: String): Int {
    val foundIndex = getElementIndex(name)
    if (foundIndex != CompositeDecoder.UNKNOWN_NAME) {
        return foundIndex
    } else {
        throw SerializationException("Value of enum entry in has unknown value $name")
    }
}
