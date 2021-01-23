package kscience.kmath.commons.linear

import kscience.kmath.linear.DiagonalFeature
import kscience.kmath.linear.MatrixContext
import kscience.kmath.linear.Point
import kscience.kmath.linear.origin
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.structures.Matrix
import org.apache.commons.math3.linear.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

public inline class CMMatrix(public val origin: RealMatrix) : Matrix<Double> {
    public override val rowNum: Int get() = origin.rowDimension
    public override val colNum: Int get() = origin.columnDimension

    @UnstableKMathAPI
    override fun <T : Any> getFeature(type: KClass<T>): T? = when (type) {
        DiagonalFeature::class -> if (origin is DiagonalMatrix) DiagonalFeature else null
        else -> null
    }?.let { type.cast(it) }

    public override operator fun get(i: Int, j: Int): Double = origin.getEntry(i, j)
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

    @OptIn(UnstableKMathAPI::class)
    public fun Matrix<Double>.toCM(): CMMatrix = when (val matrix = origin) {
        is CMMatrix -> matrix
        else -> {
            //TODO add feature analysis
            val array = Array(rowNum) { i -> DoubleArray(colNum) { j -> get(i, j) } }
            CMMatrix(Array2DRowRealMatrix(array))
        }
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
