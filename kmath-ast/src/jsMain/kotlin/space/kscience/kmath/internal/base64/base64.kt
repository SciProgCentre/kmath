/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING",
    "ObjectPropertyName",
    "ClassName",
)
@file:JsNonModule
@file:JsModule("js-base64")

package space.kscience.kmath.internal.base64

import org.khronos.webgl.Uint8Array

internal external var version: Any

internal external var VERSION: Any

internal external var btoaPolyfill: (bin: String) -> String

internal external var _btoa: (bin: String) -> String

internal external var fromUint8Array: (u8a: Uint8Array, urlsafe: Boolean) -> String

internal external var utob: (u: String) -> String

internal external var encode: (src: String, urlsafe: Boolean) -> String

internal external var encodeURI: (src: String) -> String

internal external var btou: (b: String) -> String

internal external var atobPolyfill: (asc: String) -> String

internal external var _atob: (asc: String) -> String

internal external var toUint8Array: (a: String) -> Uint8Array

internal external var decode: (src: String) -> String

internal external var isValid: (src: Any) -> Boolean

internal external var extendString: () -> Unit

internal external var extendUint8Array: () -> Unit

internal external var extendBuiltins: () -> Unit
