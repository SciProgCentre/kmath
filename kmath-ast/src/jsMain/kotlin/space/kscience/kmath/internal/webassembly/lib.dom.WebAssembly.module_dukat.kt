/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JsQualifier("WebAssembly")

@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING",
    "ClassName",
)

package space.kscience.kmath.internal.webassembly

import space.kscience.kmath.internal.tsstdlib.PromiseLike
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array
import org.w3c.fetch.Response
import kotlin.js.Promise

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
internal external interface CompileError {
    companion object {
        var prototype: CompileError
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
internal external interface Global {
    var value: Any
    fun valueOf(): Any

    companion object {
        var prototype: Global
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@JsName("Instance")
internal external interface Instance1 {
    var exports: Exports

    companion object {
        var prototype: Instance
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
internal external interface LinkError {
    companion object {
        var prototype: LinkError
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
internal external interface Memory {
    var buffer: ArrayBuffer
    fun grow(delta: Number): Number

    companion object {
        var prototype: Memory
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@JsName("Module")
internal external interface Module1 {
    companion object {
        var prototype: Module
        fun customSections(moduleObject: Module, sectionName: String): Array<ArrayBuffer>
        fun exports(moduleObject: Module): Array<ModuleExportDescriptor>
        fun imports(moduleObject: Module): Array<ModuleImportDescriptor>
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
internal external interface RuntimeError {
    companion object {
        var prototype: RuntimeError
    }
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
internal external interface Table {
    var length: Number
    fun get(index: Number): Function<*>?
    fun grow(delta: Number): Number
    fun set(index: Number, value: Function<*>?)

    companion object {
        var prototype: Table
    }
}

internal external interface GlobalDescriptor {
    var mutable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var value: String /* "f32" | "f64" | "i32" | "i64" */
}

internal external interface MemoryDescriptor {
    var initial: Number
    var maximum: Number?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface ModuleExportDescriptor {
    var kind: String /* "function" | "global" | "memory" | "table" */
    var name: String
}

internal external interface ModuleImportDescriptor {
    var kind: String /* "function" | "global" | "memory" | "table" */
    var module: String
    var name: String
}

internal external interface TableDescriptor {
    var element: String /* "anyfunc" */
    var initial: Number
    var maximum: Number?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface WebAssemblyInstantiatedSource {
    var instance: Instance
    var module: Module
}

internal external fun compile(bytes: ArrayBufferView): Promise<Module>

internal external fun compile(bytes: ArrayBuffer): Promise<Module>

internal external fun compileStreaming(source: Response): Promise<Module>

internal external fun compileStreaming(source: Promise<Response>): Promise<Module>

internal external fun instantiate(
    bytes: ArrayBufferView,
    importObject: Imports = definedExternally,
): Promise<WebAssemblyInstantiatedSource>

internal external fun instantiate(bytes: ArrayBufferView): Promise<WebAssemblyInstantiatedSource>

internal external fun instantiate(
    bytes: ArrayBuffer,
    importObject: Imports = definedExternally,
): dynamic /* Promise | Promise */

internal external fun instantiate(bytes: ArrayBuffer): dynamic /* Promise | Promise */

internal external fun instantiate(moduleObject: Module, importObject: Imports = definedExternally): Promise<Instance>

internal external fun instantiate(moduleObject: Module): Promise<Instance>

internal external fun instantiateStreaming(
    response: Response,
    importObject: Imports = definedExternally,
): Promise<WebAssemblyInstantiatedSource>

internal external fun instantiateStreaming(response: Response): Promise<WebAssemblyInstantiatedSource>

internal external fun instantiateStreaming(
    response: PromiseLike<Response>,
    importObject: Imports = definedExternally,
): Promise<WebAssemblyInstantiatedSource>

internal external fun instantiateStreaming(response: PromiseLike<Response>): Promise<WebAssemblyInstantiatedSource>

internal external fun validate(bytes: ArrayBufferView): Boolean

internal external fun validate(bytes: ArrayBuffer): Boolean

internal external interface `T$0` {
    var name: String
    var kind: String
}

internal external interface `T$1` {
    var module: String
    var name: String
    var kind: String
}

internal open external class Module {
    constructor(bufferSource: ArrayBuffer)
    constructor(bufferSource: Uint8Array)

    companion object {
        fun customSections(module: Module, sectionName: String): Array<ArrayBuffer>
        fun exports(module: Module): Array<`T$0`>
        fun imports(module: Module): Array<`T$1`>
    }
}

@JsName("Instance")
internal open external class Instance(module: Module, importObject: dynamic = definedExternally) {
    open var exports: dynamic
}

@JsName("Memory")
internal open external class Memory1(memoryDescriptor: MemoryDescriptor) {
    open var buffer: ArrayBuffer
    open fun grow(numPages: Number): Number
}

@JsName("Table")
internal open external class Table1(tableDescriptor: TableDescriptor) {
    open var length: Number
    open fun get(index: Number): Function<*>
    open fun grow(numElements: Number): Number
    open fun set(index: Number, value: Function<*>)
}

internal external fun compile(bufferSource: Uint8Array): Promise<Module>

internal external interface ResultObject {
    var module: Module
    var instance: Instance
}

internal external fun instantiate(
    bufferSource: Uint8Array,
    importObject: Any = definedExternally,
): Promise<ResultObject>

internal external fun instantiate(bufferSource: Uint8Array): Promise<ResultObject>

internal external fun validate(bufferSource: Uint8Array): Boolean
