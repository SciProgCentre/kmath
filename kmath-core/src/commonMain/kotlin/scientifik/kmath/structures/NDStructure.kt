package scientifik.kmath.structures

import kotlin.jvm.JvmName
import kotlin.reflect.KClass


interface NDStructure<T> {

    val shape: IntArray

    val dimension get() = shape.size

    operator fun get(index: IntArray): T

    fun elements(): Sequence<Pair<IntArray, T>>

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    companion object {
        fun equals(st1: NDStructure<*>, st2: NDStructure<*>): Boolean {
            if (st1 === st2) return true

            // fast comparison of buffers if possible
            if (
                st1 is NDBuffer &&
                st2 is NDBuffer &&
                st1.strides == st2.strides
            ) {
                return st1.buffer.contentEquals(st2.buffer)
            }

            //element by element comparison if it could not be avoided
            return st1.elements().all { (index, value) -> value == st2[index] }
        }

        /**
         * Create a NDStructure with explicit buffer factory
         *
         * Strides should be reused if possible
         */
        fun <T> build(
            strides: Strides,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
            initializer: (IntArray) -> T
        ) =
            BufferNDStructure(strides, bufferFactory(strides.linearSize) { i -> initializer(strides.index(i)) })

        /**
         * Inline create NDStructure with non-boxing buffer implementation if it is possible
         */
        inline fun <reified T : Any> auto(strides: Strides, crossinline initializer: (IntArray) -> T) =
            BufferNDStructure(strides, Buffer.auto(strides.linearSize) { i -> initializer(strides.index(i)) })

        inline fun <T : Any> auto(type: KClass<T>, strides: Strides, crossinline initializer: (IntArray) -> T) =
            BufferNDStructure(strides, Buffer.auto(type, strides.linearSize) { i -> initializer(strides.index(i)) })

        fun <T> build(
            shape: IntArray,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
            initializer: (IntArray) -> T
        ) = build(DefaultStrides(shape), bufferFactory, initializer)

        inline fun <reified T : Any> auto(shape: IntArray, crossinline initializer: (IntArray) -> T) =
            auto(DefaultStrides(shape), initializer)

        @JvmName("autoVarArg")
        inline fun <reified T : Any> auto(vararg shape: Int, crossinline initializer: (IntArray) -> T) =
            auto(DefaultStrides(shape), initializer)

        inline fun <T : Any> auto(type: KClass<T>, vararg shape: Int, crossinline initializer: (IntArray) -> T) =
            auto(type, DefaultStrides(shape), initializer)
    }
}

operator fun <T> NDStructure<T>.get(vararg index: Int): T = get(index)

interface MutableNDStructure<T> : NDStructure<T> {
    operator fun set(index: IntArray, value: T)
}

inline fun <T> MutableNDStructure<T>.mapInPlace(action: (IntArray, T) -> T) {
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


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultStrides) return false

        if (!shape.contentEquals(other.shape)) return false

        return true
    }

    override fun hashCode(): Int {
        return shape.contentHashCode()
    }

    companion object {
        private val defaultStridesCache = HashMap<IntArray, Strides>()

        /**
         * Cached builder for default strides
         */
        operator fun invoke(shape: IntArray): Strides = defaultStridesCache.getOrPut(shape) { DefaultStrides(shape) }
    }
}

abstract class NDBuffer<T> : NDStructure<T> {
    abstract val buffer: Buffer<T>
    abstract val strides: Strides

    override fun get(index: IntArray): T = buffer[strides.offset(index)]

    override val shape: IntArray get() = strides.shape

    override fun elements(): Sequence<Pair<IntArray, T>> = strides.indices().map { it to this[it] }

    override fun equals(other: Any?): Boolean {
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    override fun hashCode(): Int {
        var result = strides.hashCode()
        result = 31 * result + buffer.hashCode()
        return result
    }
}

/**
 * Boxing generic [NDStructure]
 */
class BufferNDStructure<T>(
    override val strides: Strides,
    override val buffer: Buffer<T>
) : NDBuffer<T>() {
    init {
        if (strides.linearSize != buffer.size) {
            error("Expected buffer side of ${strides.linearSize}, but found ${buffer.size}")
        }
    }
}

/**
 * Transform structure to a new structure using provided [BufferFactory] and optimizing if argument is [BufferNDStructure]
 */
inline fun <T, reified R : Any> NDStructure<T>.mapToBuffer(
    factory: BufferFactory<R> = Buffer.Companion::auto,
    crossinline transform: (T) -> R
): BufferNDStructure<R> {
    return if (this is BufferNDStructure<T>) {
        BufferNDStructure(this.strides, factory.invoke(strides.linearSize) { transform(buffer[it]) })
    } else {
        val strides = DefaultStrides(shape)
        BufferNDStructure(strides, factory.invoke(strides.linearSize) { transform(get(strides.index(it))) })
    }
}

/**
 * Mutable ND buffer based on linear [autoBuffer]
 */
class MutableBufferNDStructure<T>(
    override val strides: Strides,
    override val buffer: MutableBuffer<T>
) : NDBuffer<T>(), MutableNDStructure<T> {

    init {
        if (strides.linearSize != buffer.size) {
            error("Expected buffer side of ${strides.linearSize}, but found ${buffer.size}")
        }
    }

    override fun set(index: IntArray, value: T) = buffer.set(strides.offset(index), value)
}

inline fun <reified T : Any> NDStructure<T>.combine(
    struct: NDStructure<T>,
    crossinline block: (T, T) -> T
): NDStructure<T> {
    if (!this.shape.contentEquals(struct.shape)) error("Shape mismatch in structure combination")
    return NDStructure.auto(shape) { block(this[it], struct[it]) }
}