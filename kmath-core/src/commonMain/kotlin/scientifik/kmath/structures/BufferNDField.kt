package scientifik.kmath.structures

import scientifik.kmath.operations.Field


/**
 * A generic buffer for both primitives and objects
 */
interface Buffer<T> {
    operator fun get(index: Int): T
    operator fun set(index: Int, value: T)
}

/**
 * Generic implementation of NDField based on continuous buffer
 */
abstract class BufferNDField<T>(shape: List<Int>, field: Field<T>) : NDField<T>(shape, field) {

    /**
     * Strides for memory access
     */
    private val strides: List<Int> by lazy {
        ArrayList<Int>(shape.size).apply {
            var current = 1
            add(1)
            shape.forEach {
                current *= it
                add(current)
            }
        }
    }

    protected fun offset(index: List<Int>): Int {
        return index.mapIndexed { i, value ->
            if (value < 0 || value >= shape[i]) {
                throw RuntimeException("Index out of shape bounds: ($i,$value)")
            }
            value * strides[i]
        }.sum()
    }

    //TODO introduce a fast way to calculate index of the next element?
    protected fun index(offset: Int): List<Int> {
        return sequence {
            var current = offset
            var strideIndex = strides.size - 2
            while (strideIndex >= 0) {
                yield(current / strides[strideIndex])
                current %= strides[strideIndex]
                strideIndex--
            }
        }.toList().reversed()
    }

    private val capacity: Int
        get() = strides[shape.size]


    protected abstract fun createBuffer(capacity: Int, initializer: (Int) -> T): Buffer<T>

    override fun produce(initializer: (List<Int>) -> T): NDArray<T> {
        val buffer = createBuffer(capacity) { initializer(index(it)) }
        return BufferNDArray(this, buffer)
    }


    class BufferNDArray<T>(override val context: BufferNDField<T>, val data: Buffer<T>) : NDArray<T> {

        override fun get(vararg index: Int): T {
            return data[context.offset(index.asList())]
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is BufferNDArray<*>) return false

            if (context != other.context) return false
            if (data != other.data) return false

            return true
        }

        override fun hashCode(): Int {
            var result = context.hashCode()
            result = 31 * result + data.hashCode()
            return result
        }

        override val self: NDArray<T> get() = this
    }
}


