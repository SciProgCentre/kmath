package scientifik.kmath.structures

import scientifik.kmath.operations.Field

/**
 * Create a platform-optimized NDArray of doubles
 */
expect fun realNDArray(shape: List<Int>, initializer: (List<Int>) -> Double = { 0.0 }): NDArray<Double>

fun real2DArray(dim1: Int, dim2: Int, initializer: (Int, Int) -> Double = { _, _ -> 0.0 }): NDArray<Double> {
    return realNDArray(listOf(dim1, dim2)) { initializer(it[0], it[1]) }
}

fun real3DArray(dim1: Int, dim2: Int, dim3: Int, initializer: (Int, Int, Int) -> Double = { _, _, _ -> 0.0 }): NDArray<Double> {
    return realNDArray(listOf(dim1, dim2, dim3)) { initializer(it[0], it[1], it[2]) }
}


class SimpleNDField<T: Any>(field: Field<T>, shape: List<Int>) : BufferNDField<T>(shape, field) {
    override fun createBuffer(capacity: Int, initializer: (Int) -> T): Buffer<T> {
        val array = ArrayList<T>(capacity)
        (0 until capacity).forEach {
            array.add(initializer(it))
        }

        return object : Buffer<T> {
            override fun get(index: Int): T = array[index]

            override fun set(index: Int, value: T) {
                array[index] = initializer(index)
            }
        }
    }
}

fun <T: Any> simpleNDArray(field: Field<T>, shape: List<Int>, initializer: (List<Int>) -> T): NDArray<T> {
    return SimpleNDField(field, shape).produce { initializer(it) }
}