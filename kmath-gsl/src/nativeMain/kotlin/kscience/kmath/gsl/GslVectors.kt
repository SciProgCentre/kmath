package kscience.kmath.gsl

import kotlinx.cinterop.*
import kscience.kmath.linear.Point
import kscience.kmath.operations.Complex
import org.gnu.gsl.*

public sealed class GslVector<T> : StructHolder(), Point<T> {
    public override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var cursor = 0

        override fun hasNext(): Boolean = cursor < size

        override fun next(): T {
            cursor++
            return this@GslVector[cursor - 1]
        }
    }
}

public class GslRealVector(override val nativeHandle: CValues<gsl_vector>) : GslVector<Double>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): Double = gsl_vector_get(nativeHandle, index.toULong())
}

public class GslFloatVector(override val nativeHandle: CValues<gsl_vector_float>) : GslVector<Float>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): Float = gsl_vector_float_get(nativeHandle, index.toULong())
}

public class GslIntVector(override val nativeHandle: CValues<gsl_vector_int>) : GslVector<Int>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): Int = gsl_vector_int_get(nativeHandle, index.toULong())
}

public class GslUIntVector(override val nativeHandle: CValues<gsl_vector_uint>) : GslVector<UInt>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): UInt = gsl_vector_uint_get(nativeHandle, index.toULong())
}

public class GslLongVector(override val nativeHandle: CValues<gsl_vector_long>) : GslVector<Long>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): Long = gsl_vector_long_get(nativeHandle, index.toULong())
}

public class GslULongVector(override val nativeHandle: CValues<gsl_vector_ulong>) : GslVector<ULong>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): ULong = gsl_vector_ulong_get(nativeHandle, index.toULong())
}

public class GslShortVector(override val nativeHandle: CValues<gsl_vector_short>) : GslVector<Short>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): Short = gsl_vector_short_get(nativeHandle, index.toULong())
}

public class GslUShortVector(override val nativeHandle: CValues<gsl_vector_ushort>) : GslVector<UShort>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): UShort = gsl_vector_ushort_get(nativeHandle, index.toULong())
}

public class GslComplexVector(override val nativeHandle: CValues<gsl_vector_complex>) : GslVector<Complex>() {
    public override val size: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size.toInt() }

    public override fun get(index: Int): Complex = gsl_vector_complex_get(nativeHandle, index.toULong()).useContents {
        Complex(dat[0], dat[1])
    }
}
