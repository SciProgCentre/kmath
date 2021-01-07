package kscience.kmath.gsl

import kotlinx.cinterop.*
import kscience.kmath.linear.*
import org.gnu.gsl.*

internal class GslRealMatrix(
    override val nativeHandle: CPointer<gsl_matrix>,
    features: Set<MatrixFeature> = emptySet(),
    scope: DeferScope
) : GslMatrix<Double, gsl_matrix>(scope) {
    override val rowNum: Int
        get() = nativeHandleChecked().pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandleChecked().pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslRealMatrix =
        GslRealMatrix(nativeHandleChecked(), this.features + features, scope)

    override operator fun get(i: Int, j: Int): Double = gsl_matrix_get(nativeHandleChecked(), i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: Double): Unit =
        gsl_matrix_set(nativeHandleChecked(), i.toULong(), j.toULong(), value)

    override fun copy(): GslRealMatrix {
        val new = requireNotNull(gsl_matrix_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_memcpy(new, nativeHandleChecked())
        return GslRealMatrix(new, features, scope)
    }

    override fun close(): Unit = gsl_matrix_free(nativeHandleChecked())

    override fun equals(other: Any?): Boolean {
        if (other is GslRealMatrix) return gsl_matrix_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1
        return super.equals(other)
    }
}

internal class GslFloatMatrix(
    override val nativeHandle: CPointer<gsl_matrix_float>,
    features: Set<MatrixFeature> = emptySet(),
    scope: DeferScope
) : GslMatrix<Float, gsl_matrix_float>(scope) {
    override val rowNum: Int
        get() = nativeHandleChecked().pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandleChecked().pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslFloatMatrix =
        GslFloatMatrix(nativeHandleChecked(), this.features + features, scope)

    override operator fun get(i: Int, j: Int): Float = gsl_matrix_float_get(nativeHandleChecked(), i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: Float): Unit =
        gsl_matrix_float_set(nativeHandleChecked(), i.toULong(), j.toULong(), value)

    override fun copy(): GslFloatMatrix {
        val new = requireNotNull(gsl_matrix_float_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_float_memcpy(new, nativeHandleChecked())
        return GslFloatMatrix(new, features, scope)
    }

    override fun close(): Unit = gsl_matrix_float_free(nativeHandleChecked())

    override fun equals(other: Any?): Boolean {
        if (other is GslFloatMatrix) return gsl_matrix_float_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1
        return super.equals(other)
    }
}

internal class GslShortMatrix(
    override val nativeHandle: CPointer<gsl_matrix_short>,
    features: Set<MatrixFeature> = emptySet(),
    scope: DeferScope
) : GslMatrix<Short, gsl_matrix_short>(scope) {
    override val rowNum: Int
        get() = nativeHandleChecked().pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandleChecked().pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslShortMatrix =
        GslShortMatrix(nativeHandleChecked(), this.features + features, scope)

    override operator fun get(i: Int, j: Int): Short = gsl_matrix_short_get(nativeHandleChecked(), i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: Short): Unit =
        gsl_matrix_short_set(nativeHandleChecked(), i.toULong(), j.toULong(), value)

    override fun copy(): GslShortMatrix {
        val new = requireNotNull(gsl_matrix_short_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_short_memcpy(new, nativeHandleChecked())
        return GslShortMatrix(new, features, scope)
    }

    override fun close(): Unit = gsl_matrix_short_free(nativeHandleChecked())

    override fun equals(other: Any?): Boolean {
        if (other is GslShortMatrix) return gsl_matrix_short_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1
        return super.equals(other)
    }
}

internal class GslUShortMatrix(
    override val nativeHandle: CPointer<gsl_matrix_ushort>,
    features: Set<MatrixFeature> = emptySet(),
    scope: DeferScope
) : GslMatrix<UShort, gsl_matrix_ushort>(scope) {
    override val rowNum: Int
        get() = nativeHandleChecked().pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandleChecked().pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslUShortMatrix =
        GslUShortMatrix(nativeHandleChecked(), this.features + features, scope)

    override operator fun get(i: Int, j: Int): UShort = gsl_matrix_ushort_get(nativeHandleChecked(), i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: UShort): Unit =
        gsl_matrix_ushort_set(nativeHandleChecked(), i.toULong(), j.toULong(), value)

    override fun copy(): GslUShortMatrix {
        val new = requireNotNull(gsl_matrix_ushort_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_ushort_memcpy(new, nativeHandleChecked())
        return GslUShortMatrix(new, features, scope)
    }

    override fun close(): Unit = gsl_matrix_ushort_free(nativeHandleChecked())

    override fun equals(other: Any?): Boolean {
        if (other is GslUShortMatrix) return gsl_matrix_ushort_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1
        return super.equals(other)
    }
}

internal class GslLongMatrix(
    override val nativeHandle: CPointer<gsl_matrix_long>,
    features: Set<MatrixFeature> = emptySet(),
    scope: DeferScope
) : GslMatrix<Long, gsl_matrix_long>(scope) {
    override val rowNum: Int
        get() = nativeHandleChecked().pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandleChecked().pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslLongMatrix =
        GslLongMatrix(nativeHandleChecked(), this.features + features, scope)

    override operator fun get(i: Int, j: Int): Long = gsl_matrix_long_get(nativeHandleChecked(), i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: Long): Unit =
        gsl_matrix_long_set(nativeHandleChecked(), i.toULong(), j.toULong(), value)

    override fun copy(): GslLongMatrix {
        val new = requireNotNull(gsl_matrix_long_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_long_memcpy(new, nativeHandleChecked())
        return GslLongMatrix(new, features, scope)
    }

    override fun close(): Unit = gsl_matrix_long_free(nativeHandleChecked())

    override fun equals(other: Any?): Boolean {
        if (other is GslLongMatrix) return gsl_matrix_long_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1
        return super.equals(other)
    }
}

internal class GslULongMatrix(
    override val nativeHandle: CPointer<gsl_matrix_ulong>,
    features: Set<MatrixFeature> = emptySet(),
    scope: DeferScope
) : GslMatrix<ULong, gsl_matrix_ulong>(scope) {
    override val rowNum: Int
        get() = nativeHandleChecked().pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandleChecked().pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslULongMatrix =
        GslULongMatrix(nativeHandleChecked(), this.features + features, scope)

    override operator fun get(i: Int, j: Int): ULong = gsl_matrix_ulong_get(nativeHandleChecked(), i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: ULong): Unit =
        gsl_matrix_ulong_set(nativeHandleChecked(), i.toULong(), j.toULong(), value)

    override fun copy(): GslULongMatrix {
        val new = requireNotNull(gsl_matrix_ulong_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_ulong_memcpy(new, nativeHandleChecked())
        return GslULongMatrix(new, features, scope)
    }

    override fun close(): Unit = gsl_matrix_ulong_free(nativeHandleChecked())

    override fun equals(other: Any?): Boolean {
        if (other is GslULongMatrix) return gsl_matrix_ulong_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1
        return super.equals(other)
    }
}

internal class GslIntMatrix(
    override val nativeHandle: CPointer<gsl_matrix_int>,
    features: Set<MatrixFeature> = emptySet(),
    scope: DeferScope
) : GslMatrix<Int, gsl_matrix_int>(scope) {
    override val rowNum: Int
        get() = nativeHandleChecked().pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandleChecked().pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslIntMatrix =
        GslIntMatrix(nativeHandleChecked(), this.features + features, scope)

    override operator fun get(i: Int, j: Int): Int = gsl_matrix_int_get(nativeHandleChecked(), i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: Int): Unit =
        gsl_matrix_int_set(nativeHandleChecked(), i.toULong(), j.toULong(), value)

    override fun copy(): GslIntMatrix {
        val new = requireNotNull(gsl_matrix_int_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_int_memcpy(new, nativeHandleChecked())
        return GslIntMatrix(new, features, scope)
    }

    override fun close(): Unit = gsl_matrix_int_free(nativeHandleChecked())

    override fun equals(other: Any?): Boolean {
        if (other is GslIntMatrix) return gsl_matrix_int_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1
        return super.equals(other)
    }
}

internal class GslUIntMatrix(
    override val nativeHandle: CPointer<gsl_matrix_uint>,
    features: Set<MatrixFeature> = emptySet(),
    scope: DeferScope
) : GslMatrix<UInt, gsl_matrix_uint>(scope) {
    override val rowNum: Int
        get() = nativeHandleChecked().pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandleChecked().pointed.size2.toInt()

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): GslUIntMatrix =
        GslUIntMatrix(nativeHandleChecked(), this.features + features, scope)

    override operator fun get(i: Int, j: Int): UInt = gsl_matrix_uint_get(nativeHandleChecked(), i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: UInt): Unit =
        gsl_matrix_uint_set(nativeHandleChecked(), i.toULong(), j.toULong(), value)

    override fun copy(): GslUIntMatrix {
        val new = requireNotNull(gsl_matrix_uint_alloc(rowNum.toULong(), colNum.toULong()))
        gsl_matrix_uint_memcpy(new, nativeHandleChecked())
        return GslUIntMatrix(new, features, scope)
    }

    override fun close(): Unit = gsl_matrix_uint_free(nativeHandleChecked())

    override fun equals(other: Any?): Boolean {
        if (other is GslUIntMatrix) return gsl_matrix_uint_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1
        return super.equals(other)
    }
}

