/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.commons.linear

import org.apache.commons.math3.linear.*
import org.apache.commons.math3.linear.LUDecomposition
import space.kscience.attributes.SafeType
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.*
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.nd.StructureAttribute
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64Buffer
import kotlin.reflect.cast

public class CMMatrix(public val origin: RealMatrix) : Matrix<Double> {
    override val type: SafeType<Double> get() = DoubleField.type
    override val rowNum: Int get() = origin.rowDimension
    override val colNum: Int get() = origin.columnDimension

    override operator fun get(i: Int, j: Int): Double = origin.getEntry(i, j)
}

@JvmInline
public value class CMVector(public val origin: RealVector) : Point<Double> {
    override val type: SafeType<Double> get() = DoubleField.type
    override val size: Int get() = origin.dimension

    override operator fun get(index: Int): Double = origin.getEntry(index)

    override operator fun iterator(): Iterator<Double> = origin.toArray().iterator()

    override fun toString(): String = Buffer.toString(this)
}

public fun RealVector.toPoint(): CMVector = CMVector(this)

public object CMLinearSpace : LinearSpace<Double, Float64Field> {
    override val elementAlgebra: Float64Field get() = Float64Field

    override val type: SafeType<Double> get() = DoubleField.type

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float64Field.(i: Int, j: Int) -> Double,
    ): CMMatrix {
        val array = Array(rows) { i -> DoubleArray(columns) { j -> Float64Field.initializer(i, j) } }
        return CMMatrix(Array2DRowRealMatrix(array))
    }

    @OptIn(UnstableKMathAPI::class)
    public fun Matrix<Double>.toCM(): CMMatrix = when (val matrix = origin) {
        is CMMatrix -> matrix
        else -> {
            //TODO add feature analysis
            val array = Array(rowNum) { i -> DoubleArray(colNum) { j -> get(i, j) } }
            Array2DRowRealMatrix(array).wrap()
        }
    }

    public fun Point<Double>.toCM(): CMVector = if (this is CMVector) this else {
        val array = DoubleArray(size) { this[it] }
        ArrayRealVector(array).wrap()
    }

    internal fun RealMatrix.wrap(): CMMatrix = CMMatrix(this)
    internal fun RealVector.wrap(): CMVector = CMVector(this)

    override fun buildVector(size: Int, initializer: Float64Field.(Int) -> Double): Point<Double> =
        ArrayRealVector(DoubleArray(size) { Float64Field.initializer(it) }).wrap()

    override fun Matrix<Double>.plus(other: Matrix<Double>): CMMatrix =
        toCM().origin.add(other.toCM().origin).wrap()

    override fun Point<Double>.plus(other: Point<Double>): CMVector =
        toCM().origin.add(other.toCM().origin).wrap()

    override fun Point<Double>.minus(other: Point<Double>): CMVector =
        toCM().origin.subtract(other.toCM().origin).wrap()

    override fun Matrix<Double>.dot(other: Matrix<Double>): CMMatrix =
        toCM().origin.multiply(other.toCM().origin).wrap()

    override fun Matrix<Double>.dot(vector: Point<Double>): CMVector =
        toCM().origin.preMultiply(vector.toCM().origin).wrap()

    override operator fun Matrix<Double>.minus(other: Matrix<Double>): CMMatrix =
        toCM().origin.subtract(other.toCM().origin).wrap()

    override operator fun Matrix<Double>.times(value: Double): CMMatrix =
        toCM().origin.scalarMultiply(value).wrap()

    override fun Double.times(m: Matrix<Double>): CMMatrix =
        m * this

    override fun Point<Double>.times(value: Double): CMVector =
        toCM().origin.mapMultiply(value).wrap()

    override fun Double.times(v: Point<Double>): CMVector =
        v * this

    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Double>, attribute: A): V? {

        val origin = structure.toCM().origin

        return when (attribute) {
            IsDiagonal -> if (origin is DiagonalMatrix) IsDiagonal else null
            Determinant -> LUDecomposition(origin).determinant
            LUP -> GenericLupDecomposition {
                private val lup by lazy { LUDecomposition(origin) }
                override val determinant: Double by lazy { lup.determinant }
                override val l: Matrix<Double> by lazy<Matrix<Double>> { CMMatrix(lup.l).withAttribute(LowerTriangular) }
                override val u: Matrix<Double> by lazy<Matrix<Double>> { CMMatrix(lup.u).withAttribute(UpperTriangular) }
                override val p: Matrix<Double> by lazy { CMMatrix(lup.p) }
            }

            CholeskyDecompositionAttribute -> object : CholeskyDecompositionAttribute<Double> {
                override val l: Matrix<Double> by lazy<Matrix<Double>> {
                    val cholesky = CholeskyDecomposition(origin)
                    CMMatrix(cholesky.l).withAttribute(LowerTriangular)
                }
            }

            QRDecompositionAttribute -> object : QRDecompositionAttribute<Double> {
                private val qr by lazy { QRDecomposition(origin) }
                override val q: Matrix<Double> by lazy<Matrix<Double>> {
                    CMMatrix(qr.q).withAttribute(
                        OrthogonalAttribute
                    )
                }
                override val r: Matrix<Double> by lazy<Matrix<Double>> { CMMatrix(qr.r).withAttribute(UpperTriangular) }
            }

            SVDAttribute -> object : SVDAttribute<Double> {
                private val sv by lazy { SingularValueDecomposition(origin) }
                override val u: Matrix<Double> by lazy { CMMatrix(sv.u) }
                override val s: Matrix<Double> by lazy { CMMatrix(sv.s) }
                override val v: Matrix<Double> by lazy { CMMatrix(sv.v) }
                override val singularValues: Point<Double> by lazy { Float64Buffer(sv.singularValues) }
            }

            else -> null
        }?.let(type::cast)
    }
}

public operator fun CMMatrix.plus(other: CMMatrix): CMMatrix = CMMatrix(origin.add(other.origin))

public operator fun CMMatrix.minus(other: CMMatrix): CMMatrix = CMMatrix(origin.subtract(other.origin))

public infix fun CMMatrix.dot(other: CMMatrix): CMMatrix = CMMatrix(origin.multiply(other.origin))
