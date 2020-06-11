package scientifik.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import scientifik.kmath.structures.NDStructure

internal fun narrowToIntArray(la: LongArray): IntArray = IntArray(la.size) { la[it].toInt() }

data class ND4JStructure<T>(val ndArray: INDArray) : NDStructure<INDArray> {
    override val shape: IntArray
        get() = narrowToIntArray(ndArray.shape())

    override fun get(index: IntArray): INDArray = ndArray.getScalar(*index)
    override fun elements(): Sequence<Pair<IntArray, INDArray>> = Sequence { INDArrayScalarsIterator(ndArray) }
}
