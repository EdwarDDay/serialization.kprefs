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
import kotlin.properties.ReadWriteProperty
import net.edwardday.serialization.preferences.DoubleRepresentation as netDoubleRepresentation
import net.edwardday.serialization.preferences.Preferences as netPreferences
import net.edwardday.serialization.preferences.PreferencesBuilder as netPreferencesBuilder
import net.edwardday.serialization.preferences.asProperty as netAsProperty
import net.edwardday.serialization.preferences.decode as netDecode
import net.edwardday.serialization.preferences.encode as netEncode

/**
 * @see [net.edwardday.serialization.preferences.Preferences]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith("net.edwardday.serialization.preferences.Preferences")
)
public typealias Preferences = netPreferences

/**
 * @see [net.edwardday.serialization.preferences.encode]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith(
        expression = "this.encode<T>(tag = tag, value = value)",
        imports = ["net.edwardday.serialization.preferences.encode"]
    )
)
@Suppress("Deprecation")
public inline fun <reified T> Preferences.encode(tag: String, value: T) {
    netEncode(tag, value)
}

/**
 * @see [net.edwardday.serialization.preferences.decode]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith(
        expression = "this.decode<T>(tag = tag)",
        imports = ["net.edwardday.serialization.preferences.decode"]
    )
)
@Suppress("Deprecation")
public inline fun <reified T> Preferences.decode(tag: String): T = netDecode(tag)

/**
 * @see [net.edwardday.serialization.preferences.Preferences]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith(
        "net.edwardday.serialization.preferences.Preferences(sharedPreferences, builderAction)"
    )
)
@Suppress("FunctionName", "Deprecation")
public fun Preferences(
    sharedPreferences: SharedPreferences,
    builderAction: PreferencesBuilder.() -> Unit = {}
): Preferences = netPreferences(sharedPreferences, builderAction)

/**
 * @see [net.edwardday.serialization.preferences.Preferences]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith("net.edwardday.serialization.preferences.Preferences")
)
@Suppress("FunctionName", "Deprecation")
public fun Preferences(
    preferences: Preferences,
    builderAction: PreferencesBuilder.() -> Unit = {}
): Preferences = netPreferences(preferences, builderAction)

/**
 * @see [net.edwardday.serialization.preferences.PreferencesBuilder]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith("net.edwardday.serialization.preferences.PreferencesBuilder")
)
public typealias PreferencesBuilder = netPreferencesBuilder

/**
 * @see [net.edwardday.serialization.preferences.DoubleRepresentation]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith("net.edwardday.serialization.preferences.DoubleRepresentation")
)
public typealias DoubleRepresentation = netDoubleRepresentation

/**
 * @see [net.edwardday.serialization.preferences.asProperty]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith(
        expression = "this.asProperty<T>(serializer = serializer, tag = tag)",
        imports = ["net.edwardday.serialization.preferences.asProperty"]
    )
)
@Suppress("Deprecation")
public fun <T> Preferences.asProperty(serializer: KSerializer<T>, tag: String? = null): ReadWriteProperty<Any?, T> =
    netAsProperty(serializer, tag)

/**
 * @see [net.edwardday.serialization.preferences.asProperty]
 */
@Deprecated(
    message = "namespace change to net.edwardday.serialization.preferences",
    replaceWith = ReplaceWith(
        expression = "this.asProperty<T>(tag = tag)",
        imports = ["net.edwardday.serialization.preferences.asProperty"]
    )
)
@Suppress("Deprecation")
public inline fun <reified T> Preferences.asProperty(tag: String? = null): ReadWriteProperty<Any?, T> =
    netAsProperty(tag)
