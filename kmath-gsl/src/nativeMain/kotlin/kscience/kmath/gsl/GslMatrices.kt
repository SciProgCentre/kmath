package kscience.kmath.gsl

import kotlinx.cinterop.*
import kscience.kmath.linear.FeaturedMatrix
import kscience.kmath.linear.MatrixFeature
import kscience.kmath.operations.Complex
import kscience.kmath.structures.NDStructure
import org.gnu.gsl.*

public sealed class GslMatrix<T : Any> : FeaturedMatrix<T> {
    protected abstract val nativeHandle: CValues<out CStructVar>

    override fun equals(other: Any?): Boolean {
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    public override fun hashCode(): Int {
        var result = nativeHandle.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }
}

public class GslRealMatrix(protected override val nativeHandle: CValues<gsl_matrix>, features: Set<MatrixFeature>) :
    GslMatrix<Double>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslRealMatrix =
        GslRealMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): Double = gsl_matrix_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslRealMatrix) gsl_matrix_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}

public class GslIntMatrix(protected override val nativeHandle: CValues<gsl_matrix_int>, features: Set<MatrixFeature>) :
    GslMatrix<Int>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslIntMatrix =
        GslIntMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): Int = gsl_matrix_int_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslIntMatrix) gsl_matrix_int_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}

public class GslLongMatrix(
    protected override val nativeHandle: CValues<gsl_matrix_long>,
    features: Set<MatrixFeature>
) :
    GslMatrix<Long>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslLongMatrix =
        GslLongMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): Long = gsl_matrix_long_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslLongMatrix) gsl_matrix_long_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}

public class GslFloatMatrix(
    protected override val nativeHandle: CValues<gsl_matrix_float>,
    features: Set<MatrixFeature>
) :
    GslMatrix<Float>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslFloatMatrix =
        GslFloatMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): Float = gsl_matrix_float_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslFloatMatrix) gsl_matrix_float_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}

public class GslUIntMatrix(
    protected override val nativeHandle: CValues<gsl_matrix_uint>,
    features: Set<MatrixFeature>
) : GslMatrix<UInt>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslUIntMatrix =
        GslUIntMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): UInt = gsl_matrix_uint_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslUIntMatrix) gsl_matrix_uint_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}

public class GslULongMatrix(
    protected override val nativeHandle: CValues<gsl_matrix_ulong>,
    features: Set<MatrixFeature>
) : GslMatrix<ULong>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslULongMatrix =
        GslULongMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): ULong = gsl_matrix_ulong_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslULongMatrix) gsl_matrix_ulong_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}

public class GslUShortMatrix(
    protected override val nativeHandle: CValues<gsl_matrix_ushort>,
    features: Set<MatrixFeature>
) : GslMatrix<UShort>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslUShortMatrix =
        GslUShortMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): UShort = gsl_matrix_ushort_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslUShortMatrix) gsl_matrix_ushort_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}

public class GslShortMatrix(
    protected override val nativeHandle: CValues<gsl_matrix_short>,
    features: Set<MatrixFeature>
) : GslMatrix<Short>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslShortMatrix =
        GslShortMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): Short = gsl_matrix_short_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslShortMatrix) gsl_matrix_short_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}

public class GslComplexMatrix(
    protected override val nativeHandle: CValues<gsl_matrix_complex>,
    features: Set<MatrixFeature>
) : GslMatrix<Complex>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslComplexMatrix =
        GslComplexMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): Complex =
        gsl_matrix_complex_get(nativeHandle, i.toULong(), j.toULong()).useContents { Complex(dat[0], dat[1]) }

    public override fun equals(other: Any?): Boolean {
        if (other is GslComplexMatrix) gsl_matrix_complex_equal(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}
