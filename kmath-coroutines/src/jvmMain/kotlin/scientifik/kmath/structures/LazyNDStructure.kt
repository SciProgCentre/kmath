package scientifik.kmath.structures

import kotlinx.coroutines.*

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