package kscience.kmath.gsl

import kotlinx.cinterop.AutofreeScope
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.DeferScope

/**
 * Represents managed native GSL object. The only property this class holds is pointer to the GSL object. In order to be
 * freed this class's object must be added to [DeferScope].
 *
 * The objects of this type shouldn't be used after being disposed by the scope.
 *
 * @param scope the scope where this object is declared.
 */
public abstract class GslObject<H : CStructVar> internal constructor(internal val scope: AutofreeScope, internal val owned: Boolean) {
    internal abstract val rawNativeHandle: CPointer<H>
    private var isClosed: Boolean = false

    internal val nativeHandle: CPointer<H>
        get() {
            check(!isClosed) { "The use of GSL object that is closed." }
            return rawNativeHandle
        }

    init {
        ensureHasGslErrorHandler()

        scope.defer {
            if (owned) close()
            isClosed = true
        }
    }

    internal abstract fun close()
}
