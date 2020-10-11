package kscience.kmath.gsl

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.DeferScope

public abstract class GslMemoryHolder<H : CStructVar> internal constructor(internal val scope: DeferScope) {
    internal abstract val nativeHandle: CPointer<H>

    init {
        scope.defer(::close)
    }

    internal abstract fun close()
}
