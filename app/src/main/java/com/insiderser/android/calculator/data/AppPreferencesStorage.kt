/*
 * Copyright 2020 Oleksandr Bezushko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.insiderser.android.calculator.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.insiderser.android.calculator.dagger.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app's and user's preferences.
 */
@WorkerThread
interface AppPreferencesStorage {

    /**
     * Theme storage key that is currently selected, or `null` if nothing selected.
     * @see com.insiderser.android.calculator.model.Theme
     */
    var selectedTheme: String?

    /**
     * A [Flow] with up-to-date theme storage key that is currently selected.
     * @see com.insiderser.android.calculator.model.Theme
     */
    val selectedThemeObservable: Flow<String?>
}

/**
 * Implementation of [AppPreferencesStorage] that uses [SharedPreferences] to store the data.
 */
@Singleton
class AppPreferencesStorageImpl @Inject constructor(
    context: Context,
    @IO ioDispatcher: CoroutineDispatcher
) : AppPreferencesStorage {

    private val storageIOScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    // Lazy to prevent IO on the main thread
    private val sharedPreferences: Lazy<SharedPreferences> = lazy {
        context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).apply {
            registerOnSharedPreferenceChangeListener(onChangedListener)
        }
    }

    private val onChangedListener = OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_THEME -> updateTheme()
        }
    }

    override var selectedTheme: String? by StringPreference(sharedPreferences, KEY_THEME)

    private val selectedThemeChannel = ConflatedBroadcastChannel<String?>()
    override val selectedThemeObservable: Flow<String?>
        get() = selectedThemeChannel.asFlow()

    init {
        updateTheme()
    }

    private fun updateTheme() {
        storageIOScope.launch {
            selectedThemeChannel.offer(selectedTheme)
        }
    }

    companion object {

        const val PREFS_NAME = "preferences"

        const val KEY_THEME = "selected_theme"
    }
}

/**
 * Property delegate that manages a single entry in [SharedPreferences].
 *
 * **Note**: all get operations are done synchronously on the calling thread.
 * Make sure to call it on a worker thread.
 */
class StringPreference(
    private val sharedPreferences: Lazy<SharedPreferences>,
    private val preferenceKey: String,
    private val defaultValue: String? = null
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? =
        sharedPreferences.value.getString(preferenceKey, defaultValue)

    @WorkerThread
    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        Timber.v("Changing value of property with key $preferenceKey to $value")
        sharedPreferences.value.edit(commit = true) {
            putString(preferenceKey, value)
        }
    }
}
