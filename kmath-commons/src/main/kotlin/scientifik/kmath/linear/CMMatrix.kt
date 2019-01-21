package scientifik.kmath.linear

import org.apache.commons.math3.linear.*
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import scientifik.kmath.operations.RealField
import scientifik.kmath.structures.DoubleBuffer

inline class CMMatrix(val origin: RealMatrix) : Matrix<Double> {
    override val rowNum: Int get() = origin.rowDimension
    override val colNum: Int get() = origin.columnDimension

    override val features: Set<MatrixFeature> get() = emptySet()

    override fun get(i: Int, j: Int): Double = origin.getEntry(i, j)
}

fun Matrix<Double>.toCM(): CMMatrix = if (this is CMMatrix) {
    this
} else {
    //TODO add feature analysis
    val array = Array(rowNum) { i -> DoubleArray(colNum) { j -> get(i, j) } }
    CMMatrix(Array2DRowRealMatrix(array))
}

fun RealMatrix.toMatrix() = CMMatrix(this)

inline class CMVector(val origin: RealVector) : Point<Double> {
    override val size: Int get() = origin.dimension

    override fun get(index: Int): Double = origin.getEntry(index)

    override fun iterator(): Iterator<Double> = origin.toArray().iterator()
}

fun Point<Double>.toCM(): CMVector = if (this is CMVector) {
    this
} else {
    val array = DoubleArray(size) { this[it] }
    CMVector(ArrayRealVector(array))
}

fun RealVector.toPoint() = DoubleBuffer(this.toArray())

object CMMatrixContext : MatrixContext<Double, RealField> {
    override val elementContext: RealField get() = RealField

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> Double): Matrix<Double> {
        val array = Array(rows) { i -> DoubleArray(columns) { j -> initializer(i, j) } }
        return CMMatrix(Array2DRowRealMatrix(array))
    }

    override fun point(size: Int, initializer: (Int) -> Double): Point<Double> {
        val array = DoubleArray(size, initializer)
        return CMVector(ArrayRealVector(array))
    }

    override fun Matrix<Double>.dot(other: Matrix<Double>): Matrix<Double> = CMMatrix(this.toCM().origin.multiply(other.toCM().origin))
}

operator fun CMMatrix.plus(other: CMMatrix): CMMatrix = CMMatrix(this.origin.add(other.origin))
operator fun CMMatrix.minus(other: CMMatrix): CMMatrix = CMMatrix(this.origin.subtract(other.origin))

infix fun CMMatrix.dot(other: CMMatrix): CMMatrix = CMMatrix(this.origin.multiply(other.origin))

object CMSolver : LinearSolver<Double, RealField> {

    override fun solve(a: Matrix<Double>, b: Matrix<Double>): Matrix<Double> {
        val decomposition = LUDecomposition(a.toCM().origin)
        return decomposition.solver.solve(b.toCM().origin).toMatrix()
    }

    override fun solve(a: Matrix<Double>, b: Point<Double>): Point<Double> {
        val decomposition = LUDecomposition(a.toCM().origin)
        return decomposition.solver.solve(b.toCM().origin).toPoint()
    }

    override fun inverse(a: Matrix<Double>): Matrix<Double> {
        val decomposition = LUDecomposition(a.toCM().origin)
        return decomposition.solver.inverse.toMatrix()
    }

}