package scientifik.memory.foreign

import jdk.incubator.foreign.MemorySegment
import scientifik.memory.MemoryReader

internal class ForeignReader(override val memory: ForeignMemory) : MemoryReader {
    private val scope: MemorySegment
        get() = memory.scope.asReadOnly()

    override fun readDouble(offset: Int): Double = with(scope) {
        ForeignMemory.doubleHandle.get(baseAddress().addOffset(offset.toLong())) as Double
    }

    override fun readFloat(offset: Int): Float = with(scope) {
        ForeignMemory.floatHandle.get(baseAddress().addOffset(offset.toLong())) as Float
    }

    override fun readByte(offset: Int): Byte = with(scope) {
        ForeignMemory.byteHandle.get(baseAddress().addOffset(offset.toLong())) as Byte
    }

    override fun readShort(offset: Int): Short = with(scope) {
        ForeignMemory.shortHandle.get(baseAddress().addOffset(offset.toLong())) as Short
    }

    override fun readInt(offset: Int): Int = with(scope) {
        ForeignMemory.intHandle.get(baseAddress().addOffset(offset.toLong())) as Int
    }

    override fun readLong(offset: Int): Long = with(scope) {
        ForeignMemory.longHandle.get(baseAddress().addOffset(offset.toLong())) as Long
    }

    override fun release(): Unit = memory.close()
}
