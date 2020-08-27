package scientifik.kmath.linear

import scientifik.kmath.structures.Matrix

class VirtualMatrix<T : Any>(
    override val rowNum: Int,
    override val colNum: Int,
    override val features: Set<MatrixFeature> = emptySet(),
    val generator: (i: Int, j: Int) -> T
) : FeaturedMatrix<T> {

    constructor(rowNum: Int, colNum: Int, vararg features: MatrixFeature, generator: (i: Int, j: Int) -> T) : this(
        rowNum,
        colNum,
        setOf(*features),
        generator
    )

    override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    override operator fun get(i: Int, j: Int): T = generator(i, j)

    override fun suggestFeature(vararg features: MatrixFeature): VirtualMatrix<T> =
        VirtualMatrix(rowNum, colNum, this.features + features, generator)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FeaturedMatrix<*>) return false

        if (rowNum != other.rowNum) return false
        if (colNum != other.colNum) return false

        return elements().all { (index, value) -> value == other[index] }
    }

    override fun hashCode(): Int {
        var result = rowNum
        result = 31 * result + colNum
        result = 31 * result + features.hashCode()
        result = 31 * result + generator.hashCode()
        return result
    }


    companion object {
        /**
         * Wrap a matrix adding additional features to it
         */
        fun <T : Any> wrap(matrix: Matrix<T>, vararg features: MatrixFeature): FeaturedMatrix<T> {
            return if (matrix is VirtualMatrix) {
                VirtualMatrix(matrix.rowNum, matrix.colNum, matrix.features + features, matrix.generator)
            } else {
                VirtualMatrix(matrix.rowNum, matrix.colNum, matrix.features + features) { i, j ->
                    matrix[i, j]
                }
            }
        }
    }
}
