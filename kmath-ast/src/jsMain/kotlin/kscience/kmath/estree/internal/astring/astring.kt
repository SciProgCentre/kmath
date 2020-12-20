@file:JsModule("astring")
@file:JsNonModule

package kscience.kmath.estree.internal.astring

import kscience.kmath.estree.internal.estree.BaseNode

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
