@file:JvmName("MapIntrinsics")

package kscience.kmath.asm.internal

/**
 * Gets value with given [key] or throws [IllegalStateException] whenever it is not present.
 *
 * @author Iaroslav Postovalov
 */
@JvmOverloads
internal fun <K, V> Map<K, V>.getOrFail(key: K, default: V? = null): V =
    this[key] ?: default ?: error("Parameter not found: $key")
