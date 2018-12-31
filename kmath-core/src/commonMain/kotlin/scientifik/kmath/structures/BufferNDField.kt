package scientifik.kmath.structures

import scientifik.kmath.operations.Field

open class BufferNDField<T, F : Field<T>>(final override val shape: IntArray, final override val field: F, val bufferFactory: BufferFactory<T>) : NDField<T, F> {
    val strides = DefaultStrides(shape)

    override fun produce(initializer: F.(IntArray) -> T): BufferNDElement<T, F> {
        return BufferNDElement(this, bufferFactory(strides.linearSize) { offset -> field.initializer(strides.index(offset)) })
    }

    open fun produceBuffered(initializer: F.(Int) -> T) =
            BufferNDElement(this, bufferFactory(strides.linearSize) { offset -> field.initializer(offset) })

//    override fun add(a: NDStructure<T>, b: NDStructure<T>): NDElement<T, F> {
//        checkShape(a, b)
//        return if (a is BufferNDElement<T, *> && b is BufferNDElement<T, *>) {
//            BufferNDElement(this,bufferFactory(strides.linearSize){i-> field.run { a.buffer[i] + b.buffer[i]}})
//        } else {
//            produce { field.run { a[it] + b[it] } }
//        }
//    }
//
//    override fun NDStructure<T>.plus(b: Number): NDElement<T,F> {
//        checkShape(this)
//        return if (this is BufferNDElement<T, *>) {
//            BufferNDElement(this@BufferNDField,bufferFactory(strides.linearSize){i-> field.run { this@plus.buffer[i] + b}})
//        } else {
//            produce {index -> field.run { this@plus[index] + b } }
//        }
//    }
}

class BufferNDElement<T, F : Field<T>>(override val context: BufferNDField<T, F>, val buffer: Buffer<T>) : NDElement<T, F> {

    override val self: NDStructure<T> get() = this
    override val shape: IntArray get() = context.shape

    override fun get(index: IntArray): T = buffer[context.strides.offset(index)]

    override fun elements(): Sequence<Pair<IntArray, T>> = context.strides.indices().map { it to get(it) }

}


/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun <T : Any, F : Field<T>> Function1<T, T>.invoke(ndElement: BufferNDElement<T, F>) =
        ndElement.context.produceBuffered { i -> invoke(ndElement.buffer[i]) }

/* plus and minus */

/**
 * Summation operation for [BufferNDElement] and single element
 */
operator fun <T : Any, F : Field<T>> BufferNDElement<T, F>.plus(arg: T) =
        context.produceBuffered { i -> buffer[i] + arg }

/**
 * Subtraction operation between [BufferNDElement] and single element
 */
operator fun <T: Any, F : Field<T>> BufferNDElement<T, F>.minus(arg: T) =
        context.produceBuffered { i -> buffer[i] - arg }

/* prod and div */

/**
 * Product operation for [BufferNDElement] and single element
 */
operator fun <T: Any, F : Field<T>> BufferNDElement<T, F>.times(arg: T) =
        context.produceBuffered { i -> buffer[i] * arg }

/**
 * Division operation between [BufferNDElement] and single element
 */
operator fun <T: Any, F : Field<T>> BufferNDElement<T, F>.div(arg: T) =
        context.produceBuffered { i -> buffer[i] / arg }