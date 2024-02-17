/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

/* This file is generated with buildSrc/src/main/kotlin/space/kscience/kmath/ejml/codegen/ejmlCodegen.kt */

package space.kscience.kmath.ejml

import org.ejml.data.*
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.CommonOps_FDRM
import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.dense.row.factory.DecompositionFactory_FDRM
import org.ejml.sparse.FillReducing
import org.ejml.sparse.csc.CommonOps_DSCC
import org.ejml.sparse.csc.CommonOps_FSCC
import org.ejml.sparse.csc.factory.DecompositionFactory_DSCC
import org.ejml.sparse.csc.factory.DecompositionFactory_FSCC
import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf
import space.kscience.kmath.linear.*
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.nd.StructureAttribute
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Float32
import space.kscience.kmath.structures.IntBuffer
import space.kscience.kmath.structures.asBuffer

/**
 * [EjmlVector] specialization for [Double].
 */
public class EjmlDoubleVector<out M : DMatrix>(override val origin: M) : EjmlVector<Double, M>(origin) {
    init {
        require(origin.numRows == 1) { "The origin matrix must have only one row to form a vector" }
    }

    override val type: SafeType<Double> get() = safeTypeOf()

    override operator fun get(index: Int): Double = origin[0, index]
}

/**
 * [EjmlVector] specialization for [Float].
 */
public class EjmlFloatVector<out M : FMatrix>(override val origin: M) : EjmlVector<Float, M>(origin) {
    init {
        require(origin.numRows == 1) { "The origin matrix must have only one row to form a vector" }
    }

    override val type: SafeType<Float> get() = safeTypeOf()

    override operator fun get(index: Int): Float = origin[0, index]
}

/**
 * [EjmlMatrix] specialization for [Double].
 */
public class EjmlDoubleMatrix<out M : DMatrix>(override val origin: M) : EjmlMatrix<Double, M>(origin) {
    override val type: SafeType<Double> get() = safeTypeOf()

    override operator fun get(i: Int, j: Int): Double = origin[i, j]
}

/**
 * [EjmlMatrix] specialization for [Float].
 */
public class EjmlFloatMatrix<out M : FMatrix>(override val origin: M) : EjmlMatrix<Float, M>(origin) {
    override val type: SafeType<Float> get() = safeTypeOf()

    override operator fun get(i: Int, j: Int): Float = origin[i, j]
}


/**
 * [EjmlLinearSpace] implementation based on [CommonOps_DDRM], [DecompositionFactory_DDRM] operations and
 * [DMatrixRMaj] matrices.
 */
public object EjmlLinearSpaceDDRM : EjmlLinearSpace<Double, Float64Field, DMatrixRMaj>() {
    /**
     * The [Float64Field] reference.
     */
    override val elementAlgebra: Float64Field get() = Float64Field

    override val type: SafeType<Double> get() = safeTypeOf()

    @Suppress("UNCHECKED_CAST")
    override fun Matrix<Double>.toEjml(): EjmlDoubleMatrix<DMatrixRMaj> = when {
        this is EjmlDoubleMatrix<*> && origin is DMatrixRMaj -> this as EjmlDoubleMatrix<DMatrixRMaj>
        else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun Point<Double>.toEjml(): EjmlDoubleVector<DMatrixRMaj> = when {
        this is EjmlDoubleVector<*> && origin is DMatrixRMaj -> this as EjmlDoubleVector<DMatrixRMaj>
        else -> EjmlDoubleVector(DMatrixRMaj(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        })
    }

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float64Field.(i: Int, j: Int) -> Double,
    ): EjmlDoubleMatrix<DMatrixRMaj> = DMatrixRMaj(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.wrapMatrix()

    override fun buildVector(
        size: Int,
        initializer: Float64Field.(Int) -> Double,
    ): EjmlDoubleVector<DMatrixRMaj> = EjmlDoubleVector(DMatrixRMaj(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    private fun <T : DMatrix> T.wrapMatrix() = EjmlDoubleMatrix(this)
    private fun <T : DMatrix> T.wrapVector() = EjmlDoubleVector(this)

    override fun Matrix<Double>.unaryMinus(): Matrix<Double> = this * elementAlgebra { -one }

    override fun Matrix<Double>.dot(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.mult(toEjml().origin, other.toEjml().origin, out)
        return out.wrapMatrix()
    }

    override fun Matrix<Double>.dot(vector: Point<Double>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.mult(toEjml().origin, vector.toEjml().origin, out)
        return out.wrapVector()
    }

    override operator fun Matrix<Double>.minus(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)

        CommonOps_DDRM.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,
        )

        return out.wrapMatrix()
    }

    override operator fun Matrix<Double>.times(value: Double): EjmlDoubleMatrix<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.scale(value, toEjml().origin, res)
        return res.wrapMatrix()
    }

