/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.commons.linear

import org.apache.commons.math3.linear.*
import space.kscience.attributes.SafeType
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.*
import space.kscience.kmath.linear.CholeskyDecomposition
import space.kscience.kmath.linear.EigenDecomposition
import space.kscience.kmath.linear.QRDecomposition
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.nd.StructureAttribute
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.IntBuffer
import space.kscience.kmath.structures.asBuffer

@JvmInline
public value class CMMatrix(public val cmMatrix: RealMatrix) : Matrix<Float64> {

    override val rowNum: Int get() = cmMatrix.rowDimension
    override val colNum: Int get() = cmMatrix.columnDimension

    override operator fun get(i: Int, j: Int): Double = cmMatrix.getEntry(i, j)
}

@JvmInline
public value class CMVector(public val cmVector: RealVector) : Point<Float64> {
    override val size: Int get() = cmVector.dimension

    override operator fun get(index: Int): Double = cmVector.getEntry(index)

    override operator fun iterator(): Iterator<Float64> = cmVector.toArray().iterator()

    override fun toString(): String = Buffer.toString(this)
}

public object CMLinearSpace : LinearSpace<Double, Float64Field> {
    override val elementAlgebra: Float64Field get() = Float64Field

    override val type: SafeType<Float64> get() = DoubleField.type

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float64Field.(i: Int, j: Int) -> Double,
    ): CMMatrix {
        val array = Array(rows) { i -> DoubleArray(columns) { j -> Float64Field.initializer(i, j) } }
        return CMMatrix(Array2DRowRealMatrix(array))
    }

    @OptIn(UnstableKMathAPI::class)
    public fun Matrix<Float64>.toCM(): RealMatrix = when (val matrix = origin) {
        is CMMatrix -> matrix.cmMatrix
        else -> {
            //TODO add feature analysis
            val array = Array(rowNum) { i -> DoubleArray(colNum) { j -> get(i, j) } }
            Array2DRowRealMatrix(array)
        }
    }

    public fun Point<Float64>.toCM(): RealVector = if (this is CMVector) cmVector else {
        val array = DoubleArray(size) { get(it) }
        ArrayRealVector(array)
    }

    public fun RealMatrix.asMatrix(): CMMatrix = CMMatrix(this)
    public fun RealVector.asVector(): CMVector = CMVector(this)

    override fun buildVector(size: Int, initializer: Float64Field.(Int) -> Double): Point<Float64> =
        ArrayRealVector(DoubleArray(size) { Float64Field.initializer(it) }).asVector()

    override fun Matrix<Float64>.plus(other: Matrix<Float64>): CMMatrix =
        toCM().add(other.toCM()).asMatrix()

    override fun Point<Float64>.plus(other: Point<Float64>): CMVector =
        toCM().add(other.toCM()).asVector()

    override fun Point<Float64>.minus(other: Point<Float64>): CMVector =
        toCM().subtract(other.toCM()).asVector()

    override fun Matrix<Float64>.dot(other: Matrix<Float64>): CMMatrix =
        toCM().multiply(other.toCM()).asMatrix()

    override fun Matrix<Float64>.dot(vector: Point<Float64>): CMVector =
        toCM().preMultiply(vector.toCM()).asVector()

    override operator fun Matrix<Float64>.minus(other: Matrix<Float64>): CMMatrix =
        toCM().subtract(other.toCM()).asMatrix()

    override operator fun Matrix<Float64>.times(value: Double): CMMatrix =
        toCM().scalarMultiply(value).asMatrix()

    override fun Double.times(m: Matrix<Float64>): CMMatrix =
        m * this

    override fun Point<Float64>.times(value: Double): CMVector =
        toCM().mapMultiply(value).asVector()

    override fun Double.times(v: Point<Float64>): CMVector =
        v * this

    @OptIn(UnstableKMathAPI::class)
    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Float64>, attribute: A): V? {

        val origin = structure.toCM()

        val raw: Any? = when (attribute) {
            IsDiagonal -> if (origin is DiagonalMatrix) Unit else null
            Determinant -> org.apache.commons.math3.linear.LUDecomposition(origin).determinant

            Inverted -> org.apache.commons.math3.linear.LUDecomposition(origin).solver.inverse.asMatrix()

            LUP -> object : LupDecomposition<Float64> {
                val lup by lazy { org.apache.commons.math3.linear.LUDecomposition(origin) }
                override val pivot: IntBuffer get() = lup.pivot.asBuffer()
                override val l: Matrix<Float64> get() = lup.l.asMatrix().withAttribute(LowerTriangular)
                override val u: Matrix<Float64> get() = lup.u.asMatrix().withAttribute(UpperTriangular)
            }

            Cholesky -> object : CholeskyDecomposition<Float64> {
                val cmCholesky by lazy { org.apache.commons.math3.linear.CholeskyDecomposition(origin) }
                override val l: Matrix<Float64> get() = cmCholesky.l.asMatrix()
            }

            QR -> object : QRDecomposition<Float64> {
                val cmQr by lazy { org.apache.commons.math3.linear.QRDecomposition(origin) }
                override val q: Matrix<Float64> get() = cmQr.q.asMatrix().withAttribute(OrthogonalAttribute)
                override val r: Matrix<Float64> get() = cmQr.r.asMatrix().withAttribute(UpperTriangular)
            }

            SVD -> object : space.kscience.kmath.linear.SingularValueDecomposition<Float64> {
                val cmSvd by lazy { org.apache.commons.math3.linear.SingularValueDecomposition(origin) }

                override val u: Matrix<Float64> get() = cmSvd.u.asMatrix()
                override val s: Matrix<Float64> get() = cmSvd.s.asMatrix()
                override val v: Matrix<Float64> get() = cmSvd.v.asMatrix()
                override val singularValues: Point<Float64> get() = cmSvd.singularValues.asBuffer()

            }

            EIG -> object : EigenDecomposition<Float64> {
                val cmEigen by lazy { org.apache.commons.math3.linear.EigenDecomposition(origin) }

                override val v: Matrix<Float64> get() = cmEigen.v.asMatrix()
                override val d: Matrix<Float64> get() = cmEigen.d.asMatrix()
            }

            else -> null
        }
        @Suppress("UNCHECKED_CAST")
        return raw as V?
    }

}

public operator fun CMMatrix.plus(other: CMMatrix): CMMatrix = CMMatrix(cmMatrix.add(other.cmMatrix))

public operator fun CMMatrix.minus(other: CMMatrix): CMMatrix = CMMatrix(cmMatrix.subtract(other.cmMatrix))

public infix fun CMMatrix.dot(other: CMMatrix): CMMatrix = CMMatrix(cmMatrix.multiply(other.cmMatrix))
