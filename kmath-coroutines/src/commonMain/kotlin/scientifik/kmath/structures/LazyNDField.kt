package scientifik.kmath.structures

import kotlinx.coroutines.*
import scientifik.kmath.operations.Field

class LazyNDField<T, F : Field<T>>(shape: IntArray, field: F, val scope: CoroutineScope = GlobalScope) : BufferNDField<T, F>(shape,field, ::boxingBuffer) {

    override fun add(a: NDStructure<T>, b: NDStructure<T>): NDElements<T, F> {
        return LazyNDStructure(this) { index ->
            val aDeferred = a.deferred(index)
            val bDeferred = b.deferred(index)
            aDeferred.await() + bDeferred.await()
        }
    }

    override fun multiply(a: NDStructure<T>, k: Double): NDElements<T, F> {
        return LazyNDStructure(this) { index -> a.await(index) * k }
    }

    override fun multiply(a: NDStructure<T>, b: NDStructure<T>): NDElements<T, F> {
        return LazyNDStructure(this) { index ->
            val aDeferred = a.deferred(index)
            val bDeferred = b.deferred(index)
            aDeferred.await() * bDeferred.await()
        }
    }

    override fun divide(a: NDStructure<T>, b: NDStructure<T>): NDElements<T, F> {
        return LazyNDStructure(this) { index ->
            val aDeferred = a.deferred(index)
            val bDeferred = b.deferred(index)
            aDeferred.await() / bDeferred.await()
        }
    }
}

class LazyNDStructure<T, F : Field<T>>(override val context: LazyNDField<T, F>, val function: suspend F.(IntArray) -> T) : NDElements<T, F>, NDStructure<T> {
    override val self: NDElements<T, F> get() = this
    override val shape: IntArray get() = context.shape

    private val cache = HashMap<IntArray, Deferred<T>>()

    fun deferred(index: IntArray) = cache.getOrPut(index) { context.scope.async(context = Dispatchers.Math) { function.invoke(context.field, index) } }

    suspend fun await(index: IntArray): T = deferred(index).await()

    override fun get(index: IntArray): T = runBlocking {
        deferred(index).await()
    }

    override fun elements(): Sequence<Pair<IntArray, T>> {
        val strides = DefaultStrides(shape)
        return strides.indices().map { index -> index to runBlocking { await(index) } }
    }
}

fun <T> NDStructure<T>.deferred(index: IntArray) = if (this is LazyNDStructure<T, *>) this.deferred(index) else CompletableDeferred(get(index))

suspend fun <T> NDStructure<T>.await(index: IntArray) = if (this is LazyNDStructure<T, *>) this.await(index) else get(index)

fun <T, F : Field<T>> NDElements<T, F>.lazy(scope: CoroutineScope = GlobalScope): LazyNDStructure<T, F> {
    return if (this is LazyNDStructure<T, F>) {
        this
    } else {
        val context = LazyNDField(context.shape, context.field, scope)
        LazyNDStructure(context) { get(it) }
    }
}

inline fun <T, F : Field<T>> LazyNDStructure<T, F>.mapIndexed(crossinline action: suspend F.(IntArray, T) -> T) = LazyNDStructure(context) { index ->
    action.invoke(this, index, await(index))
}

inline fun <T, F : Field<T>> LazyNDStructure<T, F>.map(crossinline action: suspend F.(T) -> T) = LazyNDStructure(context) { index ->
    action.invoke(this, await(index))
}