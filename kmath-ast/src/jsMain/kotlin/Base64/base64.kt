@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS", "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "KDocMissingDocumentation", "ClassName",
    "SpellCheckingInspection", "PackageName"
)
@file:JsModule("js-base64")
@file:JsNonModule
package Base64

import org.khronos.webgl.Uint8Array

external var version: Any

external var VERSION: Any

external var btoaPolyfill: (bin: String) -> String

external var _btoa: (bin: String) -> String

external var fromUint8Array: (u8a: Uint8Array, urlsafe: Boolean) -> String

external var utob: (u: String) -> String

external var encode: (src: String, urlsafe: Boolean) -> String

external var encodeURI: (src: String) -> String

external var btou: (b: String) -> String

external var atobPolyfill: (asc: String) -> String

external var _atob: (asc: String) -> String

external var toUint8Array: (a: String) -> Uint8Array

external var decode: (src: String) -> String

external var isValid: (src: Any) -> Boolean

external var extendString: () -> Unit

external var extendUint8Array: () -> Unit

external var extendBuiltins: () -> Unit

external object gBase64 {
    var version: String
    var VERSION: String
    var atob: (asc: String) -> String
    var atobPolyfill: (asc: String) -> String
    var btoa: (bin: String) -> String
    var btoaPolyfill: (bin: String) -> String
    var fromBase64: (src: String) -> String
    var toBase64: (src: String, urlsafe: Boolean) -> String
    var encode: (src: String, urlsafe: Boolean) -> String
    var encodeURI: (src: String) -> String
    var encodeURL: (src: String) -> String
    var utob: (u: String) -> String
    var btou: (b: String) -> String
    var decode: (src: String) -> String
    var isValid: (src: Any) -> Boolean
    var fromUint8Array: (u8a: Uint8Array, urlsafe: Boolean) -> String
    var toUint8Array: (a: String) -> Uint8Array
    var extendString: () -> Unit
    var extendUint8Array: () -> Unit
    var extendBuiltins: () -> Unit
}