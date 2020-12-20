@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "SortModifiers",
    "KDocMissingDocumentation", "PackageDirectoryMismatch"
)

package stream

import emitter.Emitter

external open class Stream : Emitter {
    open fun pipe(dest: Any, options: Any): Any
}