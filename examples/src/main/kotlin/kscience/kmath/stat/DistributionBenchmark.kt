package kscience.kmath.commons.prob

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kscience.kmath.chains.BlockingRealChain
import kscience.kmath.stat.*
import org.apache.commons.rng.sampling.distribution.ZigguratNormalizedGaussianSampler
import org.apache.commons.rng.simple.RandomSource
import java.time.Duration
import java.time.Instant

private fun runChain(): Duration {
    val generator = RandomGenerator.fromSource(RandomSource.MT, 123L)
    val normal = Distribution.normal(NormalSamplerMethod.Ziggurat)
    val chain = normal.sample(generator) as BlockingRealChain
    val startTime = Instant.now()
    var sum = 0.0

    repeat(10000001) { counter ->
        sum += chain.nextDouble()

        if (counter % 100000 == 0) {
            val duration = Duration.between(startTime, Instant.now())
            val meanValue = sum / counter
            println("Chain sampler completed $counter elements in $duration: $meanValue")
        }
    }

    return Duration.between(startTime, Instant.now())
}

private fun runDirect(): Duration {
    val provider = RandomSource.create(RandomSource.MT, 123L)
    val sampler = ZigguratNormalizedGaussianSampler(provider)
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
fun main() {
    runBlocking(Dispatchers.Default) {
        val chainJob = async { runChain() }
        val directJob = async { runDirect() }
        println("Chain: ${chainJob.await()}")
        println("Direct: ${directJob.await()}")
    }
}
