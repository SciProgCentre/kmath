package scientifik.kmath.structures


interface NDStructure<T> {

    val shape: IntArray

    val dimension
        get() = shape.size

    operator fun get(index: IntArray): T

    fun elements(): Sequence<Pair<IntArray, T>>
}

operator fun <T> NDStructure<T>.get(vararg index: Int): T = get(index)

interface MutableNDStructure<T> : NDStructure<T> {
    operator fun set(index: IntArray, value: T)
}

fun <T> MutableNDStructure<T>.transformInPlace(action: (IntArray, T) -> T) {
    elements().forEach { (index, oldValue) ->
        this[index] = action(index, oldValue)
    }
}

/**
 * A way to convert ND index to linear one and back
 */
interface Strides {
    /**
     * Shape of NDstructure
     */
    val shape: IntArray

    /**
     * Array strides
     */
    val strides: List<Int>

    /**
     * Get linear index from multidimensional index
     */
    fun offset(index: IntArray): Int

    /**
     * Get multidimensional from linear
     */
    fun index(offset: Int): IntArray

    /**
     * The size of linear buffer to accommodate all elements of ND-structure corresponding to strides
     */
    val linearSize: Int

    /**
     * Iterate over ND indices in a natural order
     */
    fun indices(): Sequence<IntArray> {
        //TODO introduce a fast way to calculate index of the next element?
        return (0 until linearSize).asSequence().map { index(it) }
    }
}

class DefaultStrides private constructor(override val shape: IntArray) : Strides {
    /**
     * Strides for memory access
     */
    override val strides by lazy {
        sequence {
            var current = 1
            yield(1)
            shape.forEach {
                current *= it
                yield(current)
            }
        }.toList()
    }

    override fun offset(index: IntArray): Int {
        return index.mapIndexed { i, value ->
            if (value < 0 || value >= this.shape[i]) {
                throw RuntimeException("Index $value out of shape bounds: (0,${this.shape[i]})")
            }
            value * strides[i]
        }.sum()
    }

    override fun index(offset: Int): IntArray {
        val res = IntArray(shape.size)
        var current = offset
        var strideIndex = strides.size - 2
        while (strideIndex >= 0) {
            res[strideIndex] = (current / strides[strideIndex])
            current %= strides[strideIndex]
            strideIndex--
        }
        return res
    }

    override val linearSize: Int
        get() = strides[shape.size]

    companion object {
        private val defaultStridesCache = HashMap<IntArray, Strides>()

        /**
         * Cached builder for default strides
         */
        operator fun invoke(shape: IntArray): Strides = defaultStridesCache.getOrPut(shape) { DefaultStrides(shape) }
    }
}

abstract class GenericNDStructure<T, B : Buffer<T>> : NDStructure<T> {
    protected abstract val buffer: B
    protected abstract val strides: Strides

    override fun get(index: IntArray): T = buffer[strides.offset(index)]

    override val shape: IntArray
        get() = strides.shape

    override fun elements() =
            strides.indices().map { it to this[it] }
}

/**
 * Boxing generic [NDStructure]
 */
class BufferNDStructure<T>(
        override val strides: Strides,
        override val buffer: Buffer<T>
) : GenericNDStructure<T, Buffer<T>>() {

    init {
        if (strides.linearSize != buffer.size) {
            error("Expected buffer side of ${strides.linearSize}, but found ${buffer.size}")
        }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other is BufferNDStructure<*> -> this.strides == other.strides && this.buffer.contentEquals(other.buffer)
            other is NDStructure<*> -> elements().all { (index, value) -> value == other[index] }
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = strides.hashCode()
        result = 31 * result + buffer.hashCode()
        return result
    }

}

/**
 * Create a NDStructure with explicit buffer factory
 *
 * Strides should be reused if possible
 */
@Suppress("FunctionName")
fun <T : Any> NdStructure(strides: Strides, bufferFactory: BufferFactory<T>, initializer: (IntArray) -> T) =
        BufferNDStructure(strides, bufferFactory(strides.linearSize) { i -> initializer(strides.index(i)) })

/**
 * Inline create NDStructure with non-boxing buffer implementation if it is possible
 */
inline fun <reified T : Any> inlineNDStructure(strides: Strides, crossinline initializer: (IntArray) -> T) =
        BufferNDStructure(strides, inlineBuffer(strides.linearSize) { i -> initializer(strides.index(i)) })

@Suppress("FunctionName")
fun <T : Any> NdStructure(shape: IntArray, bufferFactory: BufferFactory<T>, initializer: (IntArray) -> T) =
        NdStructure(DefaultStrides(shape), bufferFactory, initializer)

inline fun <reified T : Any> inlineNdStructure(shape: IntArray, crossinline initializer: (IntArray) -> T) =
        inlineNDStructure(DefaultStrides(shape), initializer)

/**
 * Mutable ND buffer based on linear [inlineBuffer]
 */
class MutableBufferNDStructure<T>(
        override val strides: Strides,
        override val buffer: MutableBuffer<T>
) : GenericNDStructure<T, MutableBuffer<T>>(), MutableNDStructure<T> {

    init {
        if (strides.linearSize != buffer.size) {
            error("Expected buffer side of ${strides.linearSize}, but found ${buffer.size}")
        }
    }

    override fun set(index: IntArray, value: T) = buffer.set(strides.offset(index), value)
}

/**
 * The same as [inlineNDStructure], but mutable
 */
@Suppress("FunctionName")
fun <T : Any> MutableNdStructure(strides: Strides, bufferFactory: MutableBufferFactory<T>, initializer: (IntArray) -> T) =
        MutableBufferNDStructure(strides, bufferFactory(strides.linearSize) { i -> initializer(strides.index(i)) })

inline fun <reified T : Any> inlineMutableNdStructure(strides: Strides, crossinline initializer: (IntArray) -> T) =
        MutableBufferNDStructure(strides, inlineMutableBuffer(strides.linearSize) { i -> initializer(strides.index(i)) })

@Suppress("FunctionName")
fun <T : Any> MutableNdStructure(shape: IntArray, bufferFactory: MutableBufferFactory<T>, initializer: (IntArray) -> T) =
        MutableNdStructure(DefaultStrides(shape), bufferFactory, initializer)

inline fun <reified T : Any> inlineMutableNdStructure(shape: IntArray, crossinline initializer: (IntArray) -> T) =
        inlineMutableNdStructure(DefaultStrides(shape), initializer)

inline fun <reified T : Any> NDStructure<T>.combine(struct: NDStructure<T>, crossinline block: (T, T) -> T): NDStructure<T> {
    if (!this.shape.contentEquals(struct.shape)) error("Shape mismatch in structure combination")
    return inlineNdStructure(shape) { block(this[it], struct[it]) }
}


///**
// * Create universal mutable structure
// */
//fun <T> genericNdStructure(shape: IntArray, initializer: (IntArray) -> T): MutableBufferNDStructure<T> {
//    val strides = DefaultStrides(shape)
//    val sequence = sequence {
//        strides.indices().forEach {
//            yield(initializer(it))
//        }
//    }
//    val buffer = MutableListBuffer(sequence.toMutableList())
//    return MutableBufferNDStructure(strides, buffer)
//}
