/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.memory

import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryTest {
    @Test
    fun memoryWriteRead() {
        val memorySize = 60
        val data = buildList {
            for (i in 0 until (memorySize / 4)) {
                add(i)
            }
        }
        val memory = Memory.allocate(memorySize)
        memory.write {
            for (i in 0 until (memory.size / 4)) {
                writeInt(i*4, data[i])
            }
        }

        val result = memory.read {
            buildList {
                for (i in 0 until (memory.size / 4)) {
                    add(readInt(i*4))
                }
            }
        }

        assertEquals(data,result)
    }
}