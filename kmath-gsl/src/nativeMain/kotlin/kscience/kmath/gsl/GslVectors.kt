package kscience.kmath.gsl

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kscience.kmath.linear.Point
import kscience.kmath.operations.Complex
import org.gnu.gsl.*

public abstract class GslVector<T, H : CStructVar> internal constructor(): GslMemoryHolder<H>(), Point<T> {
    public override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var cursor = 0

        override fun hasNext(): Boolean = cursor < size

        override fun next(): T {
            cursor++
            return this@GslVector[cursor - 1]
        }
    }
}

internal class GslRealVector(override val nativeHandle: CPointer<gsl_vector>) : GslVector<Double, gsl_vector>() {
    override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    override fun get(index: Int): Double = gsl_vector_get(nativeHandle, index.toULong())
    override fun close(): Unit = gsl_vector_free(nativeHandle)
}

internal class GslFloatVector(override val nativeHandle: CPointer<gsl_vector_float>) :
    GslVector<Float, gsl_vector_float>() {
    override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    override fun get(index: Int): Float = gsl_vector_float_get(nativeHandle, index.toULong())
    override fun close(): Unit = gsl_vector_float_free(nativeHandle)
}

internal class GslIntVector(override val nativeHandle: CPointer<gsl_vector_int>) : GslVector<Int, gsl_vector_int>() {
    override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    override fun get(index: Int): Int = gsl_vector_int_get(nativeHandle, index.toULong())
    override fun close(): Unit = gsl_vector_int_free(nativeHandle)
}

internal class GslUIntVector(override val nativeHandle: CPointer<gsl_vector_uint>) :
    GslVector<UInt, gsl_vector_uint>() {
    override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    override fun get(index: Int): UInt = gsl_vector_uint_get(nativeHandle, index.toULong())
    override fun close(): Unit = gsl_vector_uint_free(nativeHandle)
}

internal class GslLongVector(override val nativeHandle: CPointer<gsl_vector_long>) :
    GslVector<Long, gsl_vector_long>() {
    override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    override fun get(index: Int): Long = gsl_vector_long_get(nativeHandle, index.toULong())
    override fun close(): Unit = gsl_vector_long_free(nativeHandle)
}

internal class GslULongVector(override val nativeHandle: CPointer<gsl_vector_ulong>) :
    GslVector<ULong, gsl_vector_ulong>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): ULong = gsl_vector_ulong_get(nativeHandle, index.toULong())
    public override fun close(): Unit = gsl_vector_ulong_free(nativeHandle)
}

internal class GslShortVector(override val nativeHandle: CPointer<gsl_vector_short>) :
    GslVector<Short, gsl_vector_short>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): Short = gsl_vector_short_get(nativeHandle, index.toULong())
    public override fun close(): Unit = gsl_vector_short_free(nativeHandle)
}

internal class GslUShortVector(override val nativeHandle: CPointer<gsl_vector_ushort>) :
    GslVector<UShort, gsl_vector_ushort>() {
    override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    override fun get(index: Int): UShort = gsl_vector_ushort_get(nativeHandle, index.toULong())
    override fun close(): Unit = gsl_vector_ushort_free(nativeHandle)
}

internal class GslComplexVector(override val nativeHandle: CPointer<gsl_vector_complex>) :
    GslVector<Complex, gsl_vector_complex>() {
    override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    override fun get(index: Int): Complex = gsl_vector_complex_get(nativeHandle, index.toULong()).toKMath()
    override fun close(): Unit = gsl_vector_complex_free(nativeHandle)
}
