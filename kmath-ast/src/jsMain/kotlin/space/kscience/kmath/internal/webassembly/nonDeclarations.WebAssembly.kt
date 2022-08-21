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
)

package space.kscience.kmath.internal.webassembly

import space.kscience.kmath.internal.tsstdlib.Record

internal typealias Exports = Record<String, dynamic /* Function<*> | Global | Memory | Table */>

internal typealias ModuleImports = Record<String, dynamic /* Function<*> | Global | Memory | Table | Number */>

internal typealias Imports = Record<String, ModuleImports>

internal typealias CompileError1 = Error

internal typealias LinkError1 = Error

internal typealias RuntimeError1 = Error
