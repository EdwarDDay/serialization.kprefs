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

import android.content.SharedPreferences
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Encodes changes to the delegated property into the [SharedPreferences] and decodes the current value from them.
 * ```
 * val setting by preferences.asProperty(Boolean.serializer())
 * ```
 *
 * @param serializer which encodes and decodes the value
 * @param tag optional tag which is used as SharedPreferences key - default to property name
 */
public fun <T> Preferences.asProperty(serializer: KSerializer<T>, tag: String? = null): ReadWriteProperty<Any?, T> =
    PreferenceProperty(this, serializer, tag)

/**
 * Encodes changes to the delegated property into the [SharedPreferences] and decodes the current value from them.
 * ```
 * val setting: Boolean by preferences.asProperty()
 * ```
 *
 * @param tag optional tag which is used as SharedPreferences key - default to property name
 */
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
