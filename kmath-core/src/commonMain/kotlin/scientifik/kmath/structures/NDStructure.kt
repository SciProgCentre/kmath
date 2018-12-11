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

    val linearSize: Int

    /**
     * Iterate over ND indices in a natural order
     */
    fun indices(): Sequence<IntArray> {
        //TODO introduce a fast way to calculate index of the next element?
        return (0 until linearSize).asSequence().map { index(it) }
    }
}

class DefaultStrides(override val shape: IntArray) : Strides {
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
            if (value < 0 || value >= shape[i]) {
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
}

abstract class GenericNDStructure<T, B : Buffer<T>> : NDStructure<T> {
    protected abstract val buffer: B
    protected abstract val strides: Strides

    override fun get(index: IntArray): T = buffer[strides.offset(index)]

    override val shape: IntArray
        get() = strides.shape

    override fun elements()=
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
}

inline fun <reified T : Any> ndStructure(strides: Strides, noinline initializer: (IntArray) -> T) =
        BufferNDStructure<T>(strides, buffer(strides.linearSize) { i -> initializer(strides.index(i)) })

inline fun <reified T : Any> ndStructure(shape: IntArray, noinline initializer: (IntArray) -> T) =
        ndStructure(DefaultStrides(shape), initializer)


/**
 * Mutable ND buffer based on linear [Buffer]
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
 * Create optimized mutable structure for given type
 */
inline fun <reified T : Any> mutableNdStructure(strides: Strides, noinline initializer: (IntArray) -> T) =
        MutableBufferNDStructure(strides, mutableBuffer(strides.linearSize) { i -> initializer(strides.index(i)) })

inline fun <reified T : Any> mutableNdStructure(shape: IntArray, noinline initializer: (IntArray) -> T) =
        mutableNdStructure(DefaultStrides(shape), initializer)

/**
 * Create universal mutable structure
 */
fun <T> genericNdStructure(shape: IntArray, initializer: (IntArray) -> T): MutableBufferNDStructure<T> {
    val strides = DefaultStrides(shape)
    val sequence = sequence {
        strides.indices().forEach {
            yield(initializer(it))
        }
    }
    val buffer = MutableListBuffer(sequence.toMutableList())
    return MutableBufferNDStructure(strides, buffer)
}
