package kscience.kmath.gsl

import kotlinx.cinterop.*
import kscience.kmath.linear.MatrixFeature
import kscience.kmath.operations.Complex
import org.gnu.gsl.*

internal fun CValue<gsl_complex>.toKMath(): Complex = useContents { Complex(dat[0], dat[1]) }

internal fun Complex.toGsl(): CValue<gsl_complex> = cValue {
    dat[0] = re
    dat[1] = im
}

internal class GslComplexMatrix(
    override val nativeHandle: CPointer<gsl_matrix_complex>,
    features: Set<MatrixFeature> = emptySet()
) : GslMatrix<Complex, gsl_matrix_complex>() {
    override val rowNum: Int
        get() = nativeHandle.pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandle.pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslComplexMatrix =
        GslComplexMatrix(nativeHandle, this.features + features)

    override operator fun get(i: Int, j: Int): Complex =
        gsl_matrix_complex_get(nativeHandle, i.toULong(), j.toULong()).toKMath()

    override operator fun set(i: Int, j: Int, value: Complex): Unit =
        gsl_matrix_complex_set(nativeHandle, i.toULong(), j.toULong(), value.toGsl())

    override fun copy(): GslComplexMatrix {
        val new = requireNotNull(gsl_matrix_complex_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_complex_memcpy(new, nativeHandle)
        return GslComplexMatrix(new, features)
    }

    override fun close(): Unit = gsl_matrix_complex_free(nativeHandle)

    override fun equals(other: Any?): Boolean {
        if (other is GslComplexMatrix) return gsl_matrix_complex_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }
}

internal class GslComplexVector(override val nativeHandle: CPointer<gsl_vector_complex>) :
    GslVector<Complex, gsl_vector_complex>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): Complex = gsl_vector_complex_get(nativeHandle, index.toULong()).toKMath()
    override fun set(index: Int, value: Complex): Unit = gsl_vector_complex_set(nativeHandle, index.toULong(), value.toGsl())

    override fun copy(): GslComplexVector {
        val new = requireNotNull(gsl_vector_complex_alloc(size.toULong()))
        gsl_vector_complex_memcpy(new, nativeHandle)
        return GslComplexVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslComplexVector) return gsl_vector_complex_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_complex_free(nativeHandle)
}
