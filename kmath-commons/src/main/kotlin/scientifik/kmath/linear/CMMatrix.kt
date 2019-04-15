package scientifik.kmath.linear

import org.apache.commons.math3.linear.*
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import scientifik.kmath.structures.Matrix

class CMMatrix(val origin: RealMatrix, features: Set<MatrixFeature>? = null) : FeaturedMatrix<Double> {
    override val rowNum: Int get() = origin.rowDimension
    override val colNum: Int get() = origin.columnDimension

    override val features: Set<MatrixFeature> = features ?: sequence<MatrixFeature> {
        if (origin is DiagonalMatrix) yield(DiagonalFeature)
    }.toSet()

    override fun suggestFeature(vararg features: MatrixFeature) =
        CMMatrix(origin, this.features + features)

    override fun get(i: Int, j: Int): Double = origin.getEntry(i, j)
}

fun Matrix<Double>.toCM(): CMMatrix = if (this is CMMatrix) {
    this
} else {
    //TODO add feature analysis
    val array = Array(rowNum) { i -> DoubleArray(colNum) { j -> get(i, j) } }
    CMMatrix(Array2DRowRealMatrix(array))
}

fun RealMatrix.asMatrix() = CMMatrix(this)

class CMVector(val origin: RealVector) : Point<Double> {
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

fun RealVector.toPoint() = CMVector(this)

object CMMatrixContext : MatrixContext<Double> {
    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> Double): CMMatrix {
        val array = Array(rows) { i -> DoubleArray(columns) { j -> initializer(i, j) } }
        return CMMatrix(Array2DRowRealMatrix(array))
    }

    override fun Matrix<Double>.dot(other: Matrix<Double>) =
        CMMatrix(this.toCM().origin.multiply(other.toCM().origin))

    override fun Matrix<Double>.dot(vector: Point<Double>): CMVector =
        CMVector(this.toCM().origin.preMultiply(vector.toCM().origin))

    override fun Matrix<Double>.unaryMinus(): CMMatrix =
        produce(rowNum, colNum) { i, j -> -get(i, j) }

    override fun add(a: Matrix<Double>, b: Matrix<Double>) =
        CMMatrix(a.toCM().origin.multiply(b.toCM().origin))

    override fun Matrix<Double>.minus(b: Matrix<Double>) =
        CMMatrix(this.toCM().origin.subtract(b.toCM().origin))

    override fun multiply(a: Matrix<Double>, k: Number) =
        CMMatrix(a.toCM().origin.scalarMultiply(k.toDouble()))

    override fun Matrix<Double>.times(value: Double): Matrix<Double>  = produce(rowNum,colNum){i,j-> get(i,j)*value}
}

operator fun CMMatrix.plus(other: CMMatrix): CMMatrix = CMMatrix(this.origin.add(other.origin))
operator fun CMMatrix.minus(other: CMMatrix): CMMatrix = CMMatrix(this.origin.subtract(other.origin))

infix fun CMMatrix.dot(other: CMMatrix): CMMatrix = CMMatrix(this.origin.multiply(other.origin))