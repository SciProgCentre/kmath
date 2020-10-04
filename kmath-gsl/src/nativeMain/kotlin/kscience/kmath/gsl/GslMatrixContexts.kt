package kscience.kmath.gsl

import kotlinx.cinterop.CStructVar
import kscience.kmath.linear.MatrixContext
import kscience.kmath.linear.Point
import kscience.kmath.operations.invoke
import kscience.kmath.structures.Matrix
import org.gnu.gsl.*

private inline fun <T : Any, H : CStructVar> GslMatrix<T, H>.fill(initializer: (Int, Int) -> T): GslMatrix<T, H> =
    apply {
        (0 until rowNum).forEach { row -> (0 until colNum).forEach { col -> this[row, col] = initializer(row, col) } }
    }

public sealed class GslMatrixContext<T : Any, H : CStructVar> : MatrixContext<T> {
    @Suppress("UNCHECKED_CAST")
    public fun Matrix<T>.toGsl(): GslMatrix<T, H> =
        (if (this is GslMatrix<*, *>) this as GslMatrix<T, H> else produce(rowNum, colNum) { i, j -> get(i, j) }).copy()

    internal abstract fun produceDirty(rows: Int, columns: Int): GslMatrix<T, H>

    public override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): GslMatrix<T, H> =
        produceDirty(rows, columns).fill(initializer)
}

public object GslRealMatrixContext : GslMatrixContext<Double, gsl_matrix>() {
    public override fun produceDirty(rows: Int, columns: Int): GslMatrix<Double, gsl_matrix> =
        GslRealMatrix(requireNotNull(gsl_matrix_alloc(rows.toULong(), columns.toULong())))

    public override fun Matrix<Double>.dot(other: Matrix<Double>): GslMatrix<Double, gsl_matrix> {
        val g1 = toGsl()
        gsl_matrix_mul_elements(g1.nativeHandle, other.toGsl().nativeHandle)
        return g1
    }

    public override fun Matrix<Double>.dot(vector: Point<Double>): GslVector<Double, gsl_vector> {
        TODO()
    }

    public override fun Matrix<Double>.times(value: Double): GslMatrix<Double, gsl_matrix> {
        val g1 = toGsl()
        gsl_matrix_scale(g1.nativeHandle, value)
        return g1
    }

    public override fun add(a: Matrix<Double>, b: Matrix<Double>): GslMatrix<Double, gsl_matrix> {
        val g1 = a.toGsl()
        gsl_matrix_add(g1.nativeHandle, b.toGsl().nativeHandle)
        return g1
    }

    public override fun multiply(a: Matrix<Double>, k: Number): GslMatrix<Double, gsl_matrix> {
        val g1 = a.toGsl()
        gsl_matrix_scale(g1.nativeHandle, k.toDouble())
        return g1
    }
}
