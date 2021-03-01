package space.kscience.kmath.memory

public expect class DeferScope {
    public inline fun defer(crossinline block: () -> Unit)
}

public expect inline fun <R> withDeferScope(block: DeferScope.() -> R): R
