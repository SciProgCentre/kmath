@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS",
    "UnusedImport", "PackageDirectoryMismatch", "PackageName", "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING",
    "KDocMissingDocumentation"
)
package WebAssembly

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import tsstdlib.Record

typealias Exports = Record<String, dynamic /* Function<*> | Global | Memory | Table */>

typealias ModuleImports = Record<String, dynamic /* Function<*> | Global | Memory | Table | Number */>

typealias Imports = Record<String, ModuleImports>

typealias CompileError1 = Error

typealias LinkError1 = Error

typealias RuntimeError1 = Error
