package scientifik.kmath.structures

import kotlinx.coroutines.*
import scientifik.kmath.coroutines.Math

class LazyNDStructure<T>(
    val scope: CoroutineScope,
    override val shape: IntArray,
    val function: suspend (IntArray) -> T
) : NDStructure<T> {
    private val cache: MutableMap<IntArray, Deferred<T>> = hashMapOf()

    fun deferred(index: IntArray): Deferred<T> = cache.getOrPut(index) {
        scope.async(context = Dispatchers.Math) {
            function(index)
        }
    }

    suspend fun await(index: IntArray): T = deferred(index).await()

    override operator fun get(index: IntArray): T = runBlocking {
        deferred(index).await()
    }

    override fun elements(): Sequence<Pair<IntArray, T>> {
        val strides = DefaultStrides(shape)
        val res = runBlocking {
            strides.indices().toList().map { index -> index to await(index) }
        }
        return res.asSequence()
    }

    override fun equals(other: Any?): Boolean {
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    override fun hashCode(): Int {
        var result = scope.hashCode()
        result = 31 * result + shape.contentHashCode()
        result = 31 * result + function.hashCode()
        result = 31 * result + cache.hashCode()
        return result
    }
}

fun <T> NDStructure<T>.deferred(index: IntArray): Deferred<T> =
    if (this is LazyNDStructure<T>) this.deferred(index) else CompletableDeferred(get(index))

suspend fun <T> NDStructure<T>.await(index: IntArray): T =
    if (this is LazyNDStructure<T>) this.await(index) else get(index)

/**
 * PENDING would benefit from KEEP-176
 */
inline fun <T, R> NDStructure<T>.mapAsyncIndexed(
    scope: CoroutineScope,
    crossinline function: suspend (T, index: IntArray) -> R
): LazyNDStructure<R> = LazyNDStructure(scope, shape) { index -> function(get(index), index) }

inline fun <T, R> NDStructure<T>.mapAsync(
    scope: CoroutineScope,
    crossinline function: suspend (T) -> R
): LazyNDStructure<R> = LazyNDStructure(scope, shape) { index -> function(get(index)) }
