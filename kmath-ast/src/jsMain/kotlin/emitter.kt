@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS", "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "KDocMissingDocumentation", "SortModifiers",
    "PackageDirectoryMismatch"
)

package emitter

external open class Emitter {
    constructor(obj: Any)
    constructor()

    open fun on(event: String, fn: () -> Unit)
    open fun off(event: String, fn: () -> Unit)
    open fun once(event: String, fn: () -> Unit)
    open fun emit(event: String, vararg any: Any)
    open fun listeners(event: String): Array<() -> Unit>
    open fun hasListeners(event: String): Boolean
}