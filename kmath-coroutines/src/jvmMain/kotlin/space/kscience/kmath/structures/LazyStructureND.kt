/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlinx.coroutines.*
import space.kscience.kmath.coroutines.Math
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.StructureND

public class LazyStructureND<out T>(
    public val scope: CoroutineScope,
    override val shape: IntArray,
    public val function: suspend (IntArray) -> T,
) : StructureND<T> {
    private val cache: MutableMap<IntArray, Deferred<T>> = HashMap()

    public fun deferred(index: IntArray): Deferred<T> = cache.getOrPut(index) {
        scope.async(context = Dispatchers.Math) { function(index) }
    }

    public suspend fun await(index: IntArray): T = deferred(index).await()
    override operator fun get(index: IntArray): T = runBlocking { deferred(index).await() }

    @OptIn(PerformancePitfall::class)
    override fun elements(): Sequence<Pair<IntArray, T>> {
        val strides = DefaultStrides(shape)
        val res = runBlocking { strides.asSequence().toList().map { index -> index to await(index) } }
        return res.asSequence()
    }
}

public fun <T> StructureND<T>.deferred(index: IntArray): Deferred<T> =
    if (this is LazyStructureND<T>) deferred(index) else CompletableDeferred(get(index))

public suspend fun <T> StructureND<T>.await(index: IntArray): T =
    if (this is LazyStructureND<T>) await(index) else get(index)

context(CoroutineScope)
public inline fun <T, R> StructureND<T>.mapAsyncIndexed(
    crossinline function: suspend (T, index: IntArray) -> R,
): LazyStructureND<R> = LazyStructureND(this@CoroutineScope, shape) { index -> function(get(index), index) }

context(CoroutineScope)
public inline fun <T, R> StructureND<T>.mapAsync(crossinline function: suspend (T) -> R): LazyStructureND<R> =
    LazyStructureND(this@CoroutineScope, shape) { index -> function(get(index)) }
