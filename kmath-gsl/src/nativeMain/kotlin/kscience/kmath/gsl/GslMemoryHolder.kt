package kscience.kmath.gsl

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CStructVar
import kotlinx.io.Closeable

public abstract class GslMemoryHolder<H : CStructVar> internal constructor() : Closeable {
    internal abstract val nativeHandle: CPointer<H>
}
