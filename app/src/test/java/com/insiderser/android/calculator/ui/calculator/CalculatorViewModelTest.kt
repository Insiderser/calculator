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

package com.insiderser.android.calculator.ui.calculator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.insiderser.android.calculator.db.ExpressionsHistoryEntity
import com.insiderser.android.calculator.domain.history.AddExpressionToHistoryUseCase
import com.insiderser.android.calculator.domain.history.GetExpressionFromHistoryUseCase
import com.insiderser.android.calculator.domain.math.EvaluateExpressionUseCase
import com.insiderser.android.calculator.domain.math.LocalizeExpressionUseCase
import com.insiderser.android.calculator.fakes.FakeExpressionsHistoryDao
import com.insiderser.android.calculator.model.Expression
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
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

@OptIn(ObsoleteCoroutinesApi::class)
class CalculatorViewModelTest {

    @Rule
    @JvmField
    val executorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var localizeExpressionUseCase: LocalizeExpressionUseCase

    private val historyDao = FakeExpressionsHistoryDao()

    private lateinit var viewModel: CalculatorViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        every { localizeExpressionUseCase.invoke(any<Double>()) } answers {
            val valueParameter = arg<Double>(0)
            Expression(valueParameter.toString())
        }

        every { localizeExpressionUseCase.invoke(any<Expression>()) } returnsArgument 0

        viewModel = CalculatorViewModel(
            EvaluateExpressionUseCase(testDispatcher),
            localizeExpressionUseCase,
            AddExpressionToHistoryUseCase(
                historyDao,
                EvaluateExpressionUseCase(testDispatcher),
                testDispatcher
            ),
            GetExpressionFromHistoryUseCase(historyDao, testDispatcher)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testExpressionResultFlow() = testDispatcher.runBlockingTest {
        viewModel.expression.observeForever {}
        viewModel.result.observeForever {}

        checkExpressionAndResult("", "")

        viewModel.onArithmeticButtonClicked("5")
        checkExpressionAndResult("5", "5.0")

        viewModel.onArithmeticButtonClicked("!")
        checkExpressionAndResult("5!", "120.0")

        viewModel.onArithmeticButtonClicked("+")
        checkExpressionAndResult("5!+", "")

        viewModel.onClearButtonClicked()
        checkExpressionAndResult("5!", "120.0")

        viewModel.onArithmeticButtonClicked("*")
        viewModel.onArithmeticButtonClicked("sin")
        viewModel.onArithmeticButtonClicked("(")
        viewModel.onArithmeticButtonClicked("pi")
        viewModel.onArithmeticButtonClicked("/")
        viewModel.onArithmeticButtonClicked("2")
        viewModel.onArithmeticButtonClicked(")")
        checkExpressionAndResult("5!*sin(pi/2)", "120.0")

        viewModel.onClearButtonLongClick()
        checkExpressionAndResult("", "")

        viewModel.onClearButtonClicked()
        checkExpressionAndResult("", "")
    }

    private fun checkExpressionAndResult(
        expectedExpression: String,
        expectedResult: String
    ) {
        assertThat(viewModel.expression.value).isEqualTo(Expression(expectedExpression))
        assertThat(viewModel.result.value).isEqualTo(Expression(expectedResult))
    }

    @Test
    fun onEqualButtonClicked_insertsHistoryEntityIntoDao() = testDispatcher.runBlockingTest {
        viewModel.onArithmeticButtonClicked("5")
        viewModel.onArithmeticButtonClicked("+")
        viewModel.onArithmeticButtonClicked("9")
        viewModel.onEqualButtonClicked()

        assertThat(historyDao.history.any { it.value.expression == "5+9" }).isTrue()
    }

    @Test
    fun onEqualButtonClicked_setsExpressionToExpressionValue() = testDispatcher.runBlockingTest {
        viewModel.expression.observeForever {}

        viewModel.onArithmeticButtonClicked("5")
        viewModel.onArithmeticButtonClicked("+")
        viewModel.onArithmeticButtonClicked("9")
        viewModel.onEqualButtonClicked()

        assertThat(viewModel.expression.value?.value).isEqualTo("14")
    }

    @Test
    fun setHistoryId_loadsExpressionFromHistoryDb() = testDispatcher.runBlockingTest {
        viewModel.expression.observeForever {}

        val id = 100
        val expression = "some-expression-here"
        val historyEntity = ExpressionsHistoryEntity(expression, 10.0)
        historyDao.history[id] = historyEntity

        viewModel.setHistoryId(id)

        assertThat(viewModel.expression.value).isEqualTo(Expression(expression))
    }
}
