package space.kscience.kmath.estree.internal.emitter

internal open external class Emitter {
    constructor(obj: Any)
    constructor()

    open fun on(event: String, fn: () -> Unit)
    open fun off(event: String, fn: () -> Unit)
    open fun once(event: String, fn: () -> Unit)
    open fun emit(event: String, vararg any: Any)
    open fun listeners(event: String): Array<() -> Unit>
    open fun hasListeners(event: String): Boolean
}
