/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.memory.foreign

import jdk.incubator.foreign.MemoryAccess
import jdk.incubator.foreign.MemorySegment
import space.kscience.kmath.memory.MemoryReader

internal class ForeignReader(override val memory: ForeignMemory) : MemoryReader {
    private val scope: MemorySegment
        get() = memory.scope

    override fun readDouble(offset: Int): Double = MemoryAccess.getDoubleAtOffset(scope, offset.toLong())
    override fun readFloat(offset: Int): Float = MemoryAccess.getFloatAtOffset(scope, offset.toLong())
    override fun readByte(offset: Int): Byte = MemoryAccess.getByteAtOffset(scope, offset.toLong())
    override fun readShort(offset: Int): Short = MemoryAccess.getShortAtOffset(scope, offset.toLong())
    override fun readInt(offset: Int): Int = MemoryAccess.getIntAtOffset(scope, offset.toLong())
    override fun readLong(offset: Int): Long = MemoryAccess.getLongAtOffset(scope, offset.toLong())
    override fun release(): Unit = Unit
}
