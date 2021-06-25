// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from README.md by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.exampleReadme01

import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*

class ReadmeExample {

    val sharedPreferences = TestablePreferences()

@Serializable
data class Person(val name: String, val age: Int, val children: List<Person> = emptyList())
    @Test
    fun readmeTest() {

val preferences = Preferences(sharedPreferences)

val abby = Person("Abby", 12)
val bob = Person("Bob", 10)
val charles = Person("Charles", 36, listOf(abby, bob))

preferences.encode("person", charles)
// ...
val person: Person = preferences.decode("person")
assertEquals(charles, person)
    }
}
