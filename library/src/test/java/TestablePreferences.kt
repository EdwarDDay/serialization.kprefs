// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.SharedPreferences

class TestablePreferences : SharedPreferences {
    private val map: MutableMap<String, Any?> = mutableMapOf()

    override fun getAll(): Map<String, *> = map.toMap()

    override fun getString(key: String?, defValue: String?): String? =
        map[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String?, defValues: Set<String>?): Set<String>? =
        map[key] as? Set<String> ?: defValues

    override fun getInt(key: String?, defValue: Int): Int = map[key] as? Int ?: defValue

    override fun getLong(key: String?, defValue: Long): Long = map[key] as? Long ?: defValue

    override fun getFloat(key: String?, defValue: Float): Float = map[key] as? Float ?: defValue

    override fun getBoolean(key: String?, defValue: Boolean): Boolean = map[key] as? Boolean ?: defValue

    override fun contains(key: String?): Boolean = map.containsKey(key)

    override fun edit(): SharedPreferences.Editor = Editor()

    inner class Editor(
        private val editorMap: MutableMap<String, Any?> = map.toMutableMap()
    ) : SharedPreferences.Editor {

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            editorMap[key] = value
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            editorMap[key] = values
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            editorMap[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            editorMap[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            editorMap[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            editorMap[key] = value
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            editorMap.remove(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            editorMap.clear()
            return this
        }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            this@TestablePreferences.map.clear()
            this@TestablePreferences.map.putAll(editorMap)
        }
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        error("not needed")
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        error("not needed")
    }
}
