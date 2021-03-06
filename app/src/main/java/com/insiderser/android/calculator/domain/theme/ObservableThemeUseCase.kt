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
package com.insiderser.android.calculator.domain.theme

import com.insiderser.android.calculator.dagger.IO
import com.insiderser.android.calculator.data.AppPreferencesStorage
import com.insiderser.android.calculator.domain.ObservableUseCase
import com.insiderser.android.calculator.model.Theme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for getting observable [Flow] of user-selected theme settings from app preferences.
 * @see observe
 * @see invoke
 */
class ObservableThemeUseCase @Inject constructor(
    private val preferencesStorage: AppPreferencesStorage,
    @IO ioDispatcher: CoroutineDispatcher
) : ObservableUseCase<Unit, Theme>() {

    override val coroutineDispatcher = ioDispatcher

    override suspend fun createObservable(params: Unit): Flow<Theme> =
        preferencesStorage.selectedThemeObservable.map { storageKey: String? ->
            storageKey?.let { Theme.fromStorageKey(storageKey) } ?: DEFAULT_THEME
        }
}
