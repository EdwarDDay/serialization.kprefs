// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

// This file was automatically generated from Delegates.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePropertyWithoutDefault01

import kotlin.test.*
import kotlinx.serialization.builtins.*
import net.edwardday.serialization.preferences.*

class PropertyTest {

    val preferences = Preferences(TestablePreferences())

var setting by preferences.asProperty(Boolean.serializer())
    @Test
    fun test() {
        setting = false
        assertFalse(setting)
        setting = true
        assertTrue(setting)
    }
}
