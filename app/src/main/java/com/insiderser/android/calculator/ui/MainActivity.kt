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
package com.insiderser.android.calculator.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.insiderser.android.calculator.R
import com.insiderser.android.calculator.databinding.MainActivityBinding
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags

/**
 * The main activity and navigation point.
 */
class MainActivity : AppCompatActivity(), NavigationHost {

    private lateinit var binding: MainActivityBinding

    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(navController.graph)
    }

    private val navController: NavController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navigation_view) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setEdgeToEdgeSystemUiFlags()
        binding.root.applySystemWindowInsetsToPadding(left = true, right = true)

        if (savedInstanceState == null && intent.hasExtra(EXTRA_DESTINATION)) {
            navigateTo(intent.getIntExtra(EXTRA_DESTINATION, -1))
        }
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.setOnMenuItemClickListener { it.onNavDestinationSelected(navController) }
    }

    private fun navigateTo(@IdRes destination: Int) {
        navController.navigate(destination)
    }

    companion object {

        const val EXTRA_DESTINATION = "extra.destination"
    }
}
