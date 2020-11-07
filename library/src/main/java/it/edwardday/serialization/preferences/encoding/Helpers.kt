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

import it.edwardday.serialization.preferences.PreferenceConf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder

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
