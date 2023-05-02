/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlinx.coroutines.*
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.coroutines.Math
import space.kscience.kmath.nd.ColumnStrides
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.StructureND

public class LazyStructureND<out T>(
    public val scope: CoroutineScope,
    override val shape: ShapeND,
    public val function: suspend (IntArray) -> T,
) : StructureND<T> {
    private val cache: MutableMap<IntArray, Deferred<T>> = HashMap()

    public fun async(index: IntArray): Deferred<T> = cache.getOrPut(index) {
        scope.async(context = Dispatchers.Math) { function(index) }
    }

    public suspend fun await(index: IntArray): T = async(index).await()
    @PerformancePitfall
    override operator fun get(index: IntArray): T = runBlocking { async(index).await() }

    @OptIn(PerformancePitfall::class)
    override fun elements(): Sequence<Pair<IntArray, T>> {
        val strides = ColumnStrides(shape)
        val res = runBlocking { strides.asSequence().toList().map { index -> index to await(index) } }
        return res.asSequence()
    }
}

@OptIn(PerformancePitfall::class)
public fun <T> StructureND<T>.async(index: IntArray): Deferred<T> =
    if (this is LazyStructureND<T>) this@async.async(index) else CompletableDeferred(get(index))

@OptIn(PerformancePitfall::class)
public suspend fun <T> StructureND<T>.await(index: IntArray): T =
    if (this is LazyStructureND<T>) await(index) else get(index)

/**
 * PENDING would benefit from KEEP-176
 */
@OptIn(PerformancePitfall::class)
public inline fun <T, R> StructureND<T>.mapAsyncIndexed(
    scope: CoroutineScope,
    crossinline function: suspend (T, index: IntArray) -> R,
): LazyStructureND<R> = LazyStructureND(scope, shape) { index -> function(get(index), index) }

@OptIn(PerformancePitfall::class)
public inline fun <T, R> StructureND<T>.mapAsync(
    scope: CoroutineScope,
    crossinline function: suspend (T) -> R,
): LazyStructureND<R> = LazyStructureND(scope, shape) { index -> function(get(index)) }
