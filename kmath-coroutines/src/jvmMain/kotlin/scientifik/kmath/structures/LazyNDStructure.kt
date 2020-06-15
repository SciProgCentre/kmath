package scientifik.kmath.structures

import kotlinx.coroutines.*
import scientifik.kmath.coroutines.Math

class LazyNDStructure<T>(
    val scope: CoroutineScope,
    override val shape: IntArray,
    val function: suspend (IntArray) -> T
) : NDStructure<T> {

    private val cache = HashMap<IntArray, Deferred<T>>()

    fun deferred(index: IntArray) = cache.getOrPut(index) {
        scope.async(context = Dispatchers.Math) {
            function(index)
        }
    }

    suspend fun await(index: IntArray): T = deferred(index).await()

    override fun get(index: IntArray): T = runBlocking {
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

fun <T> NDStructure<T>.deferred(index: IntArray) =
    if (this is LazyNDStructure<T>) this.deferred(index) else CompletableDeferred(get(index))

suspend fun <T> NDStructure<T>.await(index: IntArray) =
    if (this is LazyNDStructure<T>) this.await(index) else get(index)

/**
 * PENDING would benifit from KEEP-176
 */
fun <T, R> NDStructure<T>.mapAsyncIndexed(scope: CoroutineScope, function: suspend (T, index: IntArray) -> R) =
    LazyNDStructure(scope, shape) { index -> function(get(index), index) }

fun <T, R> NDStructure<T>.mapAsync(scope: CoroutineScope, function: suspend (T) -> R) =
    LazyNDStructure(scope, shape) { index -> function(get(index)) }