    override fun Point<Double>.unaryMinus(): EjmlDoubleVector<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.changeSign(toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Matrix<Double>.plus(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)

        CommonOps_DDRM.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,
        )

        return out.wrapMatrix()
    }

    override fun Point<Double>.plus(other: Point<Double>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)

        CommonOps_DDRM.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,
        )

        return out.wrapVector()
    }

    override fun Point<Double>.minus(other: Point<Double>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)

        CommonOps_DDRM.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,
        )

        return out.wrapVector()
    }

    override fun Double.times(m: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> = m * this

    override fun Point<Double>.times(value: Double): EjmlDoubleVector<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.scale(value, toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Double.times(v: Point<Double>): EjmlDoubleVector<DMatrixRMaj> = v * this

    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Double>, attribute: A): V? {
        val origin = structure.toEjml().origin

        val raw: Any? = when (attribute) {
            Inverted -> {
                val res = origin.copy()
                CommonOps_DDRM.invert(res)
                res.wrapMatrix()
            }

            Determinant -> CommonOps_DDRM.det(origin)
            SVD -> object : SingularValueDecomposition<Double> {
                val ejmlSvd by lazy {
                    DecompositionFactory_DDRM
                        .svd(origin.numRows, origin.numCols, true, true, false)
                        .apply { decompose(origin.copy()) }
                }
                override val u: Matrix<Double> get() = ejmlSvd.getU(null, false).wrapMatrix()

                override val s: Matrix<Double> get() = ejmlSvd.getW(null).wrapMatrix()
                override val v: Matrix<Double> get() = ejmlSvd.getV(null, false).wrapMatrix()
                override val singularValues: Point<Double> get() = ejmlSvd.singularValues.asBuffer()

            }

            QR -> object : QRDecomposition<Double> {
                val ejmlQr by lazy { DecompositionFactory_DDRM.qr().apply { decompose(origin.copy()) } }
                override val q: Matrix<Double> get() = ejmlQr.getQ(null, false).wrapMatrix()
                override val r: Matrix<Double> get() = ejmlQr.getR(null, false).wrapMatrix()
            }

            Cholesky -> object : CholeskyDecomposition<Double> {
                override val l: Matrix<Double> by lazy {
                    val cholesky =
                        DecompositionFactory_DDRM.chol(structure.rowNum, true).apply { decompose(origin.copy()) }

                    cholesky.getT(null).wrapMatrix().withAttribute(LowerTriangular)
                }
            }

            LUP -> object : LupDecomposition<Double> {
                private val lup by lazy {
                    DecompositionFactory_DDRM.lu(origin.numRows, origin.numCols).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Double>
                    get() = lup.getLower(null).wrapMatrix().withAttribute(LowerTriangular)


                override val u: Matrix<Double>
                    get() = lup.getUpper(null).wrapMatrix().withAttribute(UpperTriangular)
                override val pivot: IntBuffer get() = lup.getRowPivotV(null).asBuffer()
            }

            else -> null
        }

        @Suppress("UNCHECKED_CAST")
        return raw as V?
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p matrix.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Double>, b: Matrix<Double>): EjmlDoubleMatrix<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.solve(DMatrixRMaj(a.toEjml().origin), DMatrixRMaj(b.toEjml().origin), res)
        return res.wrapMatrix()
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Double>, b: Point<Double>): EjmlDoubleVector<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.solve(DMatrixRMaj(a.toEjml().origin), DMatrixRMaj(b.toEjml().origin), res)
        return EjmlDoubleVector(res)
    }
}


/**
 * [EjmlLinearSpace] implementation based on [CommonOps_FDRM], [DecompositionFactory_FDRM] operations and
 * [FMatrixRMaj] matrices.
 */
public object EjmlLinearSpaceFDRM : EjmlLinearSpace<Float, Float32Field, FMatrixRMaj>() {
    /**
     * The [Float32Field] reference.
     */
    override val elementAlgebra: Float32Field get() = Float32Field

