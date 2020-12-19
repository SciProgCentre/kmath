package kscience.kmath.gsl

import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.DeferScope
import kscience.kmath.linear.FeaturedMatrix
import kscience.kmath.structures.NDStructure

/**
 * Wraps gsl_matrix_* objects from GSL.
 */
public abstract class GslMatrix<T : Any, H : CStructVar> internal constructor(scope: DeferScope) :
    GslMemoryHolder<H>(scope),
    FeaturedMatrix<T> {
    internal abstract operator fun set(i: Int, j: Int, value: T)
    internal abstract fun copy(): GslMatrix<T, H>

    public override fun equals(other: Any?): Boolean {
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    public override fun hashCode(): Int {
        var ret = 7
        val nRows = rowNum
        val nCols = colNum
        ret = ret * 31 + nRows
        ret = ret * 31 + nCols

        for (row in 0 until nRows)
            for (col in 0 until nCols)
                ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * this[row, col].hashCode()

        return ret
    }
}
