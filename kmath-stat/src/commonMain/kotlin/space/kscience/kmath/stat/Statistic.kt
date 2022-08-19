/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce
import space.kscience.kmath.coroutines.mapParallel
import space.kscience.kmath.structures.Buffer

/**
 * A function, that transforms a buffer of random quantities to some resulting value
 */
public fun interface Statistic<in T, out R> {
    public suspend fun evaluate(data: Buffer<T>): R
}

public suspend operator fun <T, R> Statistic<T, R>.invoke(data: Buffer<T>): R = evaluate(data)

/**
 * A statistic that is computed in a synchronous blocking mode
 */
public fun interface BlockingStatistic<in T, out R> : Statistic<T, R> {
    public fun evaluateBlocking(data: Buffer<T>): R

    override suspend fun evaluate(data: Buffer<T>): R = evaluateBlocking(data)
}

public operator fun <T, R> BlockingStatistic<T, R>.invoke(data: Buffer<T>): R = evaluateBlocking(data)

/**
 * A statistic tha could be computed separately on different blocks of data and then composed
 *
 * @param T the source type.
 * @param I the intermediate block type.
 * @param R the result type.
 */
public interface ComposableStatistic<in T, I, out R> : Statistic<T, R> {
    //compute statistic on a single block
    public suspend fun computeIntermediate(data: Buffer<T>): I

    //Compose two blocks
    public suspend fun composeIntermediate(first: I, second: I): I

    //Transform block to result
    public suspend fun toResult(intermediate: I): R

    override suspend fun evaluate(data: Buffer<T>): R = toResult(computeIntermediate(data))
}

/**
 * Flow intermediate state of the [ComposableStatistic]
 */
@OptIn(ExperimentalCoroutinesApi::class)
private fun <T, I, R> ComposableStatistic<T, I, R>.flowIntermediate(
    flow: Flow<Buffer<T>>,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Flow<I> = flow
    .mapParallel(dispatcher) { computeIntermediate(it) }
    .runningReduce(::composeIntermediate)


/**
 * Perform a streaming statistical analysis on a chunked data. The computation of inner representation is done in parallel
 * if [dispatcher] allows it.
 *
 * The resulting flow contains values that include the whole previous statistics, not only the last chunk.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T, I, R> ComposableStatistic<T, I, R>.flow(
    flow: Flow<Buffer<T>>,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Flow<R> = flowIntermediate(flow, dispatcher).map(::toResult)

