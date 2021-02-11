package kscience.kmath.gsl

import kotlinx.cinterop.*
import kscience.kmath.complex.Complex
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.VirtualBuffer
import org.gnu.gsl.*

internal fun CValue<gsl_complex>.toKMath(): Complex = useContents { Complex(dat[0], dat[1]) }

internal fun Complex.toGsl(): CValue<gsl_complex> = cValue {
    dat[0] = re
    dat[1] = im
}

internal class GslComplexMatrix(
    override val rawNativeHandle: CPointer<gsl_matrix_complex>,
    scope: AutofreeScope,
    owned: Boolean,
) : GslMatrix<Complex, gsl_matrix_complex>(scope, owned) {
    override val rowNum: Int get() = nativeHandle.pointed.size1.toInt()
    override val colNum: Int get() = nativeHandle.pointed.size2.toInt()

    override val rows: Buffer<Buffer<Complex>>
        get() = VirtualBuffer(rowNum) { r ->
            GslComplexVector(
                gsl_matrix_complex_row(nativeHandle, r.toULong()).placeTo(scope).pointed.vector.ptr,
                scope,
                false,
            )
        }

    override val columns: Buffer<Buffer<Complex>>
        get() = VirtualBuffer(rowNum) { c ->
            GslComplexVector(
                gsl_matrix_complex_column(nativeHandle, c.toULong()).placeTo(scope).pointed.vector.ptr,
                scope,
                false,
            )
        }

    override operator fun get(i: Int, j: Int): Complex =
        gsl_matrix_complex_get(nativeHandle, i.toULong(), j.toULong()).toKMath()

    override operator fun set(i: Int, j: Int, value: Complex): Unit =
        gsl_matrix_complex_set(nativeHandle, i.toULong(), j.toULong(), value.toGsl())

    override fun copy(): GslComplexMatrix {
        val new = checkNotNull(gsl_matrix_complex_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_complex_memcpy(new, nativeHandle)
        return GslComplexMatrix(new, scope, true)
    }

    override fun close(): Unit = gsl_matrix_complex_free(nativeHandle)

    override fun equals(other: Any?): Boolean {
        if (other is GslComplexMatrix)
            return gsl_matrix_complex_equal(nativeHandle, other.nativeHandle) == 1

        return super.equals(other)
    }
}

internal class GslComplexVector(
    override val rawNativeHandle: CPointer<gsl_vector_complex>,
    scope: AutofreeScope,
    owned: Boolean,
) : GslVector<Complex, gsl_vector_complex>(scope, owned) {
    override val size: Int get() = nativeHandle.pointed.size.toInt()
    override fun get(index: Int): Complex = gsl_vector_complex_get(nativeHandle, index.toULong()).toKMath()

    override fun set(index: Int, value: Complex): Unit =
        gsl_vector_complex_set(nativeHandle, index.toULong(), value.toGsl())

    override fun copy(): GslComplexVector {
        val new = checkNotNull(gsl_vector_complex_alloc(size.toULong()))
        gsl_vector_complex_memcpy(new, nativeHandle)
        return GslComplexVector(new, scope, true)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslComplexVector)
            return gsl_vector_complex_equal(nativeHandle, other.nativeHandle) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_complex_free(nativeHandle)
}
