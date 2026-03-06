/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

/**
 * A simple LRU cache implementation.
 *
 * The implementation is not thread-safe.
 */
public class LruCache<K, V>(public val maxSize: Int) {
    private val map = HashMap<K, V>(maxSize)
    private val keys = ArrayList<K>(maxSize)

    public operator fun get(key: K): V? {
        val value = map[key]
        if (value != null) {
            keys.remove(key)
            keys.add(key)
        }
        return value
    }

    public operator fun set(key: K, value: V) {
        if (map.containsKey(key)) {
            keys.remove(key)
        } else if (map.size >= maxSize) {
            val oldestKey = keys.removeAt(0)
            map.remove(oldestKey)
        }
        map[key] = value
        keys.add(key)
    }

    public fun remove(key: K): V? {
        keys.remove(key)
        return map.remove(key)
    }

    public fun clear() {
        map.clear()
        keys.clear()
    }

    public val size: Int get() = map.size
}

public inline fun <K, V> LruCache<K, V>.getOrPut(key: K, compute: () -> V): V = get(key) ?: compute().also { set(key, it) }