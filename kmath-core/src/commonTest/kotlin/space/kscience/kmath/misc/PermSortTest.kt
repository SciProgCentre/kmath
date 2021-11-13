/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.misc

import kotlin.collections.mutableListOf
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

import space.kscience.kmath.structures.IntBuffer

class PermSortTest {

    /**
     * Permutation on empty buffer should immediately return an empty array.
     */
    @Test
    fun testOnEmptyBuffer() {
        val emptyBuffer = IntBuffer(0) {it}
        var permutations = emptyBuffer.permSort()
        assertTrue(permutations.isEmpty(), "permutation on an empty buffer should return an empty result")
        permutations = emptyBuffer.permSort(true)
        assertTrue(permutations.isEmpty(), "permutation on an empty buffer should return an empty result")
    }

    @Test
    fun testOnSingleValueBuffer() {
        testPermutation(1)
    }

    @Test
    public fun testOnSomeValues() {
        testPermutation(10)
    }

    private fun testPermutation(bufferSize: Int) {   

        val seed = Random.nextLong()
        println("Test randomization seed: $seed")

        val buffer = Random(seed).buffer(bufferSize)
        val indices = buffer.permSort()

        assertEquals(bufferSize, indices.size)
        // Ensure no doublon is present in indices
        assertEquals(indices.toSet().size, indices.size)

        for (i in 0 until (bufferSize-1)) {
            val current = buffer[indices[i]]
            val next = buffer[indices[i+1]]
            assertTrue(current <= next, "Permutation indices not properly sorted")
        }

        val descIndices = buffer.permSort(true)
        assertEquals(bufferSize, descIndices.size) 
        // Ensure no doublon is present in indices
        assertEquals(descIndices.toSet().size, descIndices.size)

        for (i in 0 until (bufferSize-1)) {
            val current = buffer[descIndices[i]]
            val next = buffer[descIndices[i+1]]
            assertTrue(current >= next, "Permutation indices not properly sorted in descending order")
        }
    }

    private fun Random.buffer(size : Int) = IntBuffer(size) { nextInt() }
}
