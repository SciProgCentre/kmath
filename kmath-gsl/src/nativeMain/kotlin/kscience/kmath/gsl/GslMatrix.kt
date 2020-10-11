package kscience.kmath.gsl

import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.DeferScope
import kscience.kmath.linear.FeaturedMatrix
import kscience.kmath.structures.NDStructure

public abstract class GslMatrix<T : Any, H : CStructVar> internal constructor(scope: DeferScope) :
    GslMemoryHolder<H>(scope),
    FeaturedMatrix<T> {
    internal abstract operator fun set(i: Int, j: Int, value: T)
    internal abstract fun copy(): GslMatrix<T, H>

    public override fun equals(other: Any?): Boolean {
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    public final override fun hashCode(): Int {
        var result = nativeHandle.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }
}
