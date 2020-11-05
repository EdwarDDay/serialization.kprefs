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

package it.edwardday.serialization.preferences

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
data class SimplePrimitivesContainer(
    val a: Boolean,
    val b: Byte,
    val c: Short,
    val d: Int,
    val e: Long,
    val f: Float,
    val g: Double,
    val h: Char,
    val i: String
)

@Serializable
data class DateWithOptional(val foo: String = "optional")

@Serializable
data class SimpleContainer(val bar: Int)

@Serializable
data class Complex(
    val simple: SimpleContainer,
    val optional: DateWithOptional
)

@Serializable
data class DateWithNullable(val foo: String?)

@Serializable
sealed class SealedDate {
    @Serializable
    @SerialName("sealed1")
    data class Sealed1(val a: Boolean) : SealedDate()

    @Serializable
    @SerialName("sealed2")
    data class Sealed2(val b: Int) : SealedDate()

    @Serializable
    @SerialName("sealed3")
    object Sealed3 : SealedDate()
}

interface AnInterface

@Serializable
data class InterfaceClassOne(val a: Int) : AnInterface

@Serializable
data class InterfaceClassTwo(val b: Byte) : AnInterface

val interfaceModule = SerializersModule {
    polymorphic(AnInterface::class) {
        subclass(InterfaceClassOne::class)
        subclass(InterfaceClassTwo::class)
    }
}

enum class Weekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}
