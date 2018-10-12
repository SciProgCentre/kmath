package scientifik.kmath.structures

import scientifik.kmath.operations.Field

typealias NDFieldFactory<T> = (shape: List<Int>) -> NDField<T>

/**
 * The factory class for fast platform-dependent implementation of NDField of doubles
 */
expect val realNDFieldFactory: NDFieldFactory<Double>


class SimpleNDField<T : Any>(field: Field<T>, shape: List<Int>) : BufferNDField<T>(shape, field) {
    override fun createBuffer(capacity: Int, initializer: (Int) -> T): Buffer<T> {
        val array = ArrayList<T>(capacity)
        (0 until capacity).forEach {
            array.add(initializer(it))
        }

        return BufferOfObjects(array)
    }

    private class BufferOfObjects<T>(val array: ArrayList<T>) : Buffer<T> {
        override fun get(index: Int): T = array[index]

        override fun set(index: Int, value: T) {
            array[index] = value
        }

        override fun copy(): Buffer<T> = BufferOfObjects(ArrayList(array))
    }
}

object NDArrays {
    /**
     * Create a platform-optimized NDArray of doubles
     */
    fun realNDArray(shape: List<Int>, initializer: (List<Int>) -> Double = { 0.0 }): NDArray<Double> {
        return realNDFieldFactory(shape).produce(initializer)
    }

    fun real1DArray(dim: Int, initializer: (Int) -> Double = { _ -> 0.0 }): NDArray<Double> {
        return realNDArray(listOf(dim)) { initializer(it[0]) }
    }

    fun real2DArray(dim1: Int, dim2: Int, initializer: (Int, Int) -> Double = { _, _ -> 0.0 }): NDArray<Double> {
        return realNDArray(listOf(dim1, dim2)) { initializer(it[0], it[1]) }
    }

    fun real3DArray(dim1: Int, dim2: Int, dim3: Int, initializer: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }): NDArray<Double> {
        return realNDArray(listOf(dim1, dim2, dim3)) { initializer(it[0], it[1], it[2]) }
    }

    /**
     * Simple boxing NDField
     */
    fun <T : Any> createFactory(field: Field<T>): NDFieldFactory<T> = { shape -> SimpleNDField(field, shape) }

    /**
     * Simple boxing NDArray
     */
    fun <T : Any> create(field: Field<T>, shape: List<Int>, initializer: (List<Int>) -> T): NDArray<T> {
        return SimpleNDField(field, shape).produce { initializer(it) }
    }

    /**
     * Mutable boxing NDArray
     */
    fun <T : Any> createMutable(field: Field<T>, shape: List<Int>, initializer: (List<Int>) -> T): MutableNDArray<T> {
        return SimpleNDField(field, shape).produceMutable { initializer(it) }
    }
}