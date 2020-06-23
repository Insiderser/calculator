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

package com.insiderser.android.calculator.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.insiderser.android.calculator.data.HistoryRepository
import com.insiderser.android.calculator.domain.math.LocalizeExpressionUseCase
import com.insiderser.android.calculator.model.HistoryItem
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    historyRepository: HistoryRepository,
    localizeExpressionUseCase: LocalizeExpressionUseCase
) : ViewModel() {

    val history: LiveData<PagedList<HistoryItem>> = historyRepository.getAll()
        .map { item ->
            HistoryItem(
                id = item.id,
                expression = localizeExpressionUseCase(item.expression),
                result = localizeExpressionUseCase(item.result),
                dateAdded = item.timeAdded
            )
        }
        .toLiveData(PAGE_SIZE)

    companion object {
        private const val PAGE_SIZE = 25
    }
}
