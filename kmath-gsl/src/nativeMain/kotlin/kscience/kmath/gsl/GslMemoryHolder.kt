package kscience.kmath.gsl

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.DeferScope

/**
 * Represents managed native GSL object. The only property this class holds is pointer to the GSL object. In order to be
 * freed this class's object must be added to [DeferScope].
 *
 * @param scope the scope where this object is declared.
 */
public abstract class GslMemoryHolder<H : CStructVar> internal constructor(internal val scope: DeferScope) {
    internal abstract val nativeHandle: CPointer<H>

    init {
        ensureHasGslErrorHandler()
        scope.defer(::close)
    }

    internal abstract fun close()
}
