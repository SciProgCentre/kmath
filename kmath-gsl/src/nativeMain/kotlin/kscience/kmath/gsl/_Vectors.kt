package kscience.kmath.gsl

import kotlinx.cinterop.*
import org.gnu.gsl.*

internal class GslRealVector(override val nativeHandle: CPointer<gsl_vector>) : GslVector<Double, gsl_vector>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): Double = gsl_vector_get(nativeHandle, index.toULong())
    override fun set(index: Int, value: Double): Unit = gsl_vector_set(nativeHandle, index.toULong(), value)

    override fun copy(): GslRealVector {
        val new = requireNotNull(gsl_vector_alloc(size.toULong()))
        gsl_vector_memcpy(new, nativeHandle)
        return GslRealVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslRealVector) return gsl_vector_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_free(nativeHandle)
}

internal class GslFloatVector(override val nativeHandle: CPointer<gsl_vector_float>) : GslVector<Float, gsl_vector_float>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): Float = gsl_vector_float_get(nativeHandle, index.toULong())
    override fun set(index: Int, value: Float): Unit = gsl_vector_float_set(nativeHandle, index.toULong(), value)

    override fun copy(): GslFloatVector {
        val new = requireNotNull(gsl_vector_float_alloc(size.toULong()))
        gsl_vector_float_memcpy(new, nativeHandle)
        return GslFloatVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslFloatVector) return gsl_vector_float_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_float_free(nativeHandle)
}

internal class GslShortVector(override val nativeHandle: CPointer<gsl_vector_short>) : GslVector<Short, gsl_vector_short>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): Short = gsl_vector_short_get(nativeHandle, index.toULong())
    override fun set(index: Int, value: Short): Unit = gsl_vector_short_set(nativeHandle, index.toULong(), value)

    override fun copy(): GslShortVector {
        val new = requireNotNull(gsl_vector_short_alloc(size.toULong()))
        gsl_vector_short_memcpy(new, nativeHandle)
        return GslShortVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslShortVector) return gsl_vector_short_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_short_free(nativeHandle)
}

internal class GslUShortVector(override val nativeHandle: CPointer<gsl_vector_ushort>) : GslVector<UShort, gsl_vector_ushort>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): UShort = gsl_vector_ushort_get(nativeHandle, index.toULong())
    override fun set(index: Int, value: UShort): Unit = gsl_vector_ushort_set(nativeHandle, index.toULong(), value)

    override fun copy(): GslUShortVector {
        val new = requireNotNull(gsl_vector_ushort_alloc(size.toULong()))
        gsl_vector_ushort_memcpy(new, nativeHandle)
        return GslUShortVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslUShortVector) return gsl_vector_ushort_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_ushort_free(nativeHandle)
}

internal class GslLongVector(override val nativeHandle: CPointer<gsl_vector_long>) : GslVector<Long, gsl_vector_long>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): Long = gsl_vector_long_get(nativeHandle, index.toULong())
    override fun set(index: Int, value: Long): Unit = gsl_vector_long_set(nativeHandle, index.toULong(), value)

    override fun copy(): GslLongVector {
        val new = requireNotNull(gsl_vector_long_alloc(size.toULong()))
        gsl_vector_long_memcpy(new, nativeHandle)
        return GslLongVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslLongVector) return gsl_vector_long_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_long_free(nativeHandle)
}

internal class GslULongVector(override val nativeHandle: CPointer<gsl_vector_ulong>) : GslVector<ULong, gsl_vector_ulong>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): ULong = gsl_vector_ulong_get(nativeHandle, index.toULong())
    override fun set(index: Int, value: ULong): Unit = gsl_vector_ulong_set(nativeHandle, index.toULong(), value)

    override fun copy(): GslULongVector {
        val new = requireNotNull(gsl_vector_ulong_alloc(size.toULong()))
        gsl_vector_ulong_memcpy(new, nativeHandle)
        return GslULongVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslULongVector) return gsl_vector_ulong_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_ulong_free(nativeHandle)
}

internal class GslIntVector(override val nativeHandle: CPointer<gsl_vector_int>) : GslVector<Int, gsl_vector_int>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): Int = gsl_vector_int_get(nativeHandle, index.toULong())
    override fun set(index: Int, value: Int): Unit = gsl_vector_int_set(nativeHandle, index.toULong(), value)

    override fun copy(): GslIntVector {
        val new = requireNotNull(gsl_vector_int_alloc(size.toULong()))
        gsl_vector_int_memcpy(new, nativeHandle)
        return GslIntVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslIntVector) return gsl_vector_int_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_int_free(nativeHandle)
}

internal class GslUIntVector(override val nativeHandle: CPointer<gsl_vector_uint>) : GslVector<UInt, gsl_vector_uint>() {
    override val size: Int
        get() = nativeHandle.pointed.size.toInt()

    override fun get(index: Int): UInt = gsl_vector_uint_get(nativeHandle, index.toULong())
    override fun set(index: Int, value: UInt): Unit = gsl_vector_uint_set(nativeHandle, index.toULong(), value)

    override fun copy(): GslUIntVector {
        val new = requireNotNull(gsl_vector_uint_alloc(size.toULong()))
        gsl_vector_uint_memcpy(new, nativeHandle)
        return GslUIntVector(new)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GslUIntVector) return gsl_vector_uint_equal(nativeHandle, other.nativeHandle) == 1
        return super.equals(other)
    }

    override fun close(): Unit = gsl_vector_uint_free(nativeHandle)
}

