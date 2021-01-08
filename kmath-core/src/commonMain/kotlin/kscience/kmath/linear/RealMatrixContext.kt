package kscience.kmath.linear

import kscience.kmath.operations.RealField
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.MutableBuffer
import kscience.kmath.structures.MutableBufferFactory
import kscience.kmath.structures.RealBuffer

@Suppress("OVERRIDE_BY_INLINE")
public object RealMatrixContext : MatrixContext<Double, BufferMatrix<Double>> {

    public override inline fun produce(
        rows: Int,
        columns: Int,
        initializer: (i: Int, j: Int) -> Double,
    ): BufferMatrix<Double> {
        val buffer = RealBuffer(rows * columns) { offset -> initializer(offset / columns, offset % columns) }
        return BufferMatrix(rows, columns, buffer)
    }

    private fun Matrix<Double>.wrap(): BufferMatrix<Double> = if (this is BufferMatrix) this else {
        produce(rowNum, colNum) { i, j -> get(i, j) }
    }

    public fun one(rows: Int, columns: Int): FeaturedMatrix<Double> = VirtualMatrix(rows, columns, DiagonalFeature) { i, j ->
        if (i == j) 1.0 else 0.0
    }

    public override infix fun Matrix<Double>.dot(other: Matrix<Double>): BufferMatrix<Double> {
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }
        return produce(rowNum, other.colNum) { i, j ->
            var res = 0.0
            for (l in 0 until colNum) {
                res += get(i, l) * other.get(l, j)
            }
            res
        }
    }

    public override infix fun Matrix<Double>.dot(vector: Point<Double>): Point<Double> {
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }
        return RealBuffer(rowNum) { i ->
            var res = 0.0
            for (j in 0 until colNum) {
                res += get(i, j) * vector[j]
            }
            res
        }
    }

    override fun add(a: Matrix<Double>, b: Matrix<Double>): BufferMatrix<Double> {
        require(a.rowNum == b.rowNum) { "Row number mismatch in matrix addition. Left side: ${a.rowNum}, right side: ${b.rowNum}" }
        require(a.colNum == b.colNum) { "Column number mismatch in matrix addition. Left side: ${a.colNum}, right side: ${b.colNum}" }
        return produce(a.rowNum, a.colNum) { i, j ->
            a[i, j] + b[i, j]
        }
    }

    override fun Matrix<Double>.times(value: Double): BufferMatrix<Double> =
        produce(rowNum, colNum) { i, j -> get(i, j) * value }


    override fun multiply(a: Matrix<Double>, k: Number): BufferMatrix<Double> =
        produce(a.rowNum, a.colNum) { i, j -> a.get(i, j) * k.toDouble() }
}


/**
 * Partially optimized real-valued matrix
 */
public val MatrixContext.Companion.real: RealMatrixContext get() = RealMatrixContext

public fun RealMatrixContext.solveWithLUP(a: Matrix<Double>, b: Matrix<Double>): FeaturedMatrix<Double> {
    // Use existing decomposition if it is provided by matrix
    val bufferFactory: MutableBufferFactory<Double> = MutableBuffer.Companion::real
    val decomposition = a.getFeature() ?: lup(bufferFactory, RealField, a) { it < 1e-11 }
    return decomposition.solveWithLUP(bufferFactory, b)
}

/**
 * Inverses a square matrix using LUP decomposition. Non square matrix will throw a error.
 */
public fun RealMatrixContext.inverseWithLUP(matrix: Matrix<Double>): FeaturedMatrix<Double> =
    solveWithLUP(matrix, one(matrix.rowNum, matrix.colNum))