    override val type: SafeType<Float> get() = safeTypeOf()

    @Suppress("UNCHECKED_CAST")
    override fun Matrix<Float>.toEjml(): EjmlFloatMatrix<FMatrixRMaj> = when {
        this is EjmlFloatMatrix<*> && origin is FMatrixRMaj -> this as EjmlFloatMatrix<FMatrixRMaj>
        else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun Point<Float>.toEjml(): EjmlFloatVector<FMatrixRMaj> = when {
        this is EjmlFloatVector<*> && origin is FMatrixRMaj -> this as EjmlFloatVector<FMatrixRMaj>
        else -> EjmlFloatVector(FMatrixRMaj(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        })
    }

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float32Field.(i: Int, j: Int) -> Float,
    ): EjmlFloatMatrix<FMatrixRMaj> = FMatrixRMaj(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.wrapMatrix()

    override fun buildVector(
        size: Int,
        initializer: Float32Field.(Int) -> Float,
    ): EjmlFloatVector<FMatrixRMaj> = EjmlFloatVector(FMatrixRMaj(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    private fun <T : FMatrix> T.wrapMatrix() = EjmlFloatMatrix(this)
    private fun <T : FMatrix> T.wrapVector() = EjmlFloatVector(this)

    override fun Matrix<Float>.unaryMinus(): Matrix<Float> = this * elementAlgebra { -one }

    override fun Matrix<Float>.dot(other: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)
        CommonOps_FDRM.mult(toEjml().origin, other.toEjml().origin, out)
        return out.wrapMatrix()
    }

    override fun Matrix<Float>.dot(vector: Point<Float>): EjmlFloatVector<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)
        CommonOps_FDRM.mult(toEjml().origin, vector.toEjml().origin, out)
        return out.wrapVector()
    }

    override operator fun Matrix<Float>.minus(other: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)

