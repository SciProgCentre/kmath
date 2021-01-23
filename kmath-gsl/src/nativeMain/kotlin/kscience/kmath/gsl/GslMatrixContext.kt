package kscience.kmath.gsl

import kotlinx.cinterop.*
import kscience.kmath.linear.*
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.Complex
import kscience.kmath.operations.ComplexField
import kscience.kmath.operations.toComplex
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.toList
import org.gnu.gsl.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

internal inline fun <T : Any, H : CStructVar> GslMatrix<T, H>.fill(initializer: (Int, Int) -> T): GslMatrix<T, H> =
    apply {
        for (row in 0 until rowNum) {
            for (col in 0 until colNum) this[row, col] = initializer(row, col)
        }
    }

internal inline fun <T : Any, H : CStructVar> GslVector<T, H>.fill(initializer: (Int) -> T): GslVector<T, H> =
    apply {
        for (index in 0 until size) this[index] = initializer(index)
    }

/**
 * Represents matrix context implementing where all the operations are delegated to GSL.
 */
public abstract class GslMatrixContext<T : Any, H1 : CStructVar, H2 : CStructVar> : MatrixContext<T, GslMatrix<T, H1>> {
    init {
        ensureHasGslErrorHandler()
    }

    /**
     * Converts this matrix to GSL one.
     */
    @Suppress("UNCHECKED_CAST")
    public fun Matrix<T>.toGsl(): GslMatrix<T, H1> = if (this is GslMatrix<*, *>)
        this as GslMatrix<T, H1>
    else
        produce(rowNum, colNum) { i, j -> this[i, j] }

    /**
     * Converts this point to GSL one.
     */
    @Suppress("UNCHECKED_CAST")
    public fun Point<T>.toGsl(): GslVector<T, H2> =
        (if (this is GslVector<*, *>) this as GslVector<T, H2> else produceDirtyVector(size).fill { this[it] }).copy()

    internal abstract fun produceDirtyMatrix(rows: Int, columns: Int): GslMatrix<T, H1>
    internal abstract fun produceDirtyVector(size: Int): GslVector<T, H2>

    public override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): GslMatrix<T, H1> =
        produceDirtyMatrix(rows, columns).fill(initializer)

    public override fun point(size: Int, initializer: (Int) -> T): GslVector<T, H2> =
        produceDirtyVector(size).fill(initializer)
}

/**
 * Represents [Double] matrix context implementing where all the operations are delegated to GSL.
 */
