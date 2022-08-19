/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JsModule("astring")
@file:JsNonModule

package space.kscience.kmath.internal.astring

import space.kscience.kmath.internal.estree.BaseNode

internal external interface Options {
    var indent: String?
        get() = definedExternally
        set(value) = definedExternally
    var lineEnd: String?
        get() = definedExternally
        set(value) = definedExternally
    var startingIndentLevel: Number?
        get() = definedExternally
        set(value) = definedExternally
    var comments: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var generator: Any?
        get() = definedExternally
        set(value) = definedExternally
    var sourceMap: Any?
        get() = definedExternally
        set(value) = definedExternally
}

internal external fun generate(node: BaseNode, options: Options /* Options & `T$0` */ = definedExternally): String

internal external fun generate(node: BaseNode): String

internal external var baseGenerator: Generator
