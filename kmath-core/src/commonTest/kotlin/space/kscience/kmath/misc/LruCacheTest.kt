/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LruCacheTest {

    @Test
    fun testLruCache() {
        val cache = LruCache<Int, String>(2)
        cache[1] = "one"
        cache[2] = "two"
        assertEquals("one", cache[1])
        cache[3] = "three"
        assertNull(cache[2])
        assertEquals("one", cache[1])
        assertEquals("three", cache[3])
        
        cache[4] = "four"
        assertNull(cache[1])
        assertEquals("three", cache[3])
        assertEquals("four", cache[4])
    }

    @Test
    fun testUpdateExisting() {
        val cache = LruCache<Int, String>(2)
        cache[1] = "one"
        cache[2] = "two"
        cache[1] = "one-updated"
        cache[3] = "three"
        assertEquals("one-updated", cache[1])
        assertNull(cache[2])
    }

    @Test
    fun testRemove() {
        val cache = LruCache<Int, String>(2)
        cache[1] = "one"
        cache[2] = "two"
        assertEquals("one", cache.remove(1))
        assertEquals(1, cache.size)
        cache[3] = "three"
        cache[4] = "four"
        assertNull(cache[2])
        assertEquals("three", cache[3])
        assertEquals("four", cache[4])
    }
    
    @Test
    fun testClear() {
        val cache = LruCache<Int, String>(2)
        cache[1] = "one"
        cache[2] = "two"
        cache.clear()
        assertEquals(0, cache.size)
        assertNull(cache[1])
        assertNull(cache[2])
    }
}
