package kscience.kmath.gsl

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
public abstract class GslMemoryHolder<H : CStructVar> internal constructor(internal val scope: DeferScope) {
    internal abstract val nativeHandle: CPointer<H>
    private var isClosed: Boolean = false

    init {
        ensureHasGslErrorHandler()

        scope.defer {
            close()
            isClosed = true
        }
    }

    internal fun nativeHandleChecked(): CPointer<H> {
        check(!isClosed) { "The use of GSL object that is closed." }
        return nativeHandle
    }

    internal abstract fun close()
}
