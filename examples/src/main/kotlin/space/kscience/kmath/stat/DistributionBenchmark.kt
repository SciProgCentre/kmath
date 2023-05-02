/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.apache.commons.rng.sampling.distribution.BoxMullerNormalizedGaussianSampler
import org.apache.commons.rng.simple.RandomSource
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.samplers.GaussianSampler
import java.time.Duration
import java.time.Instant
import org.apache.commons.rng.sampling.distribution.GaussianSampler as CMGaussianSampler

private suspend fun runKMathChained(): Duration {
    val generator = RandomGenerator.fromSource(RandomSource.MT, 123L)
    val normal = GaussianSampler(7.0, 2.0)
    val chain = normal.sample(generator)
    val startTime = Instant.now()
    var sum = 0.0

    repeat(10000001) { counter ->
        sum += chain.next()

        if (counter % 100000 == 0) {
            val duration = Duration.between(startTime, Instant.now())
            val meanValue = sum / counter
            println("Chain sampler completed $counter elements in $duration: $meanValue")
        }
    }

    return Duration.between(startTime, Instant.now())
}

private fun runCMDirect(): Duration {
    val rng = RandomSource.create(RandomSource.MT, 123L)

    val sampler = CMGaussianSampler.of(
        BoxMullerNormalizedGaussianSampler.of(rng),
        7.0,
        2.0
    )

    val startTime = Instant.now()
    var sum = 0.0

    repeat(10000001) { counter ->
        sum += sampler.sample()

        if (counter % 100000 == 0) {
            val duration = Duration.between(startTime, Instant.now())
            val meanValue = sum / counter
            println("Direct sampler completed $counter elements in $duration: $meanValue")
        }
    }

    return Duration.between(startTime, Instant.now())
}

/**
 * Comparing chain sampling performance with direct sampling performance
 */
fun main(): Unit = runBlocking(Dispatchers.Default) {
    val directJob = async { runCMDirect() }
    val chainJob = async { runKMathChained() }
    println("KMath Chained: ${chainJob.await()}")
    println("Apache Direct: ${directJob.await()}")
}
