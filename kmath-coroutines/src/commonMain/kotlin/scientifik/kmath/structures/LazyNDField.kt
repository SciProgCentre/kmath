package scientifik.kmath.structures

import kotlinx.coroutines.*
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.FieldElement

class LazyNDField<T, F : Field<T>>(
    override val shape: IntArray,
    override val elementContext: F,
    val scope: CoroutineScope = GlobalScope
) :
    NDField<T, F, NDStructure<T>> {

    override val zero by lazy { produce { zero } }

    override val one by lazy { produce { one } }

    override fun produce(initializer: F.(IntArray) -> T) =
        LazyNDStructure(this) { elementContext.initializer(it) }

    override fun mapIndexed(
        arg: NDStructure<T>,
        transform: F.(index: IntArray, T) -> T
    ): LazyNDStructure<T, F> {
        check(arg)
        return if (arg is LazyNDStructure<T, *>) {
            LazyNDStructure(this) { index ->
                //FIXME if value of arg is already calculated, it should be used
                elementContext.transform(index, arg.function(index))
            }
        } else {
            LazyNDStructure(this) { elementContext.transform(it, arg.await(it)) }
        }
//        return LazyNDStructure(this) { elementField.transform(it, arg.await(it)) }
    }

    override fun map(arg: NDStructure<T>, transform: F.(T) -> T) =
        mapIndexed(arg) { _, t -> transform(t) }

    override fun combine(a: NDStructure<T>, b: NDStructure<T>, transform: F.(T, T) -> T): LazyNDStructure<T, F> {
        check(a, b)
        return if (a is LazyNDStructure<T, *> && b is LazyNDStructure<T, *>) {
            LazyNDStructure(this@LazyNDField) { index ->
                elementContext.transform(
                    a.function(index),
                    b.function(index)
                )
            }
        } else {
            LazyNDStructure(this@LazyNDField) { elementContext.transform(a.await(it), b.await(it)) }
        }
//        return LazyNDStructure(this) { elementField.transform(a.await(it), b.await(it)) }
    }

    fun NDStructure<T>.lazy(): LazyNDStructure<T, F> {
        check(this)
        return if (this is LazyNDStructure<T, *>) {
            LazyNDStructure(this@LazyNDField, function)
        } else {
            LazyNDStructure(this@LazyNDField) { get(it) }
        }
    }
}

class LazyNDStructure<T, F : Field<T>>(
    override val context: LazyNDField<T, F>,
    val function: suspend (IntArray) -> T
) : FieldElement<NDStructure<T>, LazyNDStructure<T, F>, LazyNDField<T, F>>, NDElement<T, F> {


    override fun unwrap(): NDStructure<T> = this

    override fun NDStructure<T>.wrap(): LazyNDStructure<T, F> = LazyNDStructure(context) { await(it) }

    override val shape: IntArray get() = context.shape
    override val elementField: F get() = context.elementContext

    override fun mapIndexed(transform: F.(index: IntArray, T) -> T): NDElement<T, F> =
        context.run { mapIndexed(this@LazyNDStructure, transform) }

    private val cache = HashMap<IntArray, Deferred<T>>()

    fun deferred(index: IntArray) = cache.getOrPut(index) {
        context.scope.async(context = Dispatchers.Math) {
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
    if (this is LazyNDStructure<T, *>) this.deferred(index) else CompletableDeferred(get(index))

suspend fun <T> NDStructure<T>.await(index: IntArray) =
    if (this is LazyNDStructure<T, *>) this.await(index) else get(index)


fun <T : Any, F : Field<T>> NDField.Companion.lazy(shape: IntArray, field: F, scope: CoroutineScope = GlobalScope) =
    LazyNDField(shape, field, scope)

fun <T, F : Field<T>> NDStructure<T>.lazy(field: F, scope: CoroutineScope = GlobalScope): LazyNDStructure<T, F> {
    val context: LazyNDField<T, F> = LazyNDField(shape, field, scope)
    return LazyNDStructure(context) { get(it) }
}