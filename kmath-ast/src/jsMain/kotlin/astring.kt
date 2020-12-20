@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "KDocMissingDocumentation", "PackageDirectoryMismatch"
)

@file:JsModule("astring")
@file:JsNonModule
package astring

import Generator
import estree.BaseNode

external interface Options {
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

external fun generate(node: BaseNode, options: Options /* Options & `T$0` */ = definedExternally): String

external fun generate(node: BaseNode): String

external var baseGenerator: Generator
