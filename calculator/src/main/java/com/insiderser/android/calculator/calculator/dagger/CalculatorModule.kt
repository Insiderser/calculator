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
package com.insiderser.android.calculator.calculator.dagger

import androidx.lifecycle.ViewModel
import com.insiderser.android.calculator.calculator.ui.CalculatorFragmentViewModel
import com.insiderser.android.calculator.core.dagger.AssistedViewModelFactory
import com.insiderser.android.calculator.core.dagger.ViewModelKey
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Dagger module for feature1 module. It tells dagger how to create [CalculatorFragmentViewModel].
 */
@AssistedModule
@Module(includes = [AssistedInject_CalculatorModule::class])
internal interface CalculatorModule {

    @Binds
    @IntoMap
    @ViewModelKey(CalculatorFragmentViewModel::class)
    fun bindCalculatorFragmentViewModelFactory(f: CalculatorFragmentViewModel.Factory): AssistedViewModelFactory<out ViewModel>
}
