package space.kscience.kmath.torch

import space.kscience.kmath.memory.DeferScope

public abstract class TorchTensorMemoryHolder internal constructor(
    public val scope: DeferScope
) {
    init {
        scope.defer(::close)
    }
    protected abstract fun close(): Unit

    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0
}