/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.memory.foreign

import jdk.incubator.foreign.MemorySegment
import space.kscience.kmath.memory.Memory
import space.kscience.kmath.memory.MemoryReader
import space.kscience.kmath.memory.MemoryWriter
import java.lang.ref.Cleaner

/**
 * Allocates memory using JDK Foreign Memory API. It should be even faster than default ByteBuffer memory provided by
 * [space.kscience.kmath.memory.allocate].
 */
public fun Memory.Companion.allocateAsForeign(length: Int): Memory =
    ForeignMemory(MemorySegment.allocateNative(length.toLong()))

/**
 * Wraps a [Memory] around existing [ByteArray]. This operation is unsafe since the array is not copied
 * and could be mutated independently from the resulting [Memory].
 *
 * The memory is wrapped to JDK Foreign Memory segment.
 */
public fun Memory.Companion.wrapAsForeign(array: ByteArray): Memory = ForeignMemory(MemorySegment.ofArray(array))

private val cleaner: Cleaner by lazy { Cleaner.create() }

private fun cleaningRunnable(scope: MemorySegment): Runnable = Runnable { scope.close() }

internal class ForeignMemory(val scope: MemorySegment) : Memory, AutoCloseable {
    private val cleanable: Cleaner.Cleanable = cleaner.register(this, cleaningRunnable(scope))

    override val size: Int
        get() = Math.toIntExact(scope.byteSize())

    private val writer: MemoryWriter = ForeignWriter(this)
    private val reader: MemoryReader = ForeignReader(this)

    override fun view(offset: Int, length: Int): ForeignMemory =
        ForeignMemory(scope.asSlice(offset.toLong(), length.toLong()))

    override fun copy(): Memory {
        val newScope = MemorySegment.allocateNative(scope.byteSize())
        newScope.copyFrom(scope)
        return ForeignMemory(newScope)
    }

    override fun reader(): MemoryReader = reader
    override fun writer(): MemoryWriter = writer
    override fun close(): Unit = cleanable.clean()
}
