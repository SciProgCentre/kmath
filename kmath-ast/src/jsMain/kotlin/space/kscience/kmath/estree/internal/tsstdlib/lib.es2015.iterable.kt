package space.kscience.kmath.estree.internal.tsstdlib

internal external interface IteratorYieldResult<TYield> {
    var done: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var value: TYield
}

internal external interface IteratorReturnResult<TReturn> {
    var done: Boolean
    var value: TReturn
}

internal external interface Iterator<T, TReturn, TNext> {
    fun next(vararg args: Any /* JsTuple<> | JsTuple<TNext> */): dynamic /* IteratorYieldResult<T> | IteratorReturnResult<TReturn> */
    val `return`: ((value: TReturn) -> dynamic)?
    val `throw`: ((e: Any) -> dynamic)?
}

internal typealias Iterator__1<T> = Iterator<T, Any, Nothing?>

internal external interface Iterable<T>

internal external interface IterableIterator<T> : Iterator__1<T>
