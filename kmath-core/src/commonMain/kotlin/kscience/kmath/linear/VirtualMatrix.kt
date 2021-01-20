package kscience.kmath.linear

import kscience.kmath.structures.Matrix

public class VirtualMatrix<T : Any>(
    override val rowNum: Int,
    override val colNum: Int,
    public val generator: (i: Int, j: Int) -> T
) : Matrix<T> {

    override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    override operator fun get(i: Int, j: Int): T = generator(i, j)

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
        result = 31 * result + generator.hashCode()
        return result
    }


}
