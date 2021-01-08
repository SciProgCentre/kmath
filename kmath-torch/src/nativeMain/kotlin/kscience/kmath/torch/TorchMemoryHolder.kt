package kscience.kmath.torch

import kotlinx.cinterop.*
import ctorch.*

public abstract class TorchMemoryHolder internal constructor(
    internal val scope: DeferScope,
    internal var tensorHandle: COpaquePointer?
){
    init {
        scope.defer(::close)
    }

    protected fun close() {
        dispose_tensor(tensorHandle)
        tensorHandle = null
    }
}