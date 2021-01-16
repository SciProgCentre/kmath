package kscience.kmath.memory

import kotlinx.cinterop.memScoped

public actual typealias DeferScope = kotlinx.cinterop.DeferScope

public actual inline fun <R> withDeferScope(block: DeferScope.() -> R): R = memScoped(block)
