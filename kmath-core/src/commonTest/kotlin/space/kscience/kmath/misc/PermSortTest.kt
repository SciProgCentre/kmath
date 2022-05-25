/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.misc

import space.kscience.kmath.misc.PermSortTest.Platform.*
import space.kscience.kmath.structures.IntBuffer
import space.kscience.kmath.structures.asBuffer
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PermSortTest {

    private enum class Platform {
        ANDROID, JVM, JS, NATIVE, WASM
    }

    private val platforms = Platform.values().asBuffer()

    /**
     * Permutation on empty buffer should immediately return an empty array.
     */
    @Test
    fun testOnEmptyBuffer() {
        val emptyBuffer = IntBuffer(0) {it}
        var permutations = emptyBuffer.indicesSorted()
        assertTrue(permutations.isEmpty(), "permutation on an empty buffer should return an empty result")
        permutations = emptyBuffer.indicesSortedDescending()
        assertTrue(permutations.isEmpty(), "permutation on an empty buffer should return an empty result")
    }

    @Test
    fun testOnSingleValueBuffer() {
        testPermutation(1)
    }

    @Test
    fun testOnSomeValues() {
        testPermutation(10)
    }

    @Test
    fun testPermSortBy() {
        val permutations = platforms.indicesSortedBy { it.name }
        val expected = listOf(ANDROID, JS, JVM, NATIVE, WASM)
        assertContentEquals(expected, permutations.map { platforms[it] }, "Ascending PermSort by name")
    }

    @Test
    fun testPermSortByDescending() {
        val permutations = platforms.indicesSortedByDescending { it.name }
        val expected = listOf(WASM, NATIVE, JVM, JS, ANDROID)
        assertContentEquals(expected, permutations.map { platforms[it] }, "Descending PermSort by name")
    }

    @Test
    fun testPermSortWith() {
        var permutations = platforms.indicesSortedWith { p1, p2 -> p1.name.length.compareTo(p2.name.length) }
        val expected = listOf(JS, JVM, WASM, NATIVE, ANDROID)
        assertContentEquals(expected, permutations.map { platforms[it] }, "PermSort using custom ascending comparator")

        permutations = platforms.indicesSortedWith(compareByDescending { it.name.length })
        assertContentEquals(expected.reversed(), permutations.map { platforms[it] }, "PermSort using custom descending comparator")
    }

    private fun testPermutation(bufferSize: Int) {   

        val seed = Random.nextLong()
        println("Test randomization seed: $seed")

        val buffer = Random(seed).buffer(bufferSize)
        val indices = buffer.indicesSorted()

        assertEquals(bufferSize, indices.size)
        // Ensure no doublon is present in indices
        assertEquals(indices.toSet().size, indices.size)

        for (i in 0 until (bufferSize-1)) {
            val current = buffer[indices[i]]
            val next = buffer[indices[i+1]]
            assertTrue(current <= next, "Permutation indices not properly sorted")
        }

        val descIndices = buffer.indicesSortedDescending()
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
