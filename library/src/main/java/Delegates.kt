// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.SharedPreferences
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/* Knit setup
<!--- INCLUDE .*-property-.*
import kotlin.test.*
import kotlinx.serialization.builtins.*
import net.edwardday.serialization.preferences.*

class PropertyTest {

    val preferences = Preferences(TestablePreferences())

----- SUFFIX .*-property-.*
    @Test
    fun test() {
        setting = false
        assertFalse(setting)
        setting = true
        assertTrue(setting)
    }
}
-->
*/
/**
 * Encodes changes to the delegated property into the [SharedPreferences] and decodes the current value from them.
 * ```kotlin
 * var setting by preferences.asProperty(Boolean.serializer())
 * ```
 *
 * @param serializer which encodes and decodes the value
 * @param tag optional tag which is used as SharedPreferences key - default to property name
 */
// <!--- KNIT example-property-01.kt -->
public fun <T> Preferences.asProperty(serializer: KSerializer<T>, tag: String? = null): ReadWriteProperty<Any?, T> =
    PreferenceProperty(this, serializer, tag)

/**
 * Encodes changes to the delegated property into the [SharedPreferences] and decodes the current value from them.
 * ```kotlin
 * var setting: Boolean by preferences.asProperty()
 * ```
 *
 * @param tag optional tag which is used as SharedPreferences key - default to property name
 */
// <!--- KNIT example-property-02.kt -->
public inline fun <reified T> Preferences.asProperty(tag: String? = null): ReadWriteProperty<Any?, T> =
    asProperty(serializersModule.serializer(), tag)

private class PreferenceProperty<T>(
    private val preferences: Preferences,
    private val serializer: KSerializer<T>,
    private val tag: String?
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val tag = this.tag ?: property.name
        return preferences.decode(serializer, tag)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val tag = this.tag ?: property.name
        preferences.encode(serializer, tag, value)
    }
}
