// SPDX-FileCopyrightText: 2020-2024 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AsyncTest {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var preferences: Preferences

    @BeforeTest
    fun setup() {
        sharedPreferences = createContext().getSharedPreferences("test_preferences", Context.MODE_PRIVATE)
        preferences = Preferences(sharedPreferences)
    }

    @AfterTest
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun testMassiveObjectWriteErrorOnNonSynchronized() {
        preferences = Preferences(preferences) {
            synchronizeEncoding = false
        }

        val actual = massiveObjectWrite()

        assertTrue(actual < AMOUNT_OF_COROUTINES * AMOUNT_OF_REPEATS)
    }

    @Test
    fun testMassiveObjectWriteSynchronized() {
        preferences = Preferences(preferences) {
            synchronizeEncoding = true
        }

        val actual = massiveObjectWrite()

        assertEquals(AMOUNT_OF_COROUTINES * AMOUNT_OF_REPEATS, actual)
    }

    // returns amount of matches
    private fun massiveObjectWrite(): Int = runBlocking(Dispatchers.Default) {
        var complex by preferences.asProperty<Complex>()
        complex = Complex(SimpleContainer(4), DateWithOptional("foo"))
        val matchingCounter = AtomicInteger(0)
        val writeJobs = List(AMOUNT_OF_COROUTINES) {
            launch {
                repeat(AMOUNT_OF_REPEATS) {
                    complex = if (it % 2 == 1) {
                        Complex(SimpleContainer(4), DateWithOptional("foo"))
                    } else {
                        Complex(SimpleContainer(2), DateWithOptional("bar"))
                    }
                }
            }
        }
        val readJobs = List(AMOUNT_OF_COROUTINES) {
            launch {
                repeat(AMOUNT_OF_REPEATS) {
                    when (complex) {
                        Complex(SimpleContainer(4), DateWithOptional("foo")),
                        Complex(SimpleContainer(2), DateWithOptional("bar")),
                        -> matchingCounter.incrementAndGet()
                    }
                }
            }
        }
        writeJobs.joinAll()
        readJobs.joinAll()
        matchingCounter.get()
    }

    companion object {
        private const val AMOUNT_OF_COROUTINES = 100
        private const val AMOUNT_OF_REPEATS = 100
    }
}
