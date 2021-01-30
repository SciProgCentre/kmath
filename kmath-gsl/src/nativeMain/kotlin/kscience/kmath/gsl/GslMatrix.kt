package kscience.kmath.gsl

import kotlinx.cinterop.AutofreeScope
import kotlinx.cinterop.CStructVar
import kscience.kmath.linear.Matrix
import kscience.kmath.nd.NDStructure
import kscience.kmath.structures.asSequence

/**
 * Wraps gsl_matrix_* objects from GSL.
 */
public abstract class GslMatrix<T : Any, H : CStructVar> internal constructor(scope: AutofreeScope, owned: Boolean) :
    GslObject<H>(scope, owned), Matrix<T> {
    internal abstract operator fun set(i: Int, j: Int, value: T)
    internal abstract fun copy(): GslMatrix<T, H>

    public override fun equals(other: Any?): Boolean {
        return NDStructure.contentEquals(this, other as? NDStructure<*> ?: return false)
    }

    public override fun hashCode(): Int {
        var ret = 7
        ret = ret * 31 + rowNum
        ret = ret * 31 + colNum

        for (row in 0 until rowNum)
            for (col in 0 until colNum)
                ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * this[row, col].hashCode()

        return ret
    }

    public override fun toString(): String = if (rowNum <= 5 && colNum <= 5)
        "Matrix(rowsNum = $rowNum, colNum = $colNum)\n" +
                rows.asSequence().joinToString(prefix = "(", postfix = ")", separator = "\n ") { buffer ->
                    buffer.asSequence().joinToString(separator = "\t") { it.toString() }
                }
    else
        "Matrix(rowsNum = $rowNum, colNum = $colNum)"
}
