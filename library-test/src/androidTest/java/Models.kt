// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

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
    val i: String,
)

@Serializable
data class DateWithOptional(val foo: String = "optional")

@Serializable
data class SimpleContainer(val bar: Int)

@Serializable
data class Complex(
    val simple: SimpleContainer,
    val optional: DateWithOptional,
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

@Serializable
data class StringSetWrapper(
    val kotlinSet: Set<String>,
    val customSet: List<String>,
)
