package scientifik.memory.foreign

import jdk.incubator.foreign.MemoryHandles
import jdk.incubator.foreign.MemorySegment
import scientifik.memory.Memory
import scientifik.memory.MemoryReader
import scientifik.memory.MemoryWriter
import java.lang.invoke.VarHandle
import java.nio.ByteOrder

fun Memory.Companion.allocateForeign(length: Int): Memory {
    return ForeignMemory(MemorySegment.allocateNative(length.toLong()))
}

internal class ForeignMemory(val scope: MemorySegment) : Memory, AutoCloseable {
    override val size: Int
        get() = scope.byteSize().toInt()

    private val writer: MemoryWriter = ForeignWriter(this)
    private val reader: MemoryReader = ForeignReader(this)

    override fun view(offset: Int, length: Int): ForeignMemory =
        ForeignMemory(scope.asSlice(offset.toLong(), length.toLong()))

    override fun copy(): Memory {
        val bytes = scope.toByteArray()
        val newScope = MemorySegment.allocateNative(scope.byteSize())!!

        var point = newScope.baseAddress()

        bytes.forEach {
            byteHandle.set(point, it)
            point = point.addOffset(1)
        }

        return ForeignMemory(newScope)
    }

    override fun reader(): MemoryReader = reader
    override fun writer(): MemoryWriter = writer
    override fun close(): Unit = scope.close()

    internal companion object {
        internal val doubleHandle: VarHandle = MemoryHandles.varHandle(java.lang.Double.TYPE, ByteOrder.nativeOrder())!!
        internal val floatHandle: VarHandle = MemoryHandles.varHandle(java.lang.Float.TYPE, ByteOrder.nativeOrder())!!
        internal val byteHandle: VarHandle = MemoryHandles.varHandle(java.lang.Byte.TYPE, ByteOrder.nativeOrder())!!
        internal val shortHandle: VarHandle = MemoryHandles.varHandle(java.lang.Short.TYPE, ByteOrder.nativeOrder())!!
        internal val intHandle: VarHandle = MemoryHandles.varHandle(Integer.TYPE, ByteOrder.nativeOrder())!!
        internal val longHandle: VarHandle = MemoryHandles.varHandle(java.lang.Long.TYPE, ByteOrder.nativeOrder())!!
    }
}
