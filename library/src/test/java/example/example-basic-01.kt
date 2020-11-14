// This file was automatically generated from README.md by Knit tool. Do not edit.
package net.edwardday.serialization.preferences.example.exampleBasic01

import kotlin.test.*
import net.edwardday.serialization.preferences.*

class ReadmeExample {

    val sharedPreferences = TestablePreferences()

    fun someComputation() {
        someFlag = true
    }

val preferences = Preferences(sharedPreferences)

var someFlag: Boolean by preferences.asProperty()

@Test
fun test() {
    someFlag = false // stores false in SharedPreferences at key "someFlag"
    someComputation() // some computation where someFlag is set to true
    if (!someFlag) { // reads value from SharedPreferences at key "someFlag"
        fail()
    }
}
}
