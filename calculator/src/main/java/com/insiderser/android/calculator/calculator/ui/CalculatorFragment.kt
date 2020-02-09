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
package com.insiderser.android.calculator.calculator.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.insiderser.android.calculator.calculator.dagger.DaggerCalculatorComponent
import com.insiderser.android.calculator.calculator.databinding.CalculatorFragmentBinding
import com.insiderser.android.calculator.core.dagger.SavedStateFactory
import com.insiderser.android.calculator.core.ui.binding.FragmentWithViewBinding
import com.insiderser.android.calculator.navigation.NavigationHost
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import javax.inject.Inject

/**
 * Main fragment that allows user to calculate mathematical expressions.
 */
class CalculatorFragment : FragmentWithViewBinding<CalculatorFragmentBinding>() {

    @Inject
    @SavedStateFactory
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: CalculatorFragmentViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        injectItself()
        super.onAttach(context)
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CalculatorFragmentBinding = CalculatorFragmentBinding.inflate(inflater, container, false)

    override fun onBindingCreated(binding: CalculatorFragmentBinding, savedInstanceState: Bundle?) {
        (activity as? NavigationHost)?.registerToolbarWithNavigation(binding.toolbar)

        binding.statusBar.doOnApplyWindowInsets { view, insets, _ ->
            view.updateLayoutParams<ViewGroup.LayoutParams> {
                height = insets.systemWindowInsetTop
            }
        }
    }

    private fun injectItself() {
        val feature1Component = DaggerCalculatorComponent.factory().create(this)
        feature1Component.inject(this)
    }
}
