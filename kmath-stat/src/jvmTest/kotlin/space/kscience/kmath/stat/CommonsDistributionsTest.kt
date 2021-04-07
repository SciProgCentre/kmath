package space.kscience.kmath.stat

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import space.kscience.kmath.samplers.GaussianSampler
import space.kscience.kmath.structures.asBuffer

internal class CommonsDistributionsTest {
    @Test
    fun testNormalDistributionSuspend() = runBlocking {
        val distribution = GaussianSampler(7.0, 2.0)
        val generator = RandomGenerator.default(1)
        val sample = distribution.sample(generator).take(1000).toList().asBuffer()
        Assertions.assertEquals(7.0, Mean.double(sample), 0.2)
    }

    @Test
    fun testNormalDistributionBlocking()  {
        val distribution = GaussianSampler(7.0, 2.0)
        val generator = RandomGenerator.default(1)
        val sample = distribution.sample(generator).nextBufferBlocking(1000)
        runBlocking {
            Assertions.assertEquals(7.0, Mean.double(sample), 0.2)
        }
    }
}
