package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import java.nio.DoubleBuffer

private class RealNDField(shape: List<Int>) : BufferNDField<Double>(shape, DoubleField) {
    override fun createBuffer(capacity: Int, initializer: (Int) -> Double): Buffer<Double> {
        val array = DoubleArray(capacity, initializer)
        val buffer = DoubleBuffer.wrap(array)
        return object : Buffer<Double> {
            override fun get(index: Int): Double = buffer.get(index)

            override fun set(index: Int, value: Double) {
                buffer.put(index, value)
            }
        }
    }
}

actual fun realNDArray(shape: List<Int>, initializer: (List<Int>) -> Double): NDArray<Double> {
    //TODO create a cache for fields to save time generating strides?

    return RealNDField(shape).produce { initializer(it) }
}