public class GslRealMatrixContext(internal val scope: AutofreeScope) :
    GslMatrixContext<Double, gsl_matrix, gsl_vector>() {
    override fun produceDirtyMatrix(rows: Int, columns: Int): GslMatrix<Double, gsl_matrix> = GslRealMatrix(
        rawNativeHandle = requireNotNull(gsl_matrix_alloc(rows.toULong(), columns.toULong())),
        scope = scope,
    )

    override fun produceDirtyVector(size: Int): GslVector<Double, gsl_vector> =
        GslRealVector(rawNativeHandle = requireNotNull(gsl_vector_alloc(size.toULong())), scope = scope)

    public override fun Matrix<Double>.dot(other: Matrix<Double>): GslMatrix<Double, gsl_matrix> {
        val x = toGsl().nativeHandle
        val a = other.toGsl().nativeHandle
        val result = requireNotNull(gsl_matrix_calloc(a.pointed.size1, a.pointed.size2))
        gsl_blas_dgemm(CblasNoTrans, CblasNoTrans, 1.0, x, a, 1.0, result)
        return GslRealMatrix(result, scope = scope)
    }

    public override fun Matrix<Double>.dot(vector: Point<Double>): GslVector<Double, gsl_vector> {
        val x = toGsl().nativeHandle
        val a = vector.toGsl().nativeHandle
        val result = requireNotNull(gsl_vector_calloc(a.pointed.size))
        gsl_blas_dgemv(CblasNoTrans, 1.0, x, a, 1.0, result)
        return GslRealVector(result, scope = scope)
    }

    public override fun Matrix<Double>.times(value: Double): GslMatrix<Double, gsl_matrix> {
        val g1 = toGsl().copy()
        gsl_matrix_scale(g1.nativeHandle, value)
        return g1
    }

    public override fun add(a: Matrix<Double>, b: Matrix<Double>): GslMatrix<Double, gsl_matrix> {
        val g1 = a.toGsl().copy()
        gsl_matrix_add(g1.nativeHandle, b.toGsl().nativeHandle)
        return g1
    }

    public override fun multiply(a: Matrix<Double>, k: Number): GslMatrix<Double, gsl_matrix> {
        val g1 = a.toGsl().copy()
        gsl_matrix_scale(g1.nativeHandle, k.toDouble())
        return g1
    }

    public override fun Matrix<Double>.minus(b: Matrix<Double>): Matrix<Double> {
        val g1 = toGsl().copy()
        gsl_matrix_sub(g1.nativeHandle, b.toGsl().nativeHandle)
        return g1
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    @UnstableKMathAPI
    public override fun <F : Any> getFeature(m: Matrix<Double>, type: KClass<F>): F? = when (type) {
        LupDecompositionFeature::class -> object : LupDecompositionFeature<Double> {
            private val lup by lazy {
                val lu = m.toGsl().copy()
                val n = lu.rowNum
                val perm = gsl_permutation_alloc(n.toULong())

                memScoped {
                    val i = alloc<IntVar>()
                    gsl_linalg_LU_decomp(lu.nativeHandle, perm, i.ptr)
                }

                val one = produce(n, n) { i, j -> if (i == j) 1.0 else 0.0 }
                val p = produce(n, n) { _, _ -> 0.0 }

                for (j in 0 until perm!!.pointed.size.toInt()) {
                    println("7 $j")
                    val k = gsl_permutation_get(perm, j.toULong()).toInt()
                    val col = one.columns[k]
                    println(p.toGsl())
                    println(col.toList())
                    gsl_matrix_set_col(p.nativeHandle, j.toULong(), col.toGsl().nativeHandle)
                }

                gsl_permutation_free(perm)
                lu to p
            }

            override val l by lazy {
                VirtualMatrix(lup.first.shape[0], lup.first.shape[1]) { i, j ->
                    when {
                        j < i -> lup.first[i, j]
                        j == i -> 1.0
                        else -> 0.0
                    }
                } + LFeature
            }

            override val u by lazy {
                VirtualMatrix(
                    lup.first.shape[0],
                    lup.first.shape[1],
                ) { i, j -> if (j >= i) lup.first[i, j] else 0.0 } + UFeature
            }

            override val p by lazy(lup::second)
        }
        else -> super.getFeature(m, type)
    }?.let(type::cast)
}

/**
 * Invokes [block] inside newly created [GslRealMatrixContext] which is disposed when the block is invoked.
 */
public fun <R> GslRealMatrixContext(block: GslRealMatrixContext.() -> R): R =
    memScoped { GslRealMatrixContext(this).block() }

/**
 * Represents [Float] matrix context implementing where all the operations are delegated to GSL.
 */
public class GslFloatMatrixContext(internal val scope: AutofreeScope) :
    GslMatrixContext<Float, gsl_matrix_float, gsl_vector_float>() {
    override fun produceDirtyMatrix(rows: Int, columns: Int): GslMatrix<Float, gsl_matrix_float> = GslFloatMatrix(
        rawNativeHandle = requireNotNull(gsl_matrix_float_alloc(rows.toULong(), columns.toULong())),
        scope = scope,
    )

    override fun produceDirtyVector(size: Int): GslVector<Float, gsl_vector_float> =
        GslFloatVector(rawNativeHandle = requireNotNull(value = gsl_vector_float_alloc(size.toULong())), scope = scope)

    public override fun Matrix<Float>.dot(other: Matrix<Float>): GslMatrix<Float, gsl_matrix_float> {
        val x = toGsl().nativeHandle
        val a = other.toGsl().nativeHandle
        val result = requireNotNull(gsl_matrix_float_calloc(a.pointed.size1, a.pointed.size2))
        gsl_blas_sgemm(CblasNoTrans, CblasNoTrans, 1f, x, a, 1f, result)
        return GslFloatMatrix(rawNativeHandle = result, scope = scope)
    }

    public override fun Matrix<Float>.dot(vector: Point<Float>): GslVector<Float, gsl_vector_float> {
        val x = toGsl().nativeHandle
        val a = vector.toGsl().nativeHandle
        val result = requireNotNull(gsl_vector_float_calloc(a.pointed.size))
        gsl_blas_sgemv(CblasNoTrans, 1f, x, a, 1f, result)
        return GslFloatVector(rawNativeHandle = result, scope = scope)
    }

    public override fun Matrix<Float>.times(value: Float): GslMatrix<Float, gsl_matrix_float> {
        val g1 = toGsl().copy()
        gsl_matrix_float_scale(g1.nativeHandle, value.toDouble())
        return g1
    }

    public override fun add(a: Matrix<Float>, b: Matrix<Float>): GslMatrix<Float, gsl_matrix_float> {
        val g1 = a.toGsl().copy()
        gsl_matrix_float_add(g1.nativeHandle, b.toGsl().nativeHandle)
        return g1
    }

    public override fun multiply(a: Matrix<Float>, k: Number): GslMatrix<Float, gsl_matrix_float> {
        val g1 = a.toGsl().copy()
        gsl_matrix_float_scale(g1.nativeHandle, k.toDouble())
        return g1
    }

    public override fun Matrix<Float>.minus(b: Matrix<Float>): Matrix<Float> {
        val g1 = toGsl().copy()
        gsl_matrix_float_sub(g1.nativeHandle, b.toGsl().nativeHandle)
        return g1
    }
}

/**
 * Invokes [block] inside newly created [GslFloatMatrixContext] which is disposed when the block is invoked.
 */
public fun <R> GslFloatMatrixContext(block: GslFloatMatrixContext.() -> R): R =
    memScoped { GslFloatMatrixContext(this).block() }

/**
 * Represents [Complex] matrix context implementing where all the operations are delegated to GSL.
 */
public class GslComplexMatrixContext(internal val scope: AutofreeScope) :
    GslMatrixContext<Complex, gsl_matrix_complex, gsl_vector_complex>() {
    override fun produceDirtyMatrix(rows: Int, columns: Int): GslMatrix<Complex, gsl_matrix_complex> = GslComplexMatrix(
        rawNativeHandle = requireNotNull(gsl_matrix_complex_alloc(rows.toULong(), columns.toULong())),
        scope = scope,
    )

    override fun produceDirtyVector(size: Int): GslVector<Complex, gsl_vector_complex> =
        GslComplexVector(rawNativeHandle = requireNotNull(gsl_vector_complex_alloc(size.toULong())), scope = scope)

    public override fun Matrix<Complex>.dot(other: Matrix<Complex>): GslMatrix<Complex, gsl_matrix_complex> {
        val x = toGsl().nativeHandle
        val a = other.toGsl().nativeHandle
        val result = requireNotNull(gsl_matrix_complex_calloc(a.pointed.size1, a.pointed.size2))
        gsl_blas_zgemm(CblasNoTrans, CblasNoTrans, ComplexField.one.toGsl(), x, a, ComplexField.one.toGsl(), result)
        return GslComplexMatrix(rawNativeHandle = result, scope = scope)
    }

    public override fun Matrix<Complex>.dot(vector: Point<Complex>): GslVector<Complex, gsl_vector_complex> {
        val x = toGsl().nativeHandle
        val a = vector.toGsl().nativeHandle
        val result = requireNotNull(gsl_vector_complex_calloc(a.pointed.size))
        gsl_blas_zgemv(CblasNoTrans, ComplexField.one.toGsl(), x, a, ComplexField.one.toGsl(), result)
        return GslComplexVector(result, scope)
    }

    public override fun Matrix<Complex>.times(value: Complex): GslMatrix<Complex, gsl_matrix_complex> {
        val g1 = toGsl().copy()
        gsl_matrix_complex_scale(g1.nativeHandle, value.toGsl())
        return g1
    }

    public override fun add(a: Matrix<Complex>, b: Matrix<Complex>): GslMatrix<Complex, gsl_matrix_complex> {
        val g1 = a.toGsl().copy()
        gsl_matrix_complex_add(g1.nativeHandle, b.toGsl().nativeHandle)
        return g1
    }

    public override fun multiply(a: Matrix<Complex>, k: Number): GslMatrix<Complex, gsl_matrix_complex> {
        val g1 = a.toGsl().copy()
        gsl_matrix_complex_scale(g1.nativeHandle, k.toComplex().toGsl())
        return g1
    }

    public override fun Matrix<Complex>.minus(b: Matrix<Complex>): Matrix<Complex> {
        val g1 = toGsl().copy()
        gsl_matrix_complex_sub(g1.nativeHandle, b.toGsl().nativeHandle)
        return g1
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    @UnstableKMathAPI
    public override fun <F : Any> getFeature(m: Matrix<Complex>, type: KClass<F>): F? = when (type) {
        LupDecompositionFeature::class -> object : LupDecompositionFeature<Complex> {
            private val lup by lazy {
                val lu = m.toGsl().copy()
                val n = lu.rowNum
                val perm = gsl_permutation_alloc(n.toULong())

                memScoped {
                    val i = alloc<IntVar>()
                    gsl_linalg_complex_LU_decomp(lu.nativeHandle, perm, i.ptr)
                }

                val one = produce(n, n) { i, j -> if (i == j) 1.0.toComplex() else 0.0.toComplex() }
                val p = produce(n, n) { _, _ -> 0.0.toComplex() }

                for (j in 0 until perm!!.pointed.size.toInt()) {
                    println("7 $j")
                    val k = gsl_permutation_get(perm, j.toULong()).toInt()
                    val col = one.columns[k]
                    println(p.toGsl())
                    println(col.toList())
                    gsl_matrix_complex_set_col(p.nativeHandle, j.toULong(), col.toGsl().nativeHandle)
                }

                gsl_permutation_free(perm)
                lu to p
            }

            override val l by lazy {
                VirtualMatrix(lup.first.shape[0], lup.first.shape[1]) { i, j ->
                    when {
                        j < i -> lup.first[i, j]
                        j == i -> 1.0.toComplex()
                        else -> 0.0.toComplex()
                    }
                } + LFeature
            }

            override val u by lazy {
                VirtualMatrix(
                    lup.first.shape[0],
                    lup.first.shape[1],
                ) { i, j -> if (j >= i) lup.first[i, j] else 0.0.toComplex() } + UFeature
            }

            override val p by lazy(lup::second)
        }
        else -> super.getFeature(m, type)
    }?.let(type::cast)
}

/**
 * Invokes [block] inside newly created [GslComplexMatrixContext] which is disposed when the block is invoked.
 */
public fun <R> GslComplexMatrixContext(block: GslComplexMatrixContext.() -> R): R =
    memScoped { GslComplexMatrixContext(this).block() }
