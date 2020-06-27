@file:JvmName("MapIntrinsics")

package scientifik.kmath.asm.internal

internal fun <K, V> Map<K, V>.getOrFail(key: K, default: V?): V {
    return this[key] ?: default ?: error("Parameter not found: $key")
}
