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
import org.ejml.interfaces.decomposition.EigenDecomposition_F32
import org.ejml.interfaces.decomposition.EigenDecomposition_F64
import org.ejml.sparse.FillReducing
import org.ejml.sparse.csc.CommonOps_DSCC
import org.ejml.sparse.csc.CommonOps_FSCC
import org.ejml.sparse.csc.factory.DecompositionFactory_DSCC
import org.ejml.sparse.csc.factory.DecompositionFactory_FSCC
import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.linear.*
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.nd.StructureAttribute
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Float32
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.IntBuffer
import space.kscience.kmath.structures.asBuffer

/**
 * Copy EJML [Complex_F64] into KMath [Complex]
 */
public fun Complex_F64.toKMathComplex(): Complex = Complex(real, imaginary)

/**
 * [EjmlVector] specialization for [Double].
 */
public class EjmlDoubleVector<M : DMatrix>(override val ejmlVector: M) : EjmlVector<Double, M>(ejmlVector) {
    init {
        require(ejmlVector.numCols == 1) { "The origin matrix must have only one column to form a vector" }
    }


    override operator fun get(index: Int): Double = ejmlVector[index, 0]
}

/**
 * [EjmlVector] specialization for [Float].
 */
public class EjmlFloatVector<M : FMatrix>(override val ejmlVector: M) : EjmlVector<Float, M>(ejmlVector) {
    init {
        require(ejmlVector.numCols == 1) { "The origin matrix must have only one column to form a vector" }
    }

    override operator fun get(index: Int): Float = ejmlVector[index, 0]
}

/**
 * [EjmlMatrix] specialization for [Double].
 */
public class EjmlDoubleMatrix<M : DMatrix>(override val ejmlMatrix: M) : EjmlMatrix<Double, M>(ejmlMatrix) {
    override operator fun get(i: Int, j: Int): Double = ejmlMatrix[i, j]
}

/**
 * [EjmlMatrix] specialization for [Float].
 */
public class EjmlFloatMatrix<M : FMatrix>(override val ejmlMatrix: M) : EjmlMatrix<Float, M>(ejmlMatrix) {

    override operator fun get(i: Int, j: Int): Float = ejmlMatrix[i, j]
}


/**
 * [EjmlLinearSpace] implementation based on [CommonOps_DDRM], [DecompositionFactory_DDRM] operations and
 * [DMatrixRMaj] matrices.
 */
public object EjmlLinearSpaceDDRM : EjmlLinearSpace<Double, Float64Field, DMatrixRMaj> {
    /**
     * The [Float64Field] reference.
     */
    override val elementAlgebra: Float64Field get() = Float64Field

    override val type: SafeType<Float64> get() = safeTypeOf()

