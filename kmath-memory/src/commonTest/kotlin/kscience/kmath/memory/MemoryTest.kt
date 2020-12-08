package kscience.kmath.memory

import kotlin.test.Test
import kotlin.test.assertEquals

internal class MemoryTest {
    @Test
    fun allocateReturnsMemoryWithValidSize() {
        val mem = Memory.allocate(64)
        assertEquals(64, mem.size)
    }

    @Test
    fun rwByte() {
        val mem = Memory.allocate(64)
        mem.write { writeByte(0, 42.toByte()) }
        assertEquals(42.toByte(), mem.read { readByte(0) })
        mem.write { writeByte(1, (-4).toByte()) }
        assertEquals((-4).toByte(), mem.read { readByte(1) })
    }

    @Test
    fun rwShort() {
        val mem = Memory.allocate(64)
        mem.write { writeShort(0, 44555.toShort()) }
        assertEquals(44555.toShort(), mem.read { readShort(0) })
        mem.write { writeShort(2, (-33333).toShort()) }
        assertEquals((-33333).toShort(), mem.read { readShort(2) })
    }

    @Test
    fun rwInt() {
        val mem = Memory.allocate(64)
        mem.write { writeInt(0, 1234444444) }
        assertEquals(1234444444, mem.read { readInt(0) })
        mem.write { writeInt(4, -5595959) }
        assertEquals(-5595959, mem.read { readInt(4) })
    }

    @Test
    fun rwLong() {
        val mem = Memory.allocate(64)
        mem.write { writeLong(0, 1234444444L) }
        assertEquals(1234444444L, mem.read { readLong(0) })
        mem.write { writeLong(4, -5595959L) }
        assertEquals(-5595959L, mem.read { readLong(4) })
        mem.write { writeLong(8, 1234444444444149L) }
        assertEquals(1234444444444149L, mem.read { readLong(8) })
        mem.write { writeLong(16, -50000333595959L) }
        assertEquals(-50000333595959L, mem.read { readLong(16) })
    }

    @Suppress("DEPRECATION")
    @Test
    fun rwFloat() {
        val mem = Memory.allocate(64)
        mem.write { writeFloat(0, 12.12345f) }
        println(1)
        assertEquals(12.12345027923584, mem.read { readFloat(0) }.toDouble())
        mem.write { writeFloat(4, -313.13f) }
        println(2)
        assertEquals(-313.1300048828125, mem.read { readFloat(4) }.toDouble())
        mem.write { writeFloat(8, Float.NaN) }
        println(3)
        assertEquals(Float.NaN, mem.read { readFloat(8) })
        mem.write { writeFloat(12, Float.POSITIVE_INFINITY) }
        println(4)
        assertEquals(Float.POSITIVE_INFINITY, mem.read { readFloat(12) })
        mem.write { writeFloat(12, Float.NEGATIVE_INFINITY) }
        println(5)
        assertEquals(Float.NEGATIVE_INFINITY, mem.read { readFloat(12) })
    }

    @Test
    fun rwDouble() {
        val mem = Memory.allocate(64)
        mem.write { writeDouble(0, 12.12345) }
        assertEquals(12.12345, mem.read { readDouble(0) })
        mem.write { writeDouble(8, -313.13) }
        assertEquals(-313.13, mem.read { readDouble(8) })
        mem.write { writeDouble(16, Double.NaN) }
        assertEquals(Double.NaN, mem.read { readDouble(16) })
        mem.write { writeDouble(24, Double.POSITIVE_INFINITY) }
        assertEquals(Double.POSITIVE_INFINITY, mem.read { readDouble(24) })
        mem.write { writeDouble(32, Double.NEGATIVE_INFINITY) }
        assertEquals(Double.NEGATIVE_INFINITY, mem.read { readDouble(32) })
    }
}
