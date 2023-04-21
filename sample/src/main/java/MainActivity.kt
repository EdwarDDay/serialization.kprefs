// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences.testapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import net.edwardday.serialization.preferences.Preferences

class MainActivity : ComponentActivity() {

    private val preferences: Preferences by lazy {
        Preferences(getSharedPreferences("application data", Context.MODE_PRIVATE))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                var person by remember { mutableStateOf(Person("", "", Pet.Cat)) }
                MainScreen(
                    person = person,
                    onValueChange = { person = it },
                    onLoad = {
                        preferences.decodeOrDefault(Person.serializer().nullable, "person", null)
                            ?.also { person = it }
                    },
                    onSave = { preferences.encode(Person.serializer(), "person", person) },
                )
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen(
            person = Person("John", "Doe", Pet.Dog),
            onValueChange = { },
            onLoad = { },
            onSave = { },
        )
    }
}

@Composable
private fun MainScreen(
    person: Person,
    onValueChange: (Person) -> Unit,
    onLoad: () -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colors.background)
            .padding(8.dp),
        verticalArrangement = spacedBy(16.dp),
    ) {
        TextField(
            value = person.lastName,
            onValueChange = { onValueChange(person.copy(lastName = it)) },
            label = { Text(text = "last name") },
            modifier = Modifier.defaultMinSize(minWidth = 100.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
        )
        TextField(
            value = person.name,
            onValueChange = { onValueChange(person.copy(name = it)) },
            label = { Text(text = "name") },
            modifier = Modifier.defaultMinSize(minWidth = 100.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = if (person.pet is Pet.Other) ImeAction.Next else ImeAction.Done,
            ),
            singleLine = true,
        )
        Text(text = "pet", color = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium))
        Column(
            modifier = Modifier
                .border(width = 2.dp, color = MaterialTheme.colors.primary.copy(alpha = 0.2f))
                .padding(end = 8.dp),
        ) {
            val onCatClick: () -> Unit = { onValueChange(person.copy(pet = Pet.Cat)) }
            Row(
                modifier = Modifier.clickable(onClick = onCatClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = person.pet == Pet.Cat,
                    onClick = onCatClick,
                )
                Text(text = "Cat")
            }
            val onDogClick: () -> Unit = { onValueChange(person.copy(pet = Pet.Dog)) }
            Row(
                modifier = Modifier.clickable(onClick = onDogClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = person.pet == Pet.Dog,
                    onClick = onDogClick,
                )
                Text(text = "Dog")
            }

            val onOtherClick: () -> Unit =
                { if (person.pet !is Pet.Other) onValueChange(person.copy(pet = Pet.Other(""))) }
            Row(
                modifier = Modifier.clickable(onClick = onOtherClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = person.pet is Pet.Other,
                    onClick = onOtherClick,
                )
                Text(text = "Other")
            }
            AnimatedVisibility(visible = person.pet is Pet.Other) {
                TextField(
                    value = (person.pet as? Pet.Other)?.kind ?: "",
                    onValueChange = { onValueChange(person.copy(pet = Pet.Other(it))) },
                    label = { Text(text = "pet") },
                    modifier = Modifier.defaultMinSize(minWidth = 100.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                )
            }
        }

        val personIsValid = person.name.isNotBlank() &&
            person.lastName.isNotBlank() &&
            (person.pet !is Pet.Other || person.pet.kind.isNotBlank())

        Button(
            onClick = onSave,
            enabled = personIsValid,
        ) {
            Text(text = "save")
        }

        Button(
            onClick = onLoad,
        ) {
            Text(text = "load")
        }
    }
}

@Serializable
private data class Person(
    val name: String,
    val lastName: String,
    val pet: Pet,
)

@Serializable
sealed class Pet {
    @Serializable
    object Cat : Pet()

    @Serializable
    object Dog : Pet()

    @Serializable
    data class Other(val kind: String) : Pet()
}
