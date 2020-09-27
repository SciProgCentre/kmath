package kscience.kmath.chains

import kotlinx.coroutines.runBlocking

/**
 * Represent a chain as regular iterator (uses blocking calls)
 */
public operator fun <R> Chain<R>.iterator(): Iterator<R> = object : Iterator<R> {
    override fun hasNext(): Boolean = true
    override fun next(): R = runBlocking { next() }
}

/**
 * Represent a chain as a sequence
 */
public fun <R> Chain<R>.asSequence(): Sequence<R> = Sequence { this@asSequence.iterator() }
