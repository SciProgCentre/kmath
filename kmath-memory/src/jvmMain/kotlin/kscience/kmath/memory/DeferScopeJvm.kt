package kscience.kmath.memory

private typealias Deferred = () -> Unit

public actual class DeferScope {
    @PublishedApi
    internal val deferred: MutableList<Deferred> = mutableListOf()

    @PublishedApi
    internal fun executeAllDeferred() {
        deferred.forEach(Deferred::invoke)
        deferred.clear()
    }

    public actual inline fun defer(crossinline block: () -> Unit) {
        deferred += {
            try {
                block()
            } catch (ignored: Throwable) {
            }
        }
    }
}

public actual inline fun <R> withDeferScope(block: DeferScope.() -> R): R {
    val ds = DeferScope()
    val r = ds.block()
    ds.executeAllDeferred()
    return r
}
