@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package tsstdlib

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface IteratorYieldResult<TYield> {
    var done: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var value: TYield
}

external interface IteratorReturnResult<TReturn> {
    var done: Boolean
    var value: TReturn
}

external interface Iterator<T, TReturn, TNext> {
    fun next(vararg args: Any /* JsTuple<> | JsTuple<TNext> */): dynamic /* IteratorYieldResult<T> | IteratorReturnResult<TReturn> */
    val `return`: ((value: TReturn) -> dynamic)?
    val `throw`: ((e: Any) -> dynamic)?
}

typealias Iterator__1<T> = Iterator<T, Any, Nothing?>

external interface Iterable<T>

external interface IterableIterator<T> : Iterator__1<T>