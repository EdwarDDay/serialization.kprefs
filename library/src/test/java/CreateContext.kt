// SPDX-FileCopyrightText: 2020-2022 Eduard Wolf
//
// SPDX-License-Identifier: Apache-2.0

package net.edwardday.serialization.preferences

import android.app.Activity
import android.content.Context
import org.robolectric.Robolectric

fun createContext(): Context = Robolectric.buildActivity(Activity::class.java).get().applicationContext
