# Android SharedPreferences serialization

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

## Features
* support for all primitive types
  * support for double by encoding it to
    * Float - with loss of precision
    * String - with the string representation
    * Long - with the bits representation
* support for encoding classes
* support for sealed classes
* support for objects by encoding an object start with a Boolean

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
