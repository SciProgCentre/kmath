@file:JvmName("MapIntrinsics")

package scientifik.kmath.asm.internal

@JvmOverloads
internal fun <K, V> Map<K, V>.getOrFail(key: K, default: V? = null): V =
    this[key] ?: default ?: error("Parameter not found: $key")
