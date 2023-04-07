/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.memory

import kotlin.wasm.unsafe.Pointer
import kotlin.wasm.unsafe.UnsafeWasmMemoryApi

@OptIn(UnsafeWasmMemoryApi::class)
public class WasmMemory private constructor(
    public val pointer: Pointer,
    override val size: Int,
) : Memory {

    override fun view(offset: Int, length: Int): Memory {
        TODO("Not yet implemented")
    }

    override fun copy(): Memory {
        TODO("Not yet implemented")
    }

    override fun reader(): MemoryReader = object : MemoryReader {
        override val memory: Memory
            get() = this@WasmMemory

        override fun readDouble(offset: Int): Double {
            return Double.fromBits(pointer.plus(offset).loadLong())
        }

        override fun readFloat(offset: Int): Float {
            return Float.fromBits(pointer.plus(offset).loadInt())
        }

        override fun readByte(offset: Int): Byte {
            return pointer.plus(offset).loadByte()
        }

        override fun readShort(offset: Int): Short {
            return pointer.plus(offset).loadShort()
        }

        override fun readInt(offset: Int): Int {
            return pointer.plus(offset).loadInt()
        }

        override fun readLong(offset: Int): Long {
            return pointer.plus(offset).loadLong()
        }

        override fun close() {
            TODO()
        }

    }

    override fun writer(): MemoryWriter = TODO()
}

public actual fun Memory.Companion.allocate(length: Int): Memory {
    TODO()
}

public actual fun Memory.Companion.wrap(array: ByteArray): Memory = TODO()