@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "DEPRECATION", "PackageDirectoryMismatch", "KDocMissingDocumentation",
    "PropertyName"
)

package tsstdlib

import kotlin.js.RegExp

typealias RegExpMatchArray = Array<String>

typealias RegExpExecArray = Array<String>

external interface RegExpConstructor {
    @nativeInvoke
    operator fun invoke(pattern: RegExp, flags: String = definedExternally): RegExp

    @nativeInvoke
    operator fun invoke(pattern: RegExp): RegExp

    @nativeInvoke
    operator fun invoke(pattern: String, flags: String = definedExternally): RegExp

    @nativeInvoke
    operator fun invoke(pattern: String): RegExp
    var prototype: RegExp
    var `$1`: String
    var `$2`: String
    var `$3`: String
    var `$4`: String
    var `$5`: String
    var `$6`: String
    var `$7`: String
    var `$8`: String
    var `$9`: String
    var lastMatch: String
}

external interface ConcatArray<T> {
    var length: Number

    @nativeGetter
    operator fun get(n: Number): T?

    @nativeSetter
    operator fun set(n: Number, value: T)
    fun join(separator: String = definedExternally): String
    fun slice(start: Number = definedExternally, end: Number = definedExternally): Array<T>
}

external interface ArrayConstructor {
    fun <T> from(iterable: Iterable<T>): Array<T>
    fun <T> from(iterable: ArrayLike<T>): Array<T>
    fun <T, U> from(iterable: Iterable<T>, mapfn: (v: T, k: Number) -> U, thisArg: Any = definedExternally): Array<U>
    fun <T, U> from(iterable: Iterable<T>, mapfn: (v: T, k: Number) -> U): Array<U>
    fun <T, U> from(iterable: ArrayLike<T>, mapfn: (v: T, k: Number) -> U, thisArg: Any = definedExternally): Array<U>
    fun <T, U> from(iterable: ArrayLike<T>, mapfn: (v: T, k: Number) -> U): Array<U>
    fun <T> of(vararg items: T): Array<T>

    @nativeInvoke
    operator fun invoke(arrayLength: Number = definedExternally): Array<Any>

    @nativeInvoke
    operator fun invoke(): Array<Any>

    @nativeInvoke
    operator fun <T> invoke(arrayLength: Number): Array<T>

    @nativeInvoke
    operator fun <T> invoke(vararg items: T): Array<T>
    fun isArray(arg: Any): Boolean
    var prototype: Array<Any>
}

external interface ArrayLike<T> {
    var length: Number

    @nativeGetter
    operator fun get(n: Number): T?

    @nativeSetter
    operator fun set(n: Number, value: T)
}

typealias Extract<T, U> = Any