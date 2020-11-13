# Android SharedPreferences serialization

[![License](https://img.shields.io/github/license/EdwarDDay/serialization.kprefs?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Develop](https://github.com/EdwarDDay/serialization.kprefs/workflows/Develop/badge.svg?branch=main)](https://github.com/EdwarDDay/serialization.kprefs/actions?query=workflow%3ADevelop+branch%3Amain)
[![Download](https://api.bintray.com/packages/edwardday/maven/net.edwardday.serialization%3Akprefs/images/download.svg)](https://bintray.com/edwardday/maven/net.edwardday.serialization%3Akprefs/_latestVersion)

Preferences serialization is a [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) format to
 serialize arbitrary objects in androids
 [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences).

## Small Example

```kotlin
@Serializable
data class Person(val name: String, val age: Int, val children: List<Person> = emptyList())

val preferences = Preferences(context.getSharedPreferences("application data", Context.MODE_PRIVATE))

val abby = Person("Abby", 12)
val bob = Person("Bob", 10)
val charles = Person("Charles", 36, listOf(abby, bob))

preferences.encode("person", charles)
// ...
val person: Person = preferences.decode("person")
assertEquals(charles, person)
```

## Delegated Properties Example

```kotlin
val preferences = Preferences(context.getSharedPreferences("application data", Context.MODE_PRIVATE))

val someFlag: Boolean by preferences.asProperty()

fun test() {
    someFlag = false // stores false in SharedPreferences at key "someFlag"
    // ...
    if (someFlag) { // reads value from SharedPreferences at key "someFlag"

    }
}
```

## Setup
You need to apply the kotlinx.serialization plugin and add this library as dependency.

Kotlin DSL:
```kotlin
plugins {
    kotlin("plugin.serialization") version "1.4.10"
}

repositories {
    jcenter()
}

dependencies {
    implementation("net.edwardday.serialization:kprefs:0.5.1")
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
* support for native `Set<String>` encoding
* support for encoding classes
* support for sealed classes
* support for objects by encoding an object start with a Boolean
* support for property delegated properties

## What doesn't work
`SharedPreferences` don't support nullability. So I decided to decode nonexistent values as `null`. That's why nullable
 parameters with a non null default value will be decoded to `null`, if the value wasn't encoded.

```kotlin
@Serializable
data class Test(val foo: Int, val bar: Int? = 42)

sharedPreferences.edit().putInt("test.foo", 21).apply()
val preferences = Preferences(sharedPreferences)

val test: Test = preferences.decode("test")

assertEquals(Test(21, null), test)
```

## Building
To build the library just run `./gradlew library:build`. To publish it to you local maven repository use
 `./gradlew library:publishToMavenLocal`
