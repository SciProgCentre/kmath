package scientifik.kmath.linear

import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.BufferFactory
import scientifik.kmath.structures.NDStructure
import scientifik.kmath.structures.get
import scientifik.kmath.structures.ndStructure

/**
 * Basic implementation of Matrix space based on [NDStructure]
 */
class StructureMatrixContext<T : Any, R : Ring<T>>(
    override val elementContext: R,
    private val bufferFactory: BufferFactory<T>
) : MatrixContext<T, R> {

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): Matrix<T> {
        val structure =
            ndStructure(intArrayOf(rows, columns), bufferFactory) { index -> initializer(index[0], index[1]) }
        return StructureMatrix(structure)
    }

    override fun point(size: Int, initializer: (Int) -> T): Point<T> = bufferFactory(size, initializer)
}

class StructureMatrix<T : Any>(
    val structure: NDStructure<T>,
    override val features: Set<MatrixFeature> = emptySet()
) : Matrix<T> {

    init {
        if (structure.shape.size != 2) {
            error("Dimension mismatch for matrix structure")
        }
    }

    override val rowNum: Int
        get() = structure.shape[0]
    override val colNum: Int
        get() = structure.shape[1]


    override val shape: IntArray get() = structure.shape

    override fun get(index: IntArray): T = structure[index]

    override fun get(i: Int, j: Int): T = structure[i, j]

    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            is StructureMatrix<*> -> return this.structure == other.structure
            is Matrix<*> -> elements().all { (index, value) -> value == other[index] }
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = structure.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }


}