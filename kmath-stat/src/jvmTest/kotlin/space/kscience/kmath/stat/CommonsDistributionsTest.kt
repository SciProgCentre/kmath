package space.kscience.kmath.stat

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import space.kscience.kmath.stat.samplers.GaussianSampler

internal class CommonsDistributionsTest {
    @Test
    fun testNormalDistributionSuspend() {
        val distribution = GaussianSampler.of(7.0, 2.0)
        val generator = RandomGenerator.default(1)
        val sample = runBlocking { distribution.sample(generator).take(1000).toList() }
        Assertions.assertEquals(7.0, sample.average(), 0.1)
    }

    @Test
    fun testNormalDistributionBlocking() {
        val distribution = GaussianSampler.of(7.0, 2.0)
        val generator = RandomGenerator.default(1)
        val sample = runBlocking { distribution.sample(generator).blocking().nextBlock(1000) }
        Assertions.assertEquals(7.0, sample.average(), 0.1)
    }
}
