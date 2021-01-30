package kscience.kmath.gsl

import kotlinx.cinterop.AutofreeScope
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed
import org.gnu.gsl.gsl_permutation
import org.gnu.gsl.gsl_permutation_free
import org.gnu.gsl.gsl_permutation_get

internal class GslPermutation(
    override val rawNativeHandle: CPointer<gsl_permutation>,
    scope: AutofreeScope,
    owned: Boolean,
) : GslObject<gsl_permutation>(scope, owned) {
    val size get() = nativeHandle.pointed.size.toInt()

    operator fun get(i: Int) = gsl_permutation_get(nativeHandle, i.toULong()).toInt()
    override fun close() = gsl_permutation_free(nativeHandle)
}