        CommonOps_FDRM.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,
        )

        return out.wrapMatrix()
    }

    override operator fun Matrix<Float>.times(value: Float): EjmlFloatMatrix<FMatrixRMaj> {
        val res = FMatrixRMaj(1, 1)
        CommonOps_FDRM.scale(value, toEjml().origin, res)
        return res.wrapMatrix()
    }

    override fun Point<Float>.unaryMinus(): EjmlFloatVector<FMatrixRMaj> {
        val res = FMatrixRMaj(1, 1)
        CommonOps_FDRM.changeSign(toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Matrix<Float>.plus(other: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)

        CommonOps_FDRM.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,
        )

        return out.wrapMatrix()
    }

    override fun Point<Float>.plus(other: Point<Float>): EjmlFloatVector<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)

        CommonOps_FDRM.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,
        )

        return out.wrapVector()
    }

    override fun Point<Float>.minus(other: Point<Float>): EjmlFloatVector<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)

        CommonOps_FDRM.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,
        )

        return out.wrapVector()
    }

    override fun Float.times(m: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> = m * this

    override fun Point<Float>.times(value: Float): EjmlFloatVector<FMatrixRMaj> {
        val res = FMatrixRMaj(1, 1)
        CommonOps_FDRM.scale(value, toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Float.times(v: Point<Float>): EjmlFloatVector<FMatrixRMaj> = v * this

    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Float32>, attribute: A): V? {
        val origin = structure.toEjml().origin

        val raw: Any? = when (attribute) {
            Inverted -> {
                val res = origin.copy()
                CommonOps_FDRM.invert(res)
                res.wrapMatrix()
            }

            Determinant -> CommonOps_FDRM.det(origin)
            SVD -> object : SingularValueDecomposition<Float32> {
                val ejmlSvd by lazy {
                    DecompositionFactory_FDRM
                        .svd(origin.numRows, origin.numCols, true, true, false)
                        .apply { decompose(origin.copy()) }
                }
                override val u: Matrix<Float32> get() = ejmlSvd.getU(null, false).wrapMatrix()

                override val s: Matrix<Float32> get() = ejmlSvd.getW(null).wrapMatrix()
                override val v: Matrix<Float32> get() = ejmlSvd.getV(null, false).wrapMatrix()
                override val singularValues: Point<Float32> get() = ejmlSvd.singularValues.asBuffer()

            }

            QR -> object : QRDecomposition<Float32> {
                val ejmlQr by lazy { DecompositionFactory_FDRM.qr().apply { decompose(origin.copy()) } }
                override val q: Matrix<Float32> get() = ejmlQr.getQ(null, false).wrapMatrix()
                override val r: Matrix<Float32> get() = ejmlQr.getR(null, false).wrapMatrix()
            }

            Cholesky -> object : CholeskyDecomposition<Float32> {
                override val l: Matrix<Float32> by lazy {
                    val cholesky =
                        DecompositionFactory_FDRM.chol(structure.rowNum, true).apply { decompose(origin.copy()) }

                    cholesky.getT(null).wrapMatrix().withAttribute(LowerTriangular)
                }
            }

            LUP -> object : LupDecomposition<Float32> {
                private val lup by lazy {
                    DecompositionFactory_FDRM.lu(origin.numRows, origin.numCols).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Float32>
                    get() = lup.getLower(null).wrapMatrix().withAttribute(LowerTriangular)


                override val u: Matrix<Float32>
                    get() = lup.getUpper(null).wrapMatrix().withAttribute(UpperTriangular)
                override val pivot: IntBuffer get() = lup.getRowPivotV(null).asBuffer()
            }

            else -> null
        }

        @Suppress("UNCHECKED_CAST")
        return raw as V?
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p matrix.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Float>, b: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> {
        val res = FMatrixRMaj(1, 1)
        CommonOps_FDRM.solve(FMatrixRMaj(a.toEjml().origin), FMatrixRMaj(b.toEjml().origin), res)
        return res.wrapMatrix()
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Float>, b: Point<Float>): EjmlFloatVector<FMatrixRMaj> {
        val res = FMatrixRMaj(1, 1)
        CommonOps_FDRM.solve(FMatrixRMaj(a.toEjml().origin), FMatrixRMaj(b.toEjml().origin), res)
        return EjmlFloatVector(res)
    }
}


/**
 * [EjmlLinearSpace] implementation based on [CommonOps_DSCC], [DecompositionFactory_DSCC] operations and
 * [DMatrixSparseCSC] matrices.
 */
public object EjmlLinearSpaceDSCC : EjmlLinearSpace<Double, Float64Field, DMatrixSparseCSC>() {
    /**
     * The [Float64Field] reference.
     */
    override val elementAlgebra: Float64Field get() = Float64Field

    override val type: SafeType<Double> get() = safeTypeOf()

    @Suppress("UNCHECKED_CAST")
    override fun Matrix<Double>.toEjml(): EjmlDoubleMatrix<DMatrixSparseCSC> = when {
        this is EjmlDoubleMatrix<*> && origin is DMatrixSparseCSC -> this as EjmlDoubleMatrix<DMatrixSparseCSC>
        else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun Point<Double>.toEjml(): EjmlDoubleVector<DMatrixSparseCSC> = when {
        this is EjmlDoubleVector<*> && origin is DMatrixSparseCSC -> this as EjmlDoubleVector<DMatrixSparseCSC>
        else -> EjmlDoubleVector(DMatrixSparseCSC(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        })
    }

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float64Field.(i: Int, j: Int) -> Double,
    ): EjmlDoubleMatrix<DMatrixSparseCSC> = DMatrixSparseCSC(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.wrapMatrix()

    override fun buildVector(
        size: Int,
        initializer: Float64Field.(Int) -> Double,
    ): EjmlDoubleVector<DMatrixSparseCSC> = EjmlDoubleVector(DMatrixSparseCSC(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    private fun <T : DMatrix> T.wrapMatrix() = EjmlDoubleMatrix(this)
    private fun <T : DMatrix> T.wrapVector() = EjmlDoubleVector(this)

    override fun Matrix<Double>.unaryMinus(): Matrix<Double> = this * elementAlgebra { -one }

    override fun Matrix<Double>.dot(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.mult(toEjml().origin, other.toEjml().origin, out)
        return out.wrapMatrix()
    }

    override fun Matrix<Double>.dot(vector: Point<Double>): EjmlDoubleVector<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.mult(toEjml().origin, vector.toEjml().origin, out)
        return out.wrapVector()
    }

    override operator fun Matrix<Double>.minus(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)

        CommonOps_DSCC.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,
            null,
            null,
        )

        return out.wrapMatrix()
    }

    override operator fun Matrix<Double>.times(value: Double): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.scale(value, toEjml().origin, res)
        return res.wrapMatrix()
    }

    override fun Point<Double>.unaryMinus(): EjmlDoubleVector<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.changeSign(toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Matrix<Double>.plus(other: Matrix<Double>): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)

        CommonOps_DSCC.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,
            null,
            null,
        )

        return out.wrapMatrix()
    }

    override fun Point<Double>.plus(other: Point<Double>): EjmlDoubleVector<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)

        CommonOps_DSCC.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,
            null,
            null,
        )

        return out.wrapVector()
    }

    override fun Point<Double>.minus(other: Point<Double>): EjmlDoubleVector<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)

        CommonOps_DSCC.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,
            null,
            null,
        )

        return out.wrapVector()
    }

    override fun Double.times(m: Matrix<Double>): EjmlDoubleMatrix<DMatrixSparseCSC> = m * this

    override fun Point<Double>.times(value: Double): EjmlDoubleVector<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.scale(value, toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Double.times(v: Point<Double>): EjmlDoubleVector<DMatrixSparseCSC> = v * this

    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Double>, attribute: A): V? {
        val origin = structure.toEjml().origin

        val raw: Any? = when (attribute) {
            Inverted -> {
                val res = DMatrixRMaj(origin.numRows,origin.numCols)
                CommonOps_DSCC.invert(origin,res)
                res.wrapMatrix()
            }

            Determinant -> CommonOps_DSCC.det(origin)

            QR -> object : QRDecomposition<Double> {
                val ejmlQr by lazy { DecompositionFactory_DSCC.qr(FillReducing.NONE).apply { decompose(origin.copy()) } }
                override val q: Matrix<Double> get() = ejmlQr.getQ(null, false).wrapMatrix()
                override val r: Matrix<Double> get() = ejmlQr.getR(null, false).wrapMatrix()
            }

            Cholesky -> object : CholeskyDecomposition<Double> {
                override val l: Matrix<Double> by lazy {
                    val cholesky =
                        DecompositionFactory_DSCC.cholesky().apply { decompose(origin.copy()) }

                    (cholesky.getT(null) as DMatrix).wrapMatrix().withAttribute(LowerTriangular)
                }
            }

            LUP -> object : LupDecomposition<Double> {
                private val lup by lazy {
                    DecompositionFactory_DSCC.lu(FillReducing.NONE).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Double>
                    get() = lup.getLower(null).wrapMatrix().withAttribute(LowerTriangular)


                override val u: Matrix<Double>
                    get() = lup.getUpper(null).wrapMatrix().withAttribute(UpperTriangular)
                override val pivot: IntBuffer get() = lup.getRowPivotV(null).asBuffer()
            }

            else -> null
        }

        @Suppress("UNCHECKED_CAST")
        return raw as V?
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p matrix.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Double>, b: Matrix<Double>): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.solve(DMatrixSparseCSC(a.toEjml().origin), DMatrixSparseCSC(b.toEjml().origin), res)
        return res.wrapMatrix()
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Double>, b: Point<Double>): EjmlDoubleVector<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.solve(DMatrixSparseCSC(a.toEjml().origin), DMatrixSparseCSC(b.toEjml().origin), res)
        return EjmlDoubleVector(res)
    }
}


