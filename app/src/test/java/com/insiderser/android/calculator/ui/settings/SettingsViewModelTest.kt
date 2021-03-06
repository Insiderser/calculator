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
package com.insiderser.android.calculator.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.insiderser.android.calculator.domain.theme.GetAvailableThemesUseCase
import com.insiderser.android.calculator.domain.theme.ObservableThemeUseCase
import com.insiderser.android.calculator.domain.theme.SetThemeUseCase
import com.insiderser.android.calculator.fakes.FakeAppPreferencesStorage
import com.insiderser.android.calculator.model.Theme
import com.insiderser.android.calculator.test.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Suppress("BlockingMethodInNonBlockingContext")
@OptIn(ObsoleteCoroutinesApi::class)
class SettingsViewModelTest {

    @Rule
    @JvmField
    val executorRule = InstantTaskExecutorRule()

    private val storage = FakeAppPreferencesStorage()

    private lateinit var viewModel: SettingsViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel(
            GetAvailableThemesUseCase(),
            ObservableThemeUseCase(storage, testDispatcher),
            SetThemeUseCase(storage, testDispatcher)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun assert_onThemeSettingClicked_triggersShowThemeSettingDialogEvent() = testDispatcher.runBlockingTest {
        assertThat(viewModel.showThemeSettingDialog.value).isNull()

        viewModel.onThemeSettingClicked()

        val event = viewModel.showThemeSettingDialog.await()
        assertThat(event).isNotNull()
    }

    @Test
    fun assert_availableThemes_isCorrect() = testDispatcher.runBlockingTest {
        val availableThemes = viewModel.availableThemes.await()

        // Don't test that FOLLOW_SYSTEM vs AUTO_BATTERY logic.
        // Just verify that SettingsViewModel correctly takes those values from the use case.
        assertThat(availableThemes).run {
            isNotNull()
            hasSize(3)
            containsAtLeast(Theme.DARK, Theme.LIGHT)
            containsAnyOf(Theme.AUTO_BATTERY, Theme.FOLLOW_SYSTEM)
        }
    }

    @Test
    fun assert_setSelectedTheme_updatesThemeAndSavesValue() = testDispatcher.runBlockingTest {
        var postedValue: Theme? = null
        var latch = CountDownLatch(1)

        viewModel.selectedTheme.observeForever { postedTheme ->
            postedValue = postedTheme
            latch.countDown()
        }

        // Check default value
        latch.await(1, TimeUnit.SECONDS)
        assertThat(postedValue).isAnyOf(Theme.AUTO_BATTERY, Theme.FOLLOW_SYSTEM)

        Theme.values().forEach { theme ->
            latch = CountDownLatch(1)
            viewModel.setSelectedTheme(theme)

            latch.await(1, TimeUnit.SECONDS)
            assertThat(postedValue).isEqualTo(theme)
            assertThat(storage.selectedTheme).isEqualTo(theme.storageKey)
        }
    }
}
