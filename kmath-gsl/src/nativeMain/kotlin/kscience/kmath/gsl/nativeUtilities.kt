package kscience.kmath.gsl

import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.CValues

public abstract class StructHolder internal constructor() {
    protected abstract val nativeHandle: CValues<out CStructVar>
}
