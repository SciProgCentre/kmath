package kscience.kmath.gsl

import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.CValues
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kscience.kmath.linear.FeaturedMatrix
import kscience.kmath.linear.MatrixFeature
import kscience.kmath.structures.NDStructure
import org.gnu.gsl.gsl_matrix
import org.gnu.gsl.gsl_matrix_equal
import org.gnu.gsl.gsl_matrix_get

public sealed class GslMatrix<T : Any> : FeaturedMatrix<T> {
    public abstract val nativeHandle: CValues<out CStructVar>

    public override fun hashCode(): Int {
        var result = nativeHandle.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }
}

public class GslRealMatrix(public override val nativeHandle: CValues<gsl_matrix>, features: Set<MatrixFeature>) :
    GslMatrix<Double>() {

    public override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    public override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    public override val shape: IntArray
        get() = intArrayOf(rowNum, colNum)

    public override val features: Set<MatrixFeature> = features

    public override fun suggestFeature(vararg features: MatrixFeature): GslRealMatrix =
        GslRealMatrix(nativeHandle, this.features + features)

    public override fun get(i: Int, j: Int): Double = gsl_matrix_get(nativeHandle, i.toULong(), j.toULong())

    public override fun equals(other: Any?): Boolean {
        if (other is GslRealMatrix) gsl_matrix_equal(nativeHandle, other.nativeHandle)
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }
}
