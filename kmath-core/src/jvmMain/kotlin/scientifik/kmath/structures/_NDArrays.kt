package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import java.nio.DoubleBuffer

private class RealNDField(shape: List<Int>) : BufferNDField<Double>(shape, DoubleField) {
    override fun createBuffer(capacity: Int, initializer: (Int) -> Double): Buffer<Double> {
        val array = DoubleArray(capacity, initializer)
        val buffer = DoubleBuffer.wrap(array)
        return BufferOfDoubles(buffer)
    }

    private class BufferOfDoubles(val buffer: DoubleBuffer): Buffer<Double>{
        override fun get(index: Int): Double = buffer.get(index)

        override fun set(index: Int, value: Double) {
            buffer.put(index, value)
        }

        override fun copy(): Buffer<Double> {
            return BufferOfDoubles(buffer)
        }
    }
}

actual val realNDFieldFactory: NDFieldFactory<Double> = { shape -> RealNDField(shape) }