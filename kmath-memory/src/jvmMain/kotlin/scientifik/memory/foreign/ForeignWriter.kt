package scientifik.memory.foreign

import jdk.incubator.foreign.MemorySegment
import scientifik.memory.MemoryWriter

internal class ForeignWriter(override val memory: ForeignMemory) : MemoryWriter {
    private val scope: MemorySegment
        get() = memory.scope

    override fun writeDouble(offset: Int, value: Double): Unit = with(scope) {
        ForeignMemory.doubleHandle.set(baseAddress().addOffset(offset.toLong()), value)
    }

    override fun writeFloat(offset: Int, value: Float): Unit = with(scope) {
        ForeignMemory.floatHandle.set(baseAddress().addOffset(offset.toLong()), value)
    }

    override fun writeByte(offset: Int, value: Byte): Unit = with(scope) {
        ForeignMemory.byteHandle.set(baseAddress().addOffset(offset.toLong()), value)
    }

    override fun writeShort(offset: Int, value: Short): Unit = with(scope) {
        ForeignMemory.shortHandle.set(baseAddress().addOffset(offset.toLong()), value)
    }

    override fun writeInt(offset: Int, value: Int): Unit = with(scope) {
        ForeignMemory.intHandle.set(baseAddress().addOffset(offset.toLong()), value)
    }

    override fun writeLong(offset: Int, value: Long): Unit = with(scope) {
        ForeignMemory.longHandle.set(baseAddress().addOffset(offset.toLong()), value)
    }

    override fun release(): Unit = memory.close()
}
