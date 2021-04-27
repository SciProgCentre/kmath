/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.memory

import space.kscience.kmath.memory.foreign.allocateAsForeign
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ForeignMemoryTest {
    private fun getMemory(int: Int) = Memory.allocateAsForeign(int)

    @Test
    fun size() {
        val mem = getMemory(66666)
        assertEquals(66666, mem.size)
    }

    @Test
    fun view() {
        val mem = getMemory(4242)
        val sub = mem.view(10, 10)
        sub.write { writeInt(0, 1000000) }
        assertEquals(10, sub.size)
        assertEquals(1000000, mem.read { readInt(10) })
        assertEquals(1000000, sub.read { readInt(0) })
    }

    @Test
    fun copy() {
        val mem = getMemory(8)
        mem.write { writeDouble(0, 12.0) }
        val copy = mem.copy()
        assertEquals(12.0, copy.read { readDouble(0) })
    }

    @Test
    fun reader() {
        val mem = getMemory(8)
        val rd = mem.reader()
        assertEquals(0, rd.readLong(0))
        rd.release()
    }

    @Test
    fun writer() {
        val mem = getMemory(4)
        val wr = mem.writer()
        wr.writeFloat(0, 6f)
        val rd = mem.reader()
        assertEquals(6f, rd.readFloat(0))
        rd.release()
        wr.release()
    }
}
