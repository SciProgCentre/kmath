package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import java.nio.DoubleBuffer

private class RealNDField(shape: List<Int>) : NDField<Double>(shape, DoubleField) {

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

    fun offset(index: List<Int>): Int {
        return index.mapIndexed { i, value ->
            if (value < 0 || value >= shape[i]) {
                throw RuntimeException("Index out of shape bounds: ($i,$value)")
            }
            value * strides[i]
        }.sum()
    }

    val capacity: Int
        get() = strides[shape.size]


    override fun produce(initializer: (List<Int>) -> Double): NDArray<Double> {
        //TODO use sparse arrays for large capacities
        val buffer = DoubleBuffer.allocate(capacity)
        //FIXME there could be performance degradation due to iteration procedure. Replace by straight iteration
        NDArray.iterateIndexes(shape).forEach {
            buffer.put(offset(it), initializer(it))
        }
        return RealNDArray(this, buffer)
    }

    class RealNDArray(override val context: RealNDField, val data: DoubleBuffer) : NDArray<Double> {

        override fun get(vararg index: Int): Double {
            return data.get(context.offset(index.asList()))
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RealNDArray

            if (context.shape != other.context.shape) return false
            if (data != other.data) return false

            return true
        }

        override fun hashCode(): Int {
            var result = context.shape.hashCode()
            result = 31 * result + data.hashCode()
            return result
        }

        //TODO generate fixed hash code for quick comparison?


        override val self: NDArray<Double> = this
    }
}


actual fun realNDArray(shape: List<Int>, initializer: (List<Int>) -> Double): NDArray<Double> {
    //TODO cache fields?
    return RealNDField(shape).produce { initializer(it) }
}