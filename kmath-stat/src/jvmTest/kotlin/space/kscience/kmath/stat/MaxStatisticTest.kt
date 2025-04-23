/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.random.chain
import space.kscience.kmath.streaming.chunked
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MaxStatisticTest {
    //create a random number generator.
    val generator = RandomGenerator.default(1)

    //Create a stateless chain from generator.
    val data = generator.chain { nextDouble() }

    //Convert a chain to Flow and break it into chunks.
    val chunked = data.chunked(1000)

    @Test
    fun singleBlockingMax() = runTest {
        val first = chunked.first()
        val res = Float64Field.max(first)
        assertEquals(1.0, res, 1e-1)
    }

    @Test
    fun singleSuspendMax() = runTest {
        val first = runBlocking { chunked.first() }
        val res = Float64Field.max(first)
        assertEquals(1.0, res, 1e-1)
    }

    @Test
    fun parallelMax() = runTest {
        val maxValue = Float64Field.max
            .flow(chunked) //create a flow from evaluated results
            .take(100) // Take 100 data chunks from the source and accumulate them
            .last() //get the maximum from 1e5 data samples

        assertEquals(1.0, maxValue, 1e-2)
    }
}