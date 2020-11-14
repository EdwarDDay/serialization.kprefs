// This file was automatically generated from Preferences.kt by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.examplePreferences02

import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*

class PreferencesTest {

    val sharedPreferences = TestablePreferences()

    @Test
    fun test() {

@Serializable
data class PrefTest(val u: Unit)

val pref = Preferences(sharedPreferences) { encodeObjectStarts = true }
pref.encode(PrefTest.serializer(), "test", PrefTest(Unit))

assertTrue(sharedPreferences.getBoolean("test.u", false))
    }
}
