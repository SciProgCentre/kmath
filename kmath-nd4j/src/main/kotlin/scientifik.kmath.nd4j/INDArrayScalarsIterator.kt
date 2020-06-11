package scientifik.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.shape.Shape

internal class INDArrayScalarsIterator(private val iterateOver: INDArray) : Iterator<Pair<IntArray, INDArray>> {
    private var i: Int = 0

    override fun hasNext(): Boolean = i < iterateOver.length()

    override fun next(): Pair<IntArray, INDArray> {
        val idx = if (iterateOver.ordering() == 'c')
            Shape.ind2subC(iterateOver, i++.toLong())!!
        else
            Shape.ind2sub(iterateOver, i++.toLong())!!

        return narrowToIntArray(idx) to iterateOver.getScalar(*idx)
    }
}
