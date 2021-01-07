package kscience.kmath.gsl

import kotlinx.cinterop.*
import org.gnu.gsl.*

internal class GslRealVector(override val nativeHandle: CPointer<gsl_vector>, scope: DeferScope) : 
    GslVector<Double, gsl_vector>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): Double = 
        gsl_vector_get(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: Double): Unit = 
        gsl_vector_set(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): GslRealVector {
        val new = requireNotNull(gsl_vector_alloc(size.toULong()))
        gsl_vector_memcpy(new, nativeHandleChecked())
        return GslRealVector(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslRealVector)
            return gsl_vector_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_free(nativeHandleChecked())
}

internal class GslFloatVector(override val nativeHandle: CPointer<gsl_vector_float>, scope: DeferScope) : 
    GslVector<Float, gsl_vector_float>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): Float = 
        gsl_vector_float_get(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: Float): Unit = 
        gsl_vector_float_set(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): GslFloatVector {
        val new = requireNotNull(gsl_vector_float_alloc(size.toULong()))
        gsl_vector_float_memcpy(new, nativeHandleChecked())
        return GslFloatVector(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslFloatVector)
            return gsl_vector_float_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_float_free(nativeHandleChecked())
}

internal class GslShortVector(override val nativeHandle: CPointer<gsl_vector_short>, scope: DeferScope) : 
    GslVector<Short, gsl_vector_short>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): Short = 
        gsl_vector_short_get(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: Short): Unit = 
        gsl_vector_short_set(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): GslShortVector {
        val new = requireNotNull(gsl_vector_short_alloc(size.toULong()))
        gsl_vector_short_memcpy(new, nativeHandleChecked())
        return GslShortVector(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslShortVector)
            return gsl_vector_short_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_short_free(nativeHandleChecked())
}

internal class GslUShortVector(override val nativeHandle: CPointer<gsl_vector_ushort>, scope: DeferScope) : 
    GslVector<UShort, gsl_vector_ushort>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): UShort = 
        gsl_vector_ushort_get(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: UShort): Unit = 
        gsl_vector_ushort_set(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): GslUShortVector {
        val new = requireNotNull(gsl_vector_ushort_alloc(size.toULong()))
        gsl_vector_ushort_memcpy(new, nativeHandleChecked())
        return GslUShortVector(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslUShortVector)
            return gsl_vector_ushort_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_ushort_free(nativeHandleChecked())
}

internal class GslLongVector(override val nativeHandle: CPointer<gsl_vector_long>, scope: DeferScope) : 
    GslVector<Long, gsl_vector_long>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): Long = 
        gsl_vector_long_get(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: Long): Unit = 
        gsl_vector_long_set(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): GslLongVector {
        val new = requireNotNull(gsl_vector_long_alloc(size.toULong()))
        gsl_vector_long_memcpy(new, nativeHandleChecked())
        return GslLongVector(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslLongVector)
            return gsl_vector_long_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_long_free(nativeHandleChecked())
}

internal class GslULongVector(override val nativeHandle: CPointer<gsl_vector_ulong>, scope: DeferScope) : 
    GslVector<ULong, gsl_vector_ulong>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): ULong = 
        gsl_vector_ulong_get(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: ULong): Unit = 
        gsl_vector_ulong_set(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): GslULongVector {
        val new = requireNotNull(gsl_vector_ulong_alloc(size.toULong()))
        gsl_vector_ulong_memcpy(new, nativeHandleChecked())
        return GslULongVector(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslULongVector)
            return gsl_vector_ulong_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_ulong_free(nativeHandleChecked())
}

internal class GslIntVector(override val nativeHandle: CPointer<gsl_vector_int>, scope: DeferScope) : 
    GslVector<Int, gsl_vector_int>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): Int = 
        gsl_vector_int_get(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: Int): Unit = 
        gsl_vector_int_set(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): GslIntVector {
        val new = requireNotNull(gsl_vector_int_alloc(size.toULong()))
        gsl_vector_int_memcpy(new, nativeHandleChecked())
        return GslIntVector(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslIntVector)
            return gsl_vector_int_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_int_free(nativeHandleChecked())
}

internal class GslUIntVector(override val nativeHandle: CPointer<gsl_vector_uint>, scope: DeferScope) : 
    GslVector<UInt, gsl_vector_uint>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): UInt = 
        gsl_vector_uint_get(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: UInt): Unit = 
        gsl_vector_uint_set(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): GslUIntVector {
        val new = requireNotNull(gsl_vector_uint_alloc(size.toULong()))
        gsl_vector_uint_memcpy(new, nativeHandleChecked())
        return GslUIntVector(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslUIntVector)
            return gsl_vector_uint_equal(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_uint_free(nativeHandleChecked())
}

