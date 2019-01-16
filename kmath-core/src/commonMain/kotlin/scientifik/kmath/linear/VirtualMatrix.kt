package scientifik.kmath.linear

class VirtualMatrix<T : Any>(
    override val rowNum: Int,
    override val colNum: Int,
    override val features: Set<MatrixFeature> = emptySet(),
    val generator: (i: Int, j: Int) -> T
) : Matrix<T> {
    override fun get(i: Int, j: Int): T = generator(i, j)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix<*>) return false

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


}