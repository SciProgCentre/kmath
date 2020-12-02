package kscience.kmath.commons.linear

import kscience.kmath.linear.*
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.NDStructure
import org.apache.commons.math3.linear.*

public class CMMatrix(public val origin: RealMatrix, features: Set<MatrixFeature>? = null) : FeaturedMatrix<Double> {
    public override val rowNum: Int get() = origin.rowDimension
    public override val colNum: Int get() = origin.columnDimension

    public override val features: Set<MatrixFeature> = features ?: sequence<MatrixFeature> {
        if (origin is DiagonalMatrix) yield(DiagonalFeature)
    }.toHashSet()

    public override fun suggestFeature(vararg features: MatrixFeature): CMMatrix =
        CMMatrix(origin, this.features + features)

    public override operator fun get(i: Int, j: Int): Double = origin.getEntry(i, j)

    public override fun equals(other: Any?): Boolean {
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    public override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }
}

public fun Matrix<Double>.toCM(): CMMatrix = if (this is CMMatrix) {
    this
} else {
    //TODO add feature analysis
    val array = Array(rowNum) { i -> DoubleArray(colNum) { j -> get(i, j) } }
    CMMatrix(Array2DRowRealMatrix(array))
}

public fun RealMatrix.asMatrix(): CMMatrix = CMMatrix(this)

public class CMVector(public val origin: RealVector) : Point<Double> {
    public override val size: Int get() = origin.dimension

    public override operator fun get(index: Int): Double = origin.getEntry(index)

    public override operator fun iterator(): Iterator<Double> = origin.toArray().iterator()
}

public fun Point<Double>.toCM(): CMVector = if (this is CMVector) this else {
    val array = DoubleArray(size) { this[it] }
    CMVector(ArrayRealVector(array))
}

public fun RealVector.toPoint(): CMVector = CMVector(this)

public object CMMatrixContext : MatrixContext<Double, CMMatrix> {
    public override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> Double): CMMatrix {
        val array = Array(rows) { i -> DoubleArray(columns) { j -> initializer(i, j) } }
        return CMMatrix(Array2DRowRealMatrix(array))
    }

    public override fun Matrix<Double>.dot(other: Matrix<Double>): CMMatrix =
        CMMatrix(toCM().origin.multiply(other.toCM().origin))

    public override fun Matrix<Double>.dot(vector: Point<Double>): CMVector =
        CMVector(toCM().origin.preMultiply(vector.toCM().origin))

    public override operator fun Matrix<Double>.unaryMinus(): CMMatrix =
        produce(rowNum, colNum) { i, j -> -get(i, j) }

    public override fun add(a: Matrix<Double>, b: Matrix<Double>): CMMatrix =
        CMMatrix(a.toCM().origin.multiply(b.toCM().origin))

    public override operator fun Matrix<Double>.minus(b: Matrix<Double>): CMMatrix =
        CMMatrix(toCM().origin.subtract(b.toCM().origin))

    public override fun multiply(a: Matrix<Double>, k: Number): CMMatrix =
        CMMatrix(a.toCM().origin.scalarMultiply(k.toDouble()))

    public override operator fun Matrix<Double>.times(value: Double): CMMatrix =
        produce(rowNum, colNum) { i, j -> get(i, j) * value }
}

public operator fun CMMatrix.plus(other: CMMatrix): CMMatrix =
    CMMatrix(origin.add(other.origin))

public operator fun CMMatrix.minus(other: CMMatrix): CMMatrix =
    CMMatrix(origin.subtract(other.origin))

public infix fun CMMatrix.dot(other: CMMatrix): CMMatrix =
    CMMatrix(origin.multiply(other.origin))
