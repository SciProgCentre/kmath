package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Real
import scientifik.kmath.operations.RealField
import java.nio.DoubleBuffer

private class RealNDField(shape: List<Int>) : NDField<Real>(shape, RealField) {

    /**
     * Strides for memory access
     */
    private val strides: List<Int> by lazy {
        ArrayList<Int>(shape.size).apply {
            var current = 1
            shape.forEach{
                current *=it
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
        get() = strides[shape.size - 1]


    override fun produce(initializer: (List<Int>) -> Real): NDArray<Real> {
        //TODO use sparse arrays for large capacities
        val buffer = DoubleBuffer.allocate(capacity)
        NDArray.iterateIndexes(shape).forEach {
            buffer.put(offset(it), initializer(it).value)
        }
        return RealNDArray(buffer)
    }

    inner class RealNDArray(val data: DoubleBuffer) : NDArray<Real> {

        override val context: Field<NDArray<Real>>
            get() = this@RealNDField

        override fun get(vararg index: Int): Real {
            return Real(data.get(offset(index.asList())))
        }

        override val self: NDArray<Real>
            get() = this
    }

}


actual fun RealNDArray(shape: List<Int>, initializer: (List<Int>) -> Double): NDArray<Real> {
    //TODO cache fields?
    return RealNDField(shape).produce { Real(initializer(it)) }
}