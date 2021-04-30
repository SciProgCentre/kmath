/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ejml

import org.ejml.data.DMatrix
import org.ejml.data.DMatrixD1
import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import space.kscience.kmath.linear.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.StructureFeature
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * [LinearSpace] implementation specialized for a certain EJML type.
 *
 * @param T the type of items in the matrices.
 * @param A the element context type.
 * @param M the EJML matrix type.
 * @author Iaroslav Postovalov
 */
public abstract class EjmlLinearSpace<T : Any, out A : Ring<T>, M : org.ejml.data.Matrix> : LinearSpace<T, A> {
    /**
     * Converts this matrix to EJML one.
     */
    public abstract fun Matrix<T>.toEjml(): EjmlMatrix<T, M>

    /**
     * Converts this vector to EJML one.
     */
    public abstract fun Point<T>.toEjml(): EjmlVector<T, M>

    public abstract override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: A.(i: Int, j: Int) -> T,
    ): EjmlMatrix<T, M>

    public abstract override fun buildVector(size: Int, initializer: A.(Int) -> T): EjmlVector<T, M>
}

/**
 * [EjmlLinearSpace] implementation based on [CommonOps_DDRM], [DecompositionFactory_DDRM] operations and
 * [DMatrixRMaj] matrices.
 *
 * @author Iaroslav Postovalov
 */
public object EjmlLinearSpaceDDRM : EjmlLinearSpace<Double, DoubleField, DMatrixRMaj>() {
    /**
     * The [DoubleField] reference.
     */
    public override val elementAlgebra: DoubleField get() = DoubleField

