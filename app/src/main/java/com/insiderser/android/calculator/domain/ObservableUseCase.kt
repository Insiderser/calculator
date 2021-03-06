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
package com.insiderser.android.calculator.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn

/**
 * Executes its business logic in [createObservable] method.
 */
abstract class ObservableUseCase<in Param, Type> {

    private val channel = ConflatedBroadcastChannel<Param>()

    abstract val coroutineDispatcher: CoroutineDispatcher

    /**
     * Execute the logic with the given [parameters][params]. This method is non-blocking,
     * meaning that it's safe to call it on any thread.
     */
    operator fun invoke(params: Param) {
        channel.offer(params)
    }

    /**
     * Executes the logic. Won't be called if [params] haven't changed.
     *
     * **Note**: it's implementation's responsibility to make sure that
     * no heavy logic is executed on the main thread.
     */
    protected abstract suspend fun createObservable(params: Param): Flow<Type>

    /**
     * Returns observable [Flow] that will be updated with new values.
     *
     * **Note**: this method only returns the observable. To execute this use case,
     * call [invoke] method.
     */
    fun observe(): Flow<Type> = channel.asFlow()
        .distinctUntilChanged()
        .flatMapLatest { createObservable(it) }
        .flowOn(coroutineDispatcher)
}

/**
 * Execute the logic. This method is non-blocking, meaning that it's safe to call it on any thread.
 */
operator fun <R> ObservableUseCase<Unit, R>.invoke() = invoke(Unit)