/**
 * [EjmlLinearSpace] implementation based on [CommonOps_FSCC], [DecompositionFactory_FSCC] operations and
 * [FMatrixSparseCSC] matrices.
 */
public object EjmlLinearSpaceFSCC : EjmlLinearSpace<Float, Float32Field, FMatrixSparseCSC>() {
    /**
     * The [Float32Field] reference.
     */
    override val elementAlgebra: Float32Field get() = Float32Field

    override val type: SafeType<Float> get() = safeTypeOf()

    @Suppress("UNCHECKED_CAST")
    override fun Matrix<Float>.toEjml(): EjmlFloatMatrix<FMatrixSparseCSC> = when {
        this is EjmlFloatMatrix<*> && origin is FMatrixSparseCSC -> this as EjmlFloatMatrix<FMatrixSparseCSC>
        else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun Point<Float>.toEjml(): EjmlFloatVector<FMatrixSparseCSC> = when {
        this is EjmlFloatVector<*> && origin is FMatrixSparseCSC -> this as EjmlFloatVector<FMatrixSparseCSC>
        else -> EjmlFloatVector(FMatrixSparseCSC(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        })
    }

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float32Field.(i: Int, j: Int) -> Float,
    ): EjmlFloatMatrix<FMatrixSparseCSC> = FMatrixSparseCSC(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.wrapMatrix()

    override fun buildVector(
        size: Int,
        initializer: Float32Field.(Int) -> Float,
    ): EjmlFloatVector<FMatrixSparseCSC> = EjmlFloatVector(FMatrixSparseCSC(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    private fun <T : FMatrix> T.wrapMatrix() = EjmlFloatMatrix(this)
    private fun <T : FMatrix> T.wrapVector() = EjmlFloatVector(this)

    override fun Matrix<Float>.unaryMinus(): Matrix<Float> = this * elementAlgebra { -one }

    override fun Matrix<Float>.dot(other: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.mult(toEjml().origin, other.toEjml().origin, out)
        return out.wrapMatrix()
    }

    override fun Matrix<Float>.dot(vector: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.mult(toEjml().origin, vector.toEjml().origin, out)
        return out.wrapVector()
    }

    override operator fun Matrix<Float>.minus(other: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)

        CommonOps_FSCC.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,
            null,
            null,
        )

        return out.wrapMatrix()
    }

    override operator fun Matrix<Float>.times(value: Float): EjmlFloatMatrix<FMatrixSparseCSC> {
        val res = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.scale(value, toEjml().origin, res)
        return res.wrapMatrix()
    }

    override fun Point<Float>.unaryMinus(): EjmlFloatVector<FMatrixSparseCSC> {
        val res = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.changeSign(toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Matrix<Float>.plus(other: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)

        CommonOps_FSCC.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,
            null,
            null,
        )

        return out.wrapMatrix()
    }

    override fun Point<Float>.plus(other: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)

        CommonOps_FSCC.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,
            null,
            null,
        )