    @Suppress("UNCHECKED_CAST")
    public override fun Matrix<Double>.toEjml(): EjmlDoubleMatrix<DMatrixRMaj> = when {
        this is EjmlDoubleMatrix<*> && origin is DMatrixRMaj -> this as EjmlDoubleMatrix<DMatrixRMaj>
        else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }
    }

    @Suppress("UNCHECKED_CAST")
    public override fun Point<Double>.toEjml(): EjmlDoubleVector<DMatrixRMaj> = when {
        this is EjmlDoubleVector<*> && origin is DMatrixRMaj -> this as EjmlDoubleVector<DMatrixRMaj>
        else -> EjmlDoubleVector(DMatrixRMaj(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        })
    }

    public override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: DoubleField.(i: Int, j: Int) -> Double,
    ): EjmlDoubleMatrix<DMatrixRMaj> = EjmlDoubleMatrix(DMatrixRMaj(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    })

    public override fun buildVector(
        size: Int,
        initializer: DoubleField.(Int) -> Double,
    ): EjmlDoubleVector<DMatrixRMaj> = EjmlDoubleVector(DMatrixRMaj(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    private fun <T : DMatrix> T.wrapMatrix() = EjmlDoubleMatrix(this)
    private fun <T : DMatrixD1> T.wrapVector() = EjmlDoubleVector(this)

    public override fun Matrix<Double>.unaryMinus(): Matrix<Double> = this * (-1.0)

    public override fun Matrix<Double>.dot(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.mult(toEjml().origin, other.toEjml().origin, out)
        return out.wrapMatrix()
    }

    public override fun Matrix<Double>.dot(vector: Point<Double>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.mult(toEjml().origin, vector.toEjml().origin, out)
        return out.wrapVector()
    }

    public override operator fun Matrix<Double>.minus(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.subtract(toEjml().origin, other.toEjml().origin, out)
        return out.wrapMatrix()
    }

    public override operator fun Matrix<Double>.times(value: Double): EjmlDoubleMatrix<DMatrixRMaj> {
        val res = this.toEjml().origin.copy()
        CommonOps_DDRM.scale(value, res)
        return res.wrapMatrix()
    }

    public override fun Point<Double>.unaryMinus(): EjmlDoubleVector<DMatrixRMaj> {
        val out = toEjml().origin.copy()
        CommonOps_DDRM.changeSign(out)
        return out.wrapVector()
    }

    public override fun Matrix<Double>.plus(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.add(toEjml().origin, other.toEjml().origin, out)
        return out.wrapMatrix()
    }

    public override fun Point<Double>.plus(other: Point<Double>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.add(toEjml().origin, other.toEjml().origin, out)
        return out.wrapVector()
    }

    public override fun Point<Double>.minus(other: Point<Double>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.subtract(toEjml().origin, other.toEjml().origin, out)
        return out.wrapVector()
    }

    public override fun Double.times(m: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> = m * this

    public override fun Point<Double>.times(value: Double): EjmlDoubleVector<DMatrixRMaj> {
        val res = this.toEjml().origin.copy()
        CommonOps_DDRM.scale(value, res)
        return res.wrapVector()
    }

    public override fun Double.times(v: Point<Double>): EjmlDoubleVector<DMatrixRMaj> = v * this

    @UnstableKMathAPI
    public override fun <F : StructureFeature> getFeature(structure: Matrix<Double>, type: KClass<out F>): F? {
        // Return the feature if it is intrinsic to the structure
        structure.getFeature(type)?.let { return it }

        val origin = structure.toEjml().origin

        return when (type) {
            InverseMatrixFeature::class -> object : InverseMatrixFeature<Double> {
                override val inverse: Matrix<Double> by lazy {
                    val res = origin.copy()
                    CommonOps_DDRM.invert(res)
                    EjmlDoubleMatrix(res)
                }
            }

            DeterminantFeature::class -> object : DeterminantFeature<Double> {
                override val determinant: Double by lazy { CommonOps_DDRM.det(DMatrixRMaj(origin)) }
            }

            SingularValueDecompositionFeature::class -> object : SingularValueDecompositionFeature<Double> {
                private val svd by lazy {
                    DecompositionFactory_DDRM.svd(origin.numRows, origin.numCols, true, true, false)
                        .apply { decompose(origin.copy()) }
                }

                override val u: Matrix<Double> by lazy { EjmlDoubleMatrix(svd.getU(null, false)) }
                override val s: Matrix<Double> by lazy { EjmlDoubleMatrix(svd.getW(null)) }
                override val v: Matrix<Double> by lazy { EjmlDoubleMatrix(svd.getV(null, false)) }
                override val singularValues: Point<Double> by lazy { DoubleBuffer(svd.singularValues) }
            }

            QRDecompositionFeature::class -> object : QRDecompositionFeature<Double> {
                private val qr by lazy {
                    DecompositionFactory_DDRM.qr().apply { decompose(origin.copy()) }
                }

                override val q: Matrix<Double> by lazy {
                    EjmlDoubleMatrix(qr.getQ(null, false)) + OrthogonalFeature
                }

                override val r: Matrix<Double> by lazy { EjmlDoubleMatrix(qr.getR(null, false)) + UFeature }
            }

            CholeskyDecompositionFeature::class -> object : CholeskyDecompositionFeature<Double> {
                override val l: Matrix<Double> by lazy {
                    val cholesky =
                        DecompositionFactory_DDRM.chol(structure.rowNum, true).apply { decompose(origin.copy()) }

                    EjmlDoubleMatrix(cholesky.getT(null)) + LFeature
                }
            }

            LupDecompositionFeature::class -> object : LupDecompositionFeature<Double> {
                private val lup by lazy {
                    DecompositionFactory_DDRM.lu(origin.numRows, origin.numCols).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Double> by lazy {
                    EjmlDoubleMatrix(lup.getLower(null)) + LFeature
                }

                override val u: Matrix<Double> by lazy {
                    EjmlDoubleMatrix(lup.getUpper(null)) + UFeature
                }

                override val p: Matrix<Double> by lazy { EjmlDoubleMatrix(lup.getRowPivot(null)) }
            }

            else -> null
        }?.let(type::cast)
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p matrix.
     * @return the solution for 'x' that is n by p.
     */
    public fun solve(a: Matrix<Double>, b: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.solve(DMatrixRMaj(a.toEjml().origin), DMatrixRMaj(b.toEjml().origin), res)
        return EjmlDoubleMatrix(res)
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for 'x' that is n by p.
     */
    public fun solve(a: Matrix<Double>, b: Point<Double>): EjmlDoubleVector<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.solve(DMatrixRMaj(a.toEjml().origin), DMatrixRMaj(b.toEjml().origin), res)
        return EjmlDoubleVector(res)
    }
}