    @OptIn(UnstableKMathAPI::class)
    override fun Matrix<Float64>.toEjml(): DMatrixRMaj {
        val matrix = origin
        return when  {
            matrix is EjmlDoubleMatrix<*> && matrix.ejmlMatrix is DMatrixRMaj -> matrix.ejmlMatrix
            else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }.ejmlMatrix
        }
    }

    override fun Point<Float64>.toEjml(): DMatrixRMaj = when {
        this is EjmlDoubleVector<*> && ejmlVector is DMatrixRMaj -> ejmlVector
        else -> DMatrixRMaj(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        }
    }

    public fun <T: DMatrix> T.asMatrix(): EjmlDoubleMatrix<T> = EjmlDoubleMatrix(this)
    public fun <T: DMatrix> T.asVector(): EjmlDoubleVector<T> = EjmlDoubleVector(this)

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float64Field.(i: Int, j: Int) -> Double,
    ): EjmlDoubleMatrix<DMatrixRMaj> = DMatrixRMaj(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.asMatrix()

    override fun buildVector(
        size: Int,
        initializer: Float64Field.(Int) -> Double,
    ): EjmlDoubleVector<DMatrixRMaj> = EjmlDoubleVector(DMatrixRMaj(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    override fun Matrix<Float64>.unaryMinus(): Matrix<Float64> = this * elementAlgebra { -one }

    override fun Matrix<Float64>.dot(other: Matrix<Float64>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.mult(toEjml(), other.toEjml(), out)
        return out.asMatrix()
    }

    override fun Matrix<Float64>.dot(vector: Point<Float64>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)
        CommonOps_DDRM.mult(toEjml(), vector.toEjml(), out)
        return out.asVector()
    }

    override operator fun Matrix<Float64>.minus(other: Matrix<Float64>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)

        CommonOps_DDRM.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra { -one },
            other.toEjml(),
            out,
        )

        return out.asMatrix()
    }

    override operator fun Matrix<Float64>.times(value: Double): EjmlDoubleMatrix<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.scale(value, toEjml(), res)
        return res.asMatrix()
    }

    override fun Point<Float64>.unaryMinus(): EjmlDoubleVector<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.changeSign(toEjml(), res)
        return res.asVector()
    }

    override fun Matrix<Float64>.plus(other: Matrix<Float64>): EjmlDoubleMatrix<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)

        CommonOps_DDRM.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra.one,
            other.toEjml(),
            out,
        )

        return out.asMatrix()
    }

    override fun Point<Float64>.plus(other: Point<Float64>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)

        CommonOps_DDRM.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra.one,
            other.toEjml(),
            out,
        )

        return out.asVector()
    }

    override fun Point<Float64>.minus(other: Point<Float64>): EjmlDoubleVector<DMatrixRMaj> {
        val out = DMatrixRMaj(1, 1)

        CommonOps_DDRM.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra { -one },
            other.toEjml(),
            out,
        )

        return out.asVector()
    }

    override fun Double.times(m: Matrix<Float64>): EjmlDoubleMatrix<DMatrixRMaj> = m * this

    override fun Point<Float64>.times(value: Double): EjmlDoubleVector<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.scale(value, toEjml(), res)
        return res.asVector()
    }

    override fun Double.times(v: Point<Float64>): EjmlDoubleVector<DMatrixRMaj> = v * this

    @OptIn(UnstableKMathAPI::class)
    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Float64>, attribute: A): V? {
        val origin: DMatrixRMaj = structure.toEjml()

        val raw: Any? = when (attribute) {
            Inverted -> {
                val res = origin.copy()
                CommonOps_DDRM.invert(res)
                res.asMatrix()
            }

            Determinant -> CommonOps_DDRM.det(origin)

            SVD -> object : SingularValueDecomposition<Float64> {
                val ejmlSvd by lazy {
                    DecompositionFactory_DDRM
                        .svd(origin.numRows, origin.numCols, true, true, false)
                        .apply { decompose(origin.copy()) }
                }
                override val u: Matrix<Float64> get() = ejmlSvd.getU(null, false).asMatrix()

                override val s: Matrix<Float64> get() = ejmlSvd.getW(null).asMatrix()
                override val v: Matrix<Float64> get() = ejmlSvd.getV(null, false).asMatrix()
                override val singularValues: Point<Float64> get() = ejmlSvd.singularValues.asBuffer()

            }

            QR -> object : QRDecomposition<Float64> {
                val ejmlQr by lazy { DecompositionFactory_DDRM.qr().apply { decompose(origin.copy()) } }
                override val q: Matrix<Float64> get() = ejmlQr.getQ(null, false).asMatrix()
                override val r: Matrix<Float64> get() = ejmlQr.getR(null, false).asMatrix()
            }

            Cholesky -> object : CholeskyDecomposition<Float64> {
                override val l: Matrix<Float64> by lazy {
                    val cholesky =
                        DecompositionFactory_DDRM.chol(structure.rowNum, true).apply { decompose(origin.copy()) }

                    cholesky.getT(null).asMatrix().withAttribute(LowerTriangular)
                }
            }

            LUP -> object : LupDecomposition<Float64> {
                private val lup by lazy {
                    DecompositionFactory_DDRM.lu(origin.numRows, origin.numCols).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Float64>
                    get() = lup.getLower(null).asMatrix().withAttribute(LowerTriangular)

                override val u: Matrix<Float64>
                    get() = lup.getUpper(null).asMatrix().withAttribute(UpperTriangular)

                override val pivot: IntBuffer get() = lup.getRowPivotV(null).asBuffer()
            }

            EIG -> {
                check(origin.numCols == origin.numRows) { "Eigenvalue decomposition requires symmetric matrix" }
                object : EigenDecomposition<Float64> {
                    val cmEigen: EigenDecomposition_F64<DMatrixRMaj> by lazy {
                        DecompositionFactory_DDRM.eig(origin.numRows, true).apply { decompose(origin) }
                    }

                    override val v: Matrix<Float64> by lazy {
                        val eigenvectors = List(origin.numRows) { cmEigen.getEigenVector(it) }.filterNotNull()
                        buildMatrix(eigenvectors.size, origin.numCols) { row, column ->
                            eigenvectors[row][column]
                        }
                    }

                    override val d: Matrix<Float64> by lazy {
                        val eigenvalues = List(origin.numRows) { cmEigen.getEigenvalue(it) }

                        buildMatrix(origin.numRows, origin.numCols) { row, column ->
                            when (row) {
                                column -> eigenvalues[row].real
                                column - 1 -> eigenvalues[row].imaginary
                                column + 1 -> -eigenvalues[row].imaginary
                                else -> 0.0
                            }
                        }
                    }
                }
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
    public fun solve(a: Matrix<Float64>, b: Matrix<Float64>): EjmlDoubleMatrix<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.solve(DMatrixRMaj(a.toEjml()), DMatrixRMaj(b.toEjml()), res)
        return res.asMatrix()
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Float64>, b: Point<Float64>): EjmlDoubleVector<DMatrixRMaj> {
        val res = DMatrixRMaj(1, 1)
        CommonOps_DDRM.solve(DMatrixRMaj(a.toEjml()), DMatrixRMaj(b.toEjml()), res)
        return EjmlDoubleVector(res)
    }
}


/**
 * [EjmlLinearSpace] implementation based on [CommonOps_FDRM], [DecompositionFactory_FDRM] operations and
 * [FMatrixRMaj] matrices.
 */
public object EjmlLinearSpaceFDRM : EjmlLinearSpace<Float, Float32Field, FMatrixRMaj> {
    /**
     * The [Float32Field] reference.
     */
    override val elementAlgebra: Float32Field get() = Float32Field

    override val type: SafeType<Float> get() = safeTypeOf()

    @OptIn(UnstableKMathAPI::class)
    override fun Matrix<Float32>.toEjml(): FMatrixRMaj {
        val matrix = origin
        return when  {
            matrix is EjmlFloatMatrix<*> && matrix.ejmlMatrix is FMatrixRMaj -> matrix.ejmlMatrix
            else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }.ejmlMatrix
        }
    }

    override fun Point<Float32>.toEjml(): FMatrixRMaj = when {
        this is EjmlFloatVector<*> && ejmlVector is FMatrixRMaj -> ejmlVector
        else -> FMatrixRMaj(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        }
    }

    public fun <T: FMatrix> T.asMatrix(): EjmlFloatMatrix<T> = EjmlFloatMatrix(this)
    public fun <T: FMatrix> T.asVector(): EjmlFloatVector<T> = EjmlFloatVector(this)

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float32Field.(i: Int, j: Int) -> Float,
    ): EjmlFloatMatrix<FMatrixRMaj> = FMatrixRMaj(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.asMatrix()

    override fun buildVector(
        size: Int,
        initializer: Float32Field.(Int) -> Float,
    ): EjmlFloatVector<FMatrixRMaj> = EjmlFloatVector(FMatrixRMaj(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    override fun Matrix<Float>.unaryMinus(): Matrix<Float> = this * elementAlgebra { -one }

    override fun Matrix<Float>.dot(other: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)
        CommonOps_FDRM.mult(toEjml(), other.toEjml(), out)
        return out.asMatrix()
    }

    override fun Matrix<Float>.dot(vector: Point<Float>): EjmlFloatVector<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)
        CommonOps_FDRM.mult(toEjml(), vector.toEjml(), out)
        return out.asVector()
    }

    override operator fun Matrix<Float>.minus(other: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)

        CommonOps_FDRM.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra { -one },
            other.toEjml(),
            out,
        )

        return out.asMatrix()
    }

    override operator fun Matrix<Float>.times(value: Float): EjmlFloatMatrix<FMatrixRMaj> {
        val res = FMatrixRMaj(1, 1)
        CommonOps_FDRM.scale(value, toEjml(), res)
        return res.asMatrix()
    }

    override fun Point<Float>.unaryMinus(): EjmlFloatVector<FMatrixRMaj> {
        val res = FMatrixRMaj(1, 1)
        CommonOps_FDRM.changeSign(toEjml(), res)
        return res.asVector()
    }

    override fun Matrix<Float>.plus(other: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)

        CommonOps_FDRM.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra.one,
            other.toEjml(),
            out,
        )

        return out.asMatrix()
    }

    override fun Point<Float>.plus(other: Point<Float>): EjmlFloatVector<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)

        CommonOps_FDRM.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra.one,
            other.toEjml(),
            out,
        )

        return out.asVector()
    }

    override fun Point<Float>.minus(other: Point<Float>): EjmlFloatVector<FMatrixRMaj> {
        val out = FMatrixRMaj(1, 1)

        CommonOps_FDRM.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra { -one },
            other.toEjml(),
            out,
        )

        return out.asVector()
    }

    override fun Float.times(m: Matrix<Float>): EjmlFloatMatrix<FMatrixRMaj> = m * this

    override fun Point<Float>.times(value: Float): EjmlFloatVector<FMatrixRMaj> {
        val res = FMatrixRMaj(1, 1)
        CommonOps_FDRM.scale(value, toEjml(), res)
        return res.asVector()
    }

    override fun Float.times(v: Point<Float>): EjmlFloatVector<FMatrixRMaj> = v * this

    @OptIn(UnstableKMathAPI::class)
    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Float32>, attribute: A): V? {
        val origin = structure.toEjml()

        val raw: Any? = when (attribute) {
            Inverted -> {
                val res = origin.copy()
                CommonOps_FDRM.invert(res)
                res.asMatrix()
            }

            Determinant -> CommonOps_FDRM.det(origin)
            SVD -> object : SingularValueDecomposition<Float32> {
                val ejmlSvd by lazy {
                    DecompositionFactory_FDRM
                        .svd(origin.numRows, origin.numCols, true, true, false)
                        .apply { decompose(origin.copy()) }
                }
                override val u: Matrix<Float32> get() = ejmlSvd.getU(null, false).asMatrix()

                override val s: Matrix<Float32> get() = ejmlSvd.getW(null).asMatrix()
                override val v: Matrix<Float32> get() = ejmlSvd.getV(null, false).asMatrix()
                override val singularValues: Point<Float32> get() = ejmlSvd.singularValues.asBuffer()

            }

            QR -> object : QRDecomposition<Float32> {
                val ejmlQr by lazy { DecompositionFactory_FDRM.qr().apply { decompose(origin.copy()) } }
                override val q: Matrix<Float32> get() = ejmlQr.getQ(null, false).asMatrix()
                override val r: Matrix<Float32> get() = ejmlQr.getR(null, false).asMatrix()
            }

            Cholesky -> object : CholeskyDecomposition<Float32> {
                override val l: Matrix<Float32> by lazy {
                    val cholesky =
                        DecompositionFactory_FDRM.chol(structure.rowNum, true).apply { decompose(origin.copy()) }

                    cholesky.getT(null).asMatrix().withAttribute(LowerTriangular)
                }
            }

            LUP -> object : LupDecomposition<Float32> {
                private val lup by lazy {
                    DecompositionFactory_FDRM.lu(origin.numRows, origin.numCols).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Float32>
                    get() = lup.getLower(null).asMatrix().withAttribute(LowerTriangular)


                override val u: Matrix<Float32>
                    get() = lup.getUpper(null).asMatrix().withAttribute(UpperTriangular)
                override val pivot: IntBuffer get() = lup.getRowPivotV(null).asBuffer()
            }

            EIG -> {
                check(origin.numCols == origin.numRows) { "Eigenvalue decomposition requires symmetric matrix" }
                object : EigenDecomposition<Float32> {
                    val cmEigen: EigenDecomposition_F32<FMatrixRMaj> by lazy {
                        DecompositionFactory_FDRM.eig(origin.numRows, true).apply { decompose(origin) }
                    }

                    override val v by lazy {
                        val eigenvectors: List<FMatrixRMaj> = List(origin.numRows) { cmEigen.getEigenVector(it) }
                        buildMatrix(origin.numRows, origin.numCols) { row, column ->
                            eigenvectors[row][column]
                        }
                    }

                    override val d: Matrix<Float32> by lazy {
                        val eigenvalues = List(origin.numRows) { cmEigen.getEigenvalue(it) }

                        buildMatrix(origin.numRows, origin.numCols) { row, column ->
                            when (row) {
                                column -> eigenvalues[row].real
                                column - 1 -> eigenvalues[row].imaginary
                                column + 1 -> -eigenvalues[row].imaginary
                                else -> 0f
                            }
                        }
                    }
                }
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
        CommonOps_FDRM.solve(FMatrixRMaj(a.toEjml()), FMatrixRMaj(b.toEjml()), res)
        return res.asMatrix()
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
        CommonOps_FDRM.solve(FMatrixRMaj(a.toEjml()), FMatrixRMaj(b.toEjml()), res)
        return EjmlFloatVector(res)
    }
}


/**
 * [EjmlLinearSpace] implementation based on [CommonOps_DSCC], [DecompositionFactory_DSCC] operations and
 * [DMatrixSparseCSC] matrices.
 */
public object EjmlLinearSpaceDSCC : EjmlLinearSpace<Double, Float64Field, DMatrixSparseCSC> {
    /**
     * The [Float64Field] reference.
     */
    override val elementAlgebra: Float64Field get() = Float64Field

    override val type: SafeType<Float64> get() = safeTypeOf()

    @OptIn(UnstableKMathAPI::class)
    override fun Matrix<Float64>.toEjml(): DMatrixSparseCSC {
        val matrix = origin
        return when  {
            matrix is EjmlDoubleMatrix<*> && matrix.ejmlMatrix is DMatrixSparseCSC -> matrix.ejmlMatrix
            else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }.ejmlMatrix
        }
    }

    override fun Point<Float64>.toEjml(): DMatrixSparseCSC = when {
        this is EjmlDoubleVector<*> && ejmlVector is DMatrixSparseCSC -> ejmlVector
        else -> DMatrixSparseCSC(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        }
    }

    public fun <T: DMatrix> T.asMatrix(): EjmlDoubleMatrix<T> = EjmlDoubleMatrix(this)
    public fun <T: DMatrix> T.asVector(): EjmlDoubleVector<T> = EjmlDoubleVector(this)


    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float64Field.(i: Int, j: Int) -> Double,
    ): EjmlDoubleMatrix<DMatrixSparseCSC> = DMatrixSparseCSC(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.asMatrix()

    override fun buildVector(
        size: Int,
        initializer: Float64Field.(Int) -> Double,
    ): EjmlDoubleVector<DMatrixSparseCSC> = EjmlDoubleVector(DMatrixSparseCSC(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    override fun Matrix<Float64>.unaryMinus(): Matrix<Float64> = this * elementAlgebra { -one }

    override fun Matrix<Float64>.dot(other: Matrix<Float64>): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.mult(toEjml(), other.toEjml(), out)
        return out.asMatrix()
    }

    override fun Matrix<Float64>.dot(vector: Point<Float64>): EjmlDoubleVector<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.mult(toEjml(), vector.toEjml(), out)
        return out.asVector()
    }

    override operator fun Matrix<Float64>.minus(other: Matrix<Float64>): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)

        CommonOps_DSCC.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra { -one },
            other.toEjml(),
            out,
            null,
            null,
        )

        return out.asMatrix()
    }

    override operator fun Matrix<Float64>.times(value: Double): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.scale(value, toEjml(), res)
        return res.asMatrix()
    }

    override fun Point<Float64>.unaryMinus(): EjmlDoubleVector<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.changeSign(toEjml(), res)
        return res.asVector()
    }

    override fun Matrix<Float64>.plus(other: Matrix<Float64>): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)

        CommonOps_DSCC.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra.one,
            other.toEjml(),
            out,
            null,
            null,
        )

        return out.asMatrix()
    }

    override fun Point<Float64>.plus(other: Point<Float64>): EjmlDoubleVector<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)

        CommonOps_DSCC.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra.one,
            other.toEjml(),
            out,
            null,
            null,
        )

        return out.asVector()
    }

    override fun Point<Float64>.minus(other: Point<Float64>): EjmlDoubleVector<DMatrixSparseCSC> {
        val out = DMatrixSparseCSC(1, 1)

        CommonOps_DSCC.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra { -one },
            other.toEjml(),
            out,
            null,
            null,
        )

        return out.asVector()
    }

    override fun Double.times(m: Matrix<Float64>): EjmlDoubleMatrix<DMatrixSparseCSC> = m * this

    override fun Point<Float64>.times(value: Double): EjmlDoubleVector<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.scale(value, toEjml(), res)
        return res.asVector()
    }

    override fun Double.times(v: Point<Float64>): EjmlDoubleVector<DMatrixSparseCSC> = v * this

    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Float64>, attribute: A): V? {
        val origin = structure.toEjml()

        val raw: Any? = when (attribute) {
            Inverted -> {
                val res = DMatrixRMaj(origin.numRows, origin.numCols)
                CommonOps_DSCC.invert(origin, res)
                res.asMatrix()
            }

            Determinant -> CommonOps_DSCC.det(origin)

            QR -> object : QRDecomposition<Float64> {
                val ejmlQr by lazy {
                    DecompositionFactory_DSCC.qr(FillReducing.NONE).apply { decompose(origin.copy()) }
                }
                override val q: Matrix<Float64> get() = ejmlQr.getQ(null, false).asMatrix()
                override val r: Matrix<Float64> get() = ejmlQr.getR(null, false).asMatrix()
            }

            Cholesky -> object : CholeskyDecomposition<Float64> {
                override val l: Matrix<Float64> by lazy {
                    val cholesky =
                        DecompositionFactory_DSCC.cholesky().apply { decompose(origin.copy()) }

                    (cholesky.getT(null) as DMatrix).asMatrix().withAttribute(LowerTriangular)
                }
            }

            LUP -> object : LupDecomposition<Float64> {
                private val lup by lazy {
                    DecompositionFactory_DSCC.lu(FillReducing.NONE).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Float64>
                    get() = lup.getLower(null).asMatrix().withAttribute(LowerTriangular)


                override val u: Matrix<Float64>
                    get() = lup.getUpper(null).asMatrix().withAttribute(UpperTriangular)
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
    public fun solve(a: Matrix<Float64>, b: Matrix<Float64>): EjmlDoubleMatrix<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.solve(DMatrixSparseCSC(a.toEjml()), DMatrixSparseCSC(b.toEjml()), res)
        return res.asMatrix()
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<Float64>, b: Point<Float64>): EjmlDoubleVector<DMatrixSparseCSC> {
        val res = DMatrixSparseCSC(1, 1)
        CommonOps_DSCC.solve(DMatrixSparseCSC(a.toEjml()), DMatrixSparseCSC(b.toEjml()), res)
        return EjmlDoubleVector(res)
    }
}


/**
 * [EjmlLinearSpace] implementation based on [CommonOps_FSCC], [DecompositionFactory_FSCC] operations and
 * [FMatrixSparseCSC] matrices.
 */
public object EjmlLinearSpaceFSCC : EjmlLinearSpace<Float, Float32Field, FMatrixSparseCSC> {
    /**
     * The [Float32Field] reference.
     */
    override val elementAlgebra: Float32Field get() = Float32Field

    override val type: SafeType<Float> get() = safeTypeOf()

    @OptIn(UnstableKMathAPI::class)
    override fun Matrix<Float32>.toEjml(): FMatrixSparseCSC {
        val matrix = origin
        return when  {
            matrix is EjmlFloatMatrix<*> && matrix.ejmlMatrix is FMatrixSparseCSC -> matrix.ejmlMatrix
            else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }.ejmlMatrix
        }
    }

    override fun Point<Float32>.toEjml(): FMatrixSparseCSC = when {
        this is EjmlFloatVector<*> && ejmlVector is FMatrixSparseCSC -> ejmlVector
        else -> FMatrixSparseCSC(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        }
    }

    public fun <T : FMatrix> T.asMatrix(): EjmlFloatMatrix<T> = EjmlFloatMatrix(this)
    public fun <T : FMatrix> T.asVector(): EjmlFloatVector<T> = EjmlFloatVector(this)

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float32Field.(i: Int, j: Int) -> Float,
    ): EjmlFloatMatrix<FMatrixSparseCSC> = FMatrixSparseCSC(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.asMatrix()

    override fun buildVector(
        size: Int,
        initializer: Float32Field.(Int) -> Float,
    ): EjmlFloatVector<FMatrixSparseCSC> = EjmlFloatVector(FMatrixSparseCSC(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })



    override fun Matrix<Float>.unaryMinus(): Matrix<Float> = this * elementAlgebra { -one }

    override fun Matrix<Float>.dot(other: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.mult(toEjml(), other.toEjml(), out)
        return out.asMatrix()
    }

    override fun Matrix<Float>.dot(vector: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.mult(toEjml(), vector.toEjml(), out)
        return out.asVector()
    }

    override operator fun Matrix<Float>.minus(other: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)

        CommonOps_FSCC.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra { -one },
            other.toEjml(),
            out,
            null,
            null,
        )

        return out.asMatrix()
    }

    override operator fun Matrix<Float>.times(value: Float): EjmlFloatMatrix<FMatrixSparseCSC> {
        val res = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.scale(value, toEjml(), res)
        return res.asMatrix()
    }

    override fun Point<Float>.unaryMinus(): EjmlFloatVector<FMatrixSparseCSC> {
        val res = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.changeSign(toEjml(), res)
        return res.asVector()
    }

    override fun Matrix<Float>.plus(other: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)

        CommonOps_FSCC.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra.one,
            other.toEjml(),
            out,
            null,
            null,
        )

        return out.asMatrix()
    }

    override fun Point<Float>.plus(other: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)

        CommonOps_FSCC.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra.one,
            other.toEjml(),
            out,
            null,
            null,
        )

        return out.asVector()
    }

    override fun Point<Float>.minus(other: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> {
        val out = FMatrixSparseCSC(1, 1)

        CommonOps_FSCC.add(
            elementAlgebra.one,
            toEjml(),
            elementAlgebra { -one },
            other.toEjml(),
            out,
            null,
            null,
        )

        return out.asVector()
    }

    override fun Float.times(m: Matrix<Float>): EjmlFloatMatrix<FMatrixSparseCSC> = m * this

    override fun Point<Float>.times(value: Float): EjmlFloatVector<FMatrixSparseCSC> {
        val res = FMatrixSparseCSC(1, 1)
        CommonOps_FSCC.scale(value, toEjml(), res)
        return res.asVector()
    }

    override fun Float.times(v: Point<Float>): EjmlFloatVector<FMatrixSparseCSC> = v * this
    override fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<Float32>, attribute: A): V? {
        val origin = structure.toEjml()

        val raw: Any? = when (attribute) {
            Inverted -> {
                val res = FMatrixRMaj(origin.numRows, origin.numCols)
                CommonOps_FSCC.invert(origin, res)
                res.asMatrix()
            }

            Determinant -> CommonOps_FSCC.det(origin)

            QR -> object : QRDecomposition<Float32> {
                val ejmlQr by lazy {
                    DecompositionFactory_FSCC.qr(FillReducing.NONE).apply { decompose(origin.copy()) }
                }
                override val q: Matrix<Float32> get() = ejmlQr.getQ(null, false).asMatrix()
                override val r: Matrix<Float32> get() = ejmlQr.getR(null, false).asMatrix()
            }

            Cholesky -> object : CholeskyDecomposition<Float32> {
                override val l: Matrix<Float32> by lazy {
                    val cholesky =
                        DecompositionFactory_FSCC.cholesky().apply { decompose(origin.copy()) }

                    (cholesky.getT(null) as FMatrix).asMatrix().withAttribute(LowerTriangular)
                }
            }

            LUP -> object : LupDecomposition<Float32> {
                private val lup by lazy {
                    DecompositionFactory_FSCC.lu(FillReducing.NONE).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<Float32>
                    get() = lup.getLower(null).asMatrix().withAttribute(LowerTriangular)


                override val u: Matrix<Float32>
                    get() = lup.getUpper(null).asMatrix().withAttribute(UpperTriangular)
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
        CommonOps_FSCC.solve(FMatrixSparseCSC(a.toEjml()), FMatrixSparseCSC(b.toEjml()), res)
        return res.asMatrix()
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
        CommonOps_FSCC.solve(FMatrixSparseCSC(a.toEjml()), FMatrixSparseCSC(b.toEjml()), res)
        return EjmlFloatVector(res)
    }
}

