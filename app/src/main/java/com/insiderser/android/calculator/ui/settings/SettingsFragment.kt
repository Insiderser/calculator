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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.insiderser.android.calculator.BuildConfig
import com.insiderser.android.calculator.dagger.injector
import com.insiderser.android.calculator.databinding.SettingsFragmentBinding
import com.insiderser.android.calculator.model.findTitleForTheme
import com.insiderser.android.calculator.ui.NavigationHost
import com.insiderser.android.calculator.ui.settings.theme.ThemeSettingDialogFragment
import com.insiderser.android.calculator.utils.observeEvent
import com.insiderser.android.calculator.utils.viewLifecycleScoped
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import javax.inject.Inject

/**
 * A root [fragment][androidx.fragment.app.Fragment] that displays a list of preferences.
 * All preferences are stored in preferences storage.
 */
class SettingsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }

    private var binding: SettingsFragmentBinding by viewLifecycleScoped()

    override fun onAttach(context: Context) {
        injector.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        SettingsFragmentBinding.inflate(inflater, container, false).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? NavigationHost)?.registerToolbarWithNavigation(binding.toolbar)

        binding.appBar.applySystemWindowInsetsToPadding(top = true)
        binding.scrollView.applySystemWindowInsetsToPadding(bottom = true)

        binding.chooseThemePreference.setOnClickListener {
            viewModel.onThemeSettingClicked()
        }

        binding.versionName.summary = BuildConfig.VERSION_NAME

        viewModel.showThemeSettingDialog.observeEvent(viewLifecycleOwner) {
            val dialogFragment =
                ThemeSettingDialogFragment()
            dialogFragment.show(childFragmentManager)
        }

        viewModel.selectedTheme.observe(viewLifecycleOwner) { selectedTheme ->
            binding.chooseThemePreference.summary = findTitleForTheme(selectedTheme)
        }
    }
}
