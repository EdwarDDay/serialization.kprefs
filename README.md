<!--
SPDX-FileCopyrightText: 2020-2024 Eduard Wolf

SPDX-License-Identifier: Apache-2.0
-->

# Android SharedPreferences serialization

[![REUSE status](https://api.reuse.software/badge/github.com/edwardday/serialization.kprefs)](https://api.reuse.software/info/github.com/edwardday/serialization.kprefs)
[![Develop](https://github.com/EdwarDDay/serialization.kprefs/workflows/Develop/badge.svg?branch=main)](https://github.com/EdwarDDay/serialization.kprefs/actions?query=workflow%3ADevelop+branch%3Amain)
[![Maven Central](https://img.shields.io/maven-central/v/net.edwardday.serialization/kprefs.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.edwardday.serialization%22%20AND%20a:%22kprefs%22)

Preferences serialization is a [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) format to
serialize arbitrary objects in androids
[SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences).

The documentation can be found on the projects
[Github Page](https://edwardday.github.io/serialization.kprefs/index.html).

## Contents

<!--- TOC -->

* [Small Example](#small-example)
* [Delegated Properties Example](#delegated-properties-example)
* [Setup](#setup)
* [Features](#features)
* [Building](#building)

<!--- INCLUDE .*-readme-.*
import android.content.*
import kotlin.test.*
import kotlinx.serialization.*
import net.edwardday.serialization.preferences.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReadmeExample {

    val sharedPreferences = createContext().getSharedPreferences("test_preferences", Context.MODE_PRIVATE)

    @AfterTest
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun readmeTest() {

----- SUFFIX .*-readme-.*
    }
}
-->

## Small Example

```kotlin
@Serializable
data class Person(val name: String, val age: Int, val children: List<Person> = emptyList())

val preferences = Preferences(sharedPreferences)

val abby = Person("Abby", 12)
val bob = Person("Bob", 10)
val charles = Person("Charles", 36, listOf(abby, bob))

preferences.encode("person", charles)
// ...
val person: Person = preferences.decode("person")
assertEquals(charles, person)
```

> You can get the full code [here](library/src/test/java/example/example-readme-01.kt).

## Delegated Properties Example

```kotlin
val preferences = Preferences(sharedPreferences)

var someFlag: Boolean by preferences.asProperty(default = false)

fun someComputation() {
    someFlag = true
}

someComputation()
if (!someFlag) { // reads value from SharedPreferences at key "someFlag"
    fail()
}
```

> You can get the full code [here](library/src/test/java/example/example-readme-02.kt).

## Setup

You need to apply the kotlinx.serialization plugin and add this library as dependency.

Kotlin DSL:

```
plugins {
    kotlin("plugin.serialization") version "2.0.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.edwardday.serialization:kprefs:0.13.0")
}
```

Note: additional information to the serialization plugin can be found in the
[kotlinx.serialization repository](https://github.com/Kotlin/kotlinx.serialization).

## Features

* support for all primitive types
  * support for double by encoding it to
    * Float - with loss of precision
    * String - with the string representation
    * Long - with the bits representation
* support nullability (stored in extra field)
* support for native `Set<String>` encoding
* support for encoding classes
* support for sealed classes
* support for objects by encoding an object start with a Boolean
* support for property delegated properties
* support for synchronization (synchronize on every object read / write)

## Building

To build the library just run `./gradlew library:build`. To publish it to you local maven repository use
`./gradlew library:publishToMavenLocal`
