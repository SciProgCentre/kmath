@file:JsQualifier("WebAssembly")

@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING",
    "KDocMissingDocumentation",
    "PackageDirectoryMismatch",
    "PackageName",
    "ClassName",
)

package WebAssembly

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array
import org.w3c.fetch.Response
import tsstdlib.PromiseLike
import kotlin.js.Promise

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface CompileError {
    companion object {
        var prototype: CompileError
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface Global {
    var value: Any
    fun valueOf(): Any

    companion object {
        var prototype: Global
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@JsName("Instance")
external interface Instance1 {
    var exports: Exports

    companion object {
        var prototype: Instance
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface LinkError {
    companion object {
        var prototype: LinkError
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface Memory {
    var buffer: ArrayBuffer
    fun grow(delta: Number): Number

    companion object {
        var prototype: Memory
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@JsName("Module")
external interface Module1 {
    companion object {
        var prototype: Module
        fun customSections(moduleObject: Module, sectionName: String): Array<ArrayBuffer>
        fun exports(moduleObject: Module): Array<ModuleExportDescriptor>
        fun imports(moduleObject: Module): Array<ModuleImportDescriptor>
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface RuntimeError {
    companion object {
        var prototype: RuntimeError
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface Table {
    var length: Number
    fun get(index: Number): Function<*>?
    fun grow(delta: Number): Number
    fun set(index: Number, value: Function<*>?)

    companion object {
        var prototype: Table
    }
}

external interface GlobalDescriptor {
    var mutable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var value: String /* "f32" | "f64" | "i32" | "i64" */
}

external interface MemoryDescriptor {
    var initial: Number
    var maximum: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ModuleExportDescriptor {
    var kind: String /* "function" | "global" | "memory" | "table" */
    var name: String
}

external interface ModuleImportDescriptor {
    var kind: String /* "function" | "global" | "memory" | "table" */
    var module: String
    var name: String
}

external interface TableDescriptor {
    var element: String /* "anyfunc" */
    var initial: Number
    var maximum: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WebAssemblyInstantiatedSource {
    var instance: Instance
    var module: Module
}

external fun compile(bytes: ArrayBufferView): Promise<Module>

external fun compile(bytes: ArrayBuffer): Promise<Module>

external fun compileStreaming(source: Response): Promise<Module>

external fun compileStreaming(source: Promise<Response>): Promise<Module>

external fun instantiate(
    bytes: ArrayBufferView,
    importObject: Imports = definedExternally
): Promise<WebAssemblyInstantiatedSource>

external fun instantiate(bytes: ArrayBufferView): Promise<WebAssemblyInstantiatedSource>

external fun instantiate(bytes: ArrayBuffer, importObject: Imports = definedExternally): dynamic /* Promise | Promise */

external fun instantiate(bytes: ArrayBuffer): dynamic /* Promise | Promise */

external fun instantiate(moduleObject: Module, importObject: Imports = definedExternally): Promise<Instance>

external fun instantiate(moduleObject: Module): Promise<Instance>

external fun instantiateStreaming(
    response: Response,
    importObject: Imports = definedExternally
): Promise<WebAssemblyInstantiatedSource>

external fun instantiateStreaming(response: Response): Promise<WebAssemblyInstantiatedSource>

external fun instantiateStreaming(
    response: PromiseLike<Response>,
    importObject: Imports = definedExternally
): Promise<WebAssemblyInstantiatedSource>

external fun instantiateStreaming(response: PromiseLike<Response>): Promise<WebAssemblyInstantiatedSource>

external fun validate(bytes: ArrayBufferView): Boolean

external fun validate(bytes: ArrayBuffer): Boolean

external interface `T$0` {
    var name: String
    var kind: String
}

external interface `T$1` {
    var module: String
    var name: String
    var kind: String
}

open external class Module {
    constructor(bufferSource: ArrayBuffer)
    constructor(bufferSource: Uint8Array)

    companion object {
        fun customSections(module: Module, sectionName: String): Array<ArrayBuffer>
        fun exports(module: Module): Array<`T$0`>
        fun imports(module: Module): Array<`T$1`>
    }
}

@JsName("Instance")
open external class Instance(module: Module, importObject: Any = definedExternally) {
    open var exports: Any
}

@JsName("Memory")
open external class Memory1(memoryDescriptor: MemoryDescriptor) {
    open var buffer: ArrayBuffer
    open fun grow(numPages: Number): Number
}

@JsName("Table")
open external class Table1(tableDescriptor: TableDescriptor) {
    open var length: Number
    open fun get(index: Number): Function<*>
    open fun grow(numElements: Number): Number
    open fun set(index: Number, value: Function<*>)
}

external fun compile(bufferSource: Uint8Array): Promise<Module>

external interface ResultObject {
    var module: Module
    var instance: Instance
}

external fun instantiate(bufferSource: Uint8Array, importObject: Any = definedExternally): Promise<ResultObject>

external fun instantiate(bufferSource: Uint8Array): Promise<ResultObject>

external fun validate(bufferSource: Uint8Array): Boolean
