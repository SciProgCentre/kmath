package kscience.kmath.structures

import kotlin.jvm.JvmName
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

/**
 * Represents n-dimensional structure, i.e. multidimensional container of items of the same type and size. The number
 * of dimensions and items in an array is defined by its shape, which is a sequence of non-negative integers that
 * specify the sizes of each dimension.
 *
 * @param T the type of items.
 */
public interface NDStructure<T> {
    /**
     * The shape of structure, i.e. non-empty sequence of non-negative integers that specify sizes of dimensions of
     * this structure.
     */
    public val shape: IntArray

    /**
     * The count of dimensions in this structure. It should be equal to size of [shape].
     */
    public val dimension: Int get() = shape.size

    /**
     * Returns the value at the specified indices.
     *
     * @param index the indices.
     * @return the value.
     */
    public operator fun get(index: IntArray): T

    /**
     * Returns the sequence of all the elements associated by their indices.
     *
     * @return the lazy sequence of pairs of indices to values.
     */
    public fun elements(): Sequence<Pair<IntArray, T>>

    public override fun equals(other: Any?): Boolean
    public override fun hashCode(): Int

    public companion object {
        /**
         * Indicates whether some [NDStructure] is equal to another one.
         */
        public fun equals(st1: NDStructure<*>, st2: NDStructure<*>): Boolean {
            if (st1 === st2) return true

            // fast comparison of buffers if possible
            if (st1 is NDBuffer && st2 is NDBuffer && st1.strides == st2.strides)
                return st1.buffer.contentEquals(st2.buffer)

            //element by element comparison if it could not be avoided
            return st1.elements().all { (index, value) -> value == st2[index] }
        }

        /**
         * Creates a NDStructure with explicit buffer factory.
         *
         * Strides should be reused if possible.
         */
        public fun <T> build(
            strides: Strides,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
            initializer: (IntArray) -> T,
        ): BufferNDStructure<T> =
            BufferNDStructure(strides, bufferFactory(strides.linearSize) { i -> initializer(strides.index(i)) })

        /**
         * Inline create NDStructure with non-boxing buffer implementation if it is possible
         */
        public inline fun <reified T : Any> auto(
            strides: Strides,
            crossinline initializer: (IntArray) -> T,
        ): BufferNDStructure<T> =
            BufferNDStructure(strides, Buffer.auto(strides.linearSize) { i -> initializer(strides.index(i)) })

        public inline fun <T : Any> auto(
            type: KClass<T>,
            strides: Strides,
            crossinline initializer: (IntArray) -> T,
        ): BufferNDStructure<T> =
            BufferNDStructure(strides, Buffer.auto(type, strides.linearSize) { i -> initializer(strides.index(i)) })

        public fun <T> build(
            shape: IntArray,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
            initializer: (IntArray) -> T,
        ): BufferNDStructure<T> = build(DefaultStrides(shape), bufferFactory, initializer)

        public inline fun <reified T : Any> auto(
            shape: IntArray,
            crossinline initializer: (IntArray) -> T,
        ): BufferNDStructure<T> =
            auto(DefaultStrides(shape), initializer)

        @JvmName("autoVarArg")
        public inline fun <reified T : Any> auto(
            vararg shape: Int,
            crossinline initializer: (IntArray) -> T,
        ): BufferNDStructure<T> =
            auto(DefaultStrides(shape), initializer)

        public inline fun <T : Any> auto(
            type: KClass<T>,
            vararg shape: Int,
            crossinline initializer: (IntArray) -> T,
        ): BufferNDStructure<T> =
            auto(type, DefaultStrides(shape), initializer)
    }
}

/**
 * Returns the value at the specified indices.
 *
 * @param index the indices.
 * @return the value.
 */
public operator fun <T> NDStructure<T>.get(vararg index: Int): T = get(index)

/**
 * Represents mutable [NDStructure].
 */
public interface MutableNDStructure<T> : NDStructure<T> {
    /**
     * Inserts an item at the specified indices.
     *
     * @param index the indices.
     * @param value the value.
     */
    public operator fun set(index: IntArray, value: T)
}

public inline fun <T> MutableNDStructure<T>.mapInPlace(action: (IntArray, T) -> T): Unit =
    elements().forEach { (index, oldValue) -> this[index] = action(index, oldValue) }

/**
 * A way to convert ND index to linear one and back.
 */
public interface Strides {
    /**
     * Shape of NDstructure
     */
    public val shape: IntArray

    /**
     * Array strides
     */
    public val strides: IntArray

    /**
     * The size of linear buffer to accommodate all elements of ND-structure corresponding to strides
     */
    public val linearSize: Int

    /**
     * Get linear index from multidimensional index
     */
    public fun offset(index: IntArray): Int = index.mapIndexed { i, value ->
        if (value < 0 || value >= this.shape[i])
            throw IndexOutOfBoundsException("Index $value out of shape bounds: (0,${this.shape[i]})")

        value * strides[i]
    }.sum()

    /**
     * Get multidimensional from linear
     */
    public fun index(offset: Int): IntArray

    // TODO introduce a fast way to calculate index of the next element?

    /**
     * Iterate over ND indices in a natural order
     */
    public fun indices(): Sequence<IntArray> = (0 until linearSize).asSequence().map { index(it) }
}

