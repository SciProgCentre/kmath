/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.random.chain
import space.kscience.kmath.streaming.chunked
import space.kscience.kmath.structures.Float64Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MeanStatisticTest {
    //create a random number generator.
    val generator = RandomGenerator.default(1)

    private fun setupData(): Flow<Float64Buffer> {
        //Create a stateless chain from generator.
        val data = generator.chain { nextDouble() }

        //Convert a chain to Flow and break it into chunks.
        return data.chunked(1000)
    }


    @Test
    fun singleBlockingMean() = runTest {
        val chunked = setupData()
        val first = chunked.first()
        val res = Float64Field.mean(first)
        assertEquals(0.5, res, 1e-1)
    }

    @Test
    fun singleSuspendMean() = runTest {
        val chunked = setupData()
        val first = runBlocking { chunked.first() }
        val res = Float64Field.mean(first)
        assertEquals(0.5, res, 1e-1)
    }

    @Test
    fun parallelMean() = runTest {
        val chunked = setupData()
        val average = Float64Field.mean
            .flow(chunked) //create a flow from evaluated results
            .take(100) // Take 100 data chunks from the source and accumulate them
            .last() //get 1e5 data samples average

        assertEquals(0.5, average, 1e-2)
    }

}
