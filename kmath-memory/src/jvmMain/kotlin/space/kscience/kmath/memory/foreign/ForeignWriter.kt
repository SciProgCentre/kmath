/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.memory.foreign

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import space.kscience.kmath.memory.MemoryWriter

internal class ForeignWriter(override val memory: ForeignMemory) : MemoryWriter {
    private val scope: MemorySegment
        get() = memory.scope

    override fun writeDouble(offset: Int, value: Double): Unit =
        MemoryAccess.setDoubleAtOffset(scope, offset.toLong(), value)

    override fun writeFloat(offset: Int, value: Float): Unit =
        MemoryAccess.setFloatAtOffset(scope, offset.toLong(), value)

    override fun writeByte(offset: Int, value: Byte): Unit = MemoryAccess.setByteAtOffset(scope, offset.toLong(), value)

    override fun writeShort(offset: Int, value: Short): Unit =
        MemoryAccess.setShortAtOffset(scope, offset.toLong(), value)

    override fun writeInt(offset: Int, value: Int): Unit = MemoryAccess.setIntAtOffset(scope, offset.toLong(), value)
    override fun writeLong(offset: Int, value: Long): Unit = MemoryAccess.setLongAtOffset(scope, offset.toLong(), value)
    override fun release(): Unit = Unit
}
