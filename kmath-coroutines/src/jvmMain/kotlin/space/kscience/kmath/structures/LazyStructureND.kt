package space.kscience.kmath.structures

import kotlinx.coroutines.*
import space.kscience.kmath.coroutines.Math
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.StructureND

public class LazyStructureND<T>(
    public val scope: CoroutineScope,
    public override val shape: IntArray,
    public val function: suspend (IntArray) -> T,
) : StructureND<T> {
    private val cache: MutableMap<IntArray, Deferred<T>> = HashMap()

    public fun deferred(index: IntArray): Deferred<T> = cache.getOrPut(index) {
        scope.async(context = Dispatchers.Math) { function(index) }
    }

    public suspend fun await(index: IntArray): T = deferred(index).await()
    public override operator fun get(index: IntArray): T = runBlocking { deferred(index).await() }

    public override fun elements(): Sequence<Pair<IntArray, T>> {
        val strides = DefaultStrides(shape)
        val res = runBlocking { strides.indices().toList().map { index -> index to await(index) } }
        return res.asSequence()
    }
}

public fun <T> StructureND<T>.deferred(index: IntArray): Deferred<T> =
    if (this is LazyStructureND<T>) deferred(index) else CompletableDeferred(get(index))

public suspend fun <T> StructureND<T>.await(index: IntArray): T =
    if (this is LazyStructureND<T>) await(index) else get(index)

/**
 * PENDING would benefit from KEEP-176
 */
public inline fun <T, R> StructureND<T>.mapAsyncIndexed(
    scope: CoroutineScope,
    crossinline function: suspend (T, index: IntArray) -> R,
): LazyStructureND<R> = LazyStructureND(scope, shape) { index -> function(get(index), index) }

public inline fun <T, R> StructureND<T>.mapAsync(
    scope: CoroutineScope,
    crossinline function: suspend (T) -> R,
): LazyStructureND<R> = LazyStructureND(scope, shape) { index -> function(get(index)) }