/**
 * Simple implementation of [Strides].
 */
public class DefaultStrides private constructor(override val shape: IntArray) : Strides {
    override val linearSize: Int
        get() = strides[shape.size]

    /**
     * Strides for memory access
     */
    override val strides: IntArray by lazy {
        sequence {
            var current = 1
            yield(1)

            shape.forEach {
                current *= it
                yield(current)
            }
        }.toList().toIntArray()
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultStrides) return false
        if (!shape.contentEquals(other.shape)) return false
        return true
    }

    override fun hashCode(): Int = shape.contentHashCode()

    @ThreadLocal
    public companion object {
        private val defaultStridesCache = HashMap<IntArray, Strides>()

        /**
         * Cached builder for default strides
         */
        public operator fun invoke(shape: IntArray): Strides =
            defaultStridesCache.getOrPut(shape) { DefaultStrides(shape) }
    }
}

/**
 * Trait for [NDStructure] over [Buffer].
 *
 * @param T the type of items
 * @param BufferImpl implementation of [Buffer].
 */
public abstract class NDBufferTrait<T, out BufferImpl : Buffer<T>, out StridesImpl: Strides> :
    NDStructure<T> {
    /**
     * The underlying buffer.
     */
    public abstract val buffer: BufferImpl

    /**
     * The strides to access elements of [Buffer] by linear indices.
     */
    public abstract val strides: StridesImpl

    override operator fun get(index: IntArray): T = buffer[strides.offset(index)]

    override val shape: IntArray get() = strides.shape

    override fun elements(): Sequence<Pair<IntArray, T>> = strides.indices().map { it to this[it] }

    public fun checkStridesBufferCompatibility(): Unit = require(strides.linearSize == buffer.size) {
        "Expected buffer side of ${strides.linearSize}, but found ${buffer.size}"
    }

    override fun hashCode(): Int {
        var result = strides.hashCode()
        result = 31 * result + buffer.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    override fun toString(): String {
        val bufferRepr: String = when (shape.size) {
            1 -> buffer.asSequence().joinToString(prefix = "[", postfix = "]", separator = ", ")
            2 -> (0 until shape[0]).joinToString(prefix = "[", postfix = "]", separator = ", ") { i ->
                (0 until shape[1]).joinToString(prefix = "[", postfix = "]", separator = ", ") { j ->
                    val offset = strides.offset(intArrayOf(i, j))
                    buffer[offset].toString()
                }
            }
            else -> "..."
        }
        return "NDBuffer(shape=${shape.contentToString()}, buffer=$bufferRepr)"
    }
}

/**
 * Trait for [MutableNDStructure] over [MutableBuffer].
 *
 * @param T the type of items
 * @param MutableBufferImpl implementation of [MutableBuffer].
 */
public abstract class MutableNDBufferTrait<T, out MutableBufferImpl : MutableBuffer<T>, out StridesImpl: Strides> :
    NDBufferTrait<T, MutableBufferImpl, StridesImpl>(), MutableNDStructure<T> {
    override fun hashCode(): Int = 0
    override fun equals(other: Any?): Boolean = false
    override operator fun set(index: IntArray, value: T): Unit =
        buffer.set(strides.offset(index), value)
}

/**
 * Default representation of [NDStructure] over [Buffer].
 *
 * @param T the type of items.
 */
public abstract class NDBuffer<T> : NDBufferTrait<T, Buffer<T>, Strides>()

/**
 * Default representation of [MutableNDStructure] over [MutableBuffer].
 *
 * @param T the type of items.
 */
public abstract class MutableNDBuffer<T> : MutableNDBufferTrait<T, MutableBuffer<T>, Strides>()

/**
 * Boxing generic [NDStructure]
 */
public class BufferNDStructure<T>(
    override val strides: Strides,
    override val buffer: Buffer<T>,
) : NDBuffer<T>() {
    init {
        checkStridesBufferCompatibility()
    }
}

/**
 * Transform structure to a new structure using provided [BufferFactory] and optimizing if argument is [BufferNDStructure]
 */
public inline fun <T, reified R : Any> NDStructure<T>.mapToBuffer(
    factory: BufferFactory<R> = Buffer.Companion::auto,
    crossinline transform: (T) -> R,
): BufferNDStructure<R> {
    return if (this is BufferNDStructure<T>)
        BufferNDStructure(this.strides, factory.invoke(strides.linearSize) { transform(buffer[it]) })
    else {
        val strides = DefaultStrides(shape)
        BufferNDStructure(strides, factory.invoke(strides.linearSize) { transform(get(strides.index(it))) })
    }
}

/**
 * Boxing generic [MutableNDStructure].
 */
public class MutableBufferNDStructure<T>(
    override val strides: Strides,
    override val buffer: MutableBuffer<T>,
) : MutableNDBuffer<T>() {
    init {
        checkStridesBufferCompatibility()
    }
}

public inline fun <reified T : Any> NDStructure<T>.combine(
    struct: NDStructure<T>,
    crossinline block: (T, T) -> T,
): NDStructure<T> {
    require(shape.contentEquals(struct.shape)) { "Shape mismatch in structure combination" }
    return NDStructure.auto(shape) { block(this[it], struct[it]) }
}
