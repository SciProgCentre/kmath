@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS",
    "PackageDirectoryMismatch", "DEPRECATION", "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "KDocMissingDocumentation",
    "UNUSED_TYPEALIAS_PARAMETER", "PropertyName"
)

package tsstdlib

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

external interface FunctionConstructor {
    @nativeInvoke
    operator fun invoke(vararg args: String): Function<*>
    var prototype: Function<*>
}

external interface ErrorConstructor {
    @nativeInvoke
    operator fun invoke(message: String = definedExternally): Error
    var prototype: Error
}

external interface PromiseLike<T> {
    fun then(
        onfulfilled: ((value: T) -> Any?)? = definedExternally,
        onrejected: ((reason: Any) -> Any?)? = definedExternally
    ): PromiseLike<dynamic /* TResult1 | TResult2 */>
}

external interface ArrayLike<T> {
    var length: Number

    @nativeGetter
    operator fun get(n: Number): T?

    @nativeSetter
    operator fun set(n: Number, value: T)
}

typealias Record<K, T> = Any

external interface ArrayBufferTypes {
    var ArrayBuffer: ArrayBuffer
}

external interface ArrayBufferConstructor {
    var prototype: ArrayBuffer
    fun isView(arg: Any): Boolean
}

external interface Uint8ArrayConstructor {
    fun from(
        arrayLike: Iterable<Number>,
        mapfn: (v: Number, k: Number) -> Number = definedExternally,
        thisArg: Any = definedExternally
    ): Uint8Array

    fun from(arrayLike: Iterable<Number>): Uint8Array
    fun from(arrayLike: Iterable<Number>, mapfn: (v: Number, k: Number) -> Number = definedExternally): Uint8Array
    var prototype: Uint8Array
    var BYTES_PER_ELEMENT: Number
    fun of(vararg items: Number): Uint8Array
    fun from(arrayLike: ArrayLike<Number>): Uint8Array

    fun <T> from(
        arrayLike: ArrayLike<T>,
        mapfn: (v: T, k: Number) -> Number,
        thisArg: Any = definedExternally
    ): Uint8Array

    fun <T> from(arrayLike: ArrayLike<T>, mapfn: (v: T, k: Number) -> Number): Uint8Array
}
