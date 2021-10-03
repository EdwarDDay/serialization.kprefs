// SPDX-FileCopyrightText: 2020-2021 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences.testapplication

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import net.edwardday.serialization.preferences.Preferences
import net.edwardday.serialization.preferences.testapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val preferences: Preferences by lazy {
        Preferences(getSharedPreferences("application data", Context.MODE_PRIVATE))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lastNameInput.addTextChangedListener {
            updateViewState(binding)
        }

        binding.nameInput.addTextChangedListener {
            updateViewState(binding)
        }

        binding.petInputCat.setOnClickListener {
            updateViewState(binding)
        }

        binding.petInputDog.setOnClickListener {
            updateViewState(binding)
        }

        binding.petInputOther.setOnClickListener {
            updateViewState(binding)
        }

        binding.petInputOtherContent.addTextChangedListener {
            updateViewState(binding)
        }

        binding.save.setOnClickListener {
            saveState(binding)
        }

        binding.load.setOnClickListener {
            loadState(binding)
        }
    }

    private fun updateViewState(binding: ActivityMainBinding) {
        val saveButtonEnabled = binding.lastNameInput.text.isNotBlank() &&
            binding.nameInput.text.isNotBlank() &&
            (binding.petInputCat.isChecked ||
                binding.petInputDog.isChecked ||
                binding.petInputOtherContent.text.isNotBlank())
        binding.save.isEnabled = saveButtonEnabled

        val petInputOtherContentVisible = binding.petInputOther.isChecked
        binding.petInputOtherContent.visibility =
            if (petInputOtherContentVisible) View.VISIBLE else View.GONE
    }

    private fun saveState(binding: ActivityMainBinding) {
        val pet = when {
            binding.petInputCat.isChecked -> Pet.Cat
            binding.petInputDog.isChecked -> Pet.Dog
            else -> Pet.Other(binding.petInputOtherContent.text.toString())
        }
        val person = Person(
            name = binding.nameInput.text.toString(),
            lastName = binding.lastNameInput.text.toString(),
            pet = pet,
        )
        preferences.encode(Person.serializer(), "person", person)
    }

    private fun loadState(binding: ActivityMainBinding) {
        val person = preferences.decode(Person.serializer().nullable, "person") ?: return
        binding.nameInput.setText(person.name)
        binding.lastNameInput.setText(person.lastName)
        when (person.pet) {
            Pet.Cat -> binding.petInputCat.isChecked = true
            Pet.Dog -> binding.petInputDog.isChecked = true
            is Pet.Other -> {
                binding.petInputOther.isChecked = true
                binding.petInputOtherContent.setText(person.pet.kind)
            }
        }

        updateViewState(binding)
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
