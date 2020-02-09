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

import com.insiderser.android.template.buildSrc.Libs
import com.insiderser.android.template.buildSrc.configureAndroidModule

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

configureAndroidModule()

dependencies {
    implementation(project(":core"))
    implementation(project(":navigation"))
    implementation(project(":data"))

    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.material)
    implementation(Libs.AndroidX.Fragment.fragmentKtx)

    implementation(Libs.AndroidX.Lifecycle.extensions)
    implementation(Libs.AndroidX.Lifecycle.viewModelKtx)

    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    testImplementation(project(":test-shared"))
    debugImplementation(Libs.AndroidX.Fragment.testing) {
        exclude(group = "androidx.test", module = "core")
    }
    testImplementation(Libs.Test.Robolectric.robolectric)
    testImplementation(Libs.Test.AndroidX.core)
    testImplementation(Libs.Test.AndroidX.runner)
    testImplementation(Libs.Test.AndroidX.rules)
    testImplementation(Libs.Test.AndroidX.ext)
    testImplementation(Libs.Test.AndroidX.arch)
}