        return out.wrapVector()
    }

    override fun Point<Float>.minus(other: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)

        CommonOps_FSCC.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,
            null,
            null,
        )

        return out.wrapVector()
    }

    override fun Float.times(m: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> = m * this

    override fun Point<Float>.times(value: Float): EjmlFloatVector<FMatrixSparseCSC> {
        val res = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.scale(value, toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Float.times(v: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> = v * this
    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Float32>, attribute: A): V? {
        val origin = structure.toEjml().origin

        val raw: Any? = when (attribute) {
            Inverted -> {
                val res = FMatrixRMaj(origin.numRows,origin.numCols)
                CommonOps_FSCC.invert(origin,res)
                res.wrapMatrix()
            }

            Determinant -> CommonOps_FSCC.det(origin)

            QR -> object : QRDecomposition<Float32> {
                val ejmlQr by lazy { DecompositionFactory_FSCC.qr(FillReducing.NONE).apply { decompose(origin.copy()) } }
                override val q: Matrix<Float32> get() = ejmlQr.getQ(null, false).wrapMatrix()
                override val r: Matrix<Float32> get() = ejmlQr.getR(null, false).wrapMatrix()
            }

            Cholesky -> object : CholeskyDecomposition<Float32> {
                override val l: Matrix<Float32> by lazy {
                    val cholesky =
                        DecompositionFactory_FSCC.cholesky().apply { decompose(origin.copy()) }

                    (cholesky.getT(null) as FMatrix).wrapMatrix().withAttribute(LowerTriangular)
                }
            }

            LUP -> object : LupDecomposition<Float32> {
                private val lup by lazy {
                    DecompositionFactory_FSCC.lu(FillReducing.NONE).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Float32>
                    get() = lup.getLower(null).wrapMatrix().withAttribute(LowerTriangular)


                override val u: Matrix<Float32>
                    get() = lup.getUpper(null).wrapMatrix().withAttribute(UpperTriangular)
                override val pivot: IntBuffer get() = lup.getRowPivotV(null).asBuffer()
            }

            else -> null
        }

        @Suppress("UNCHECKED_CAST")
        return raw as V?
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p matrix.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Float>, b: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> {
        val res = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.solve(FMatrixSparseCSC(a.toEjml().origin), FMatrixSparseCSC(b.toEjml().origin), res)
        return res.wrapMatrix()
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Float>, b: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> {
        val res = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.solve(FMatrixSparseCSC(a.toEjml().origin), FMatrixSparseCSC(b.toEjml().origin), res)
        return EjmlFloatVector(res)
    }
}

