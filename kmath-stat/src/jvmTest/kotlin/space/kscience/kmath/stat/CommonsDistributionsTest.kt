package space.kscience.kmath.stat

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import space.kscience.kmath.samplers.GaussianSampler

internal class CommonsDistributionsTest {
    @Test
    fun testNormalDistributionSuspend() = GlobalScope.launch {
        val distribution = GaussianSampler(7.0, 2.0)
        val generator = RandomGenerator.default(1)
        val sample = distribution.sample(generator).nextBuffer(1000)
        Assertions.assertEquals(7.0, Mean.evaluate(sample), 0.2)
    }

    @Test
    fun testNormalDistributionBlocking() {
        val distribution = GaussianSampler(7.0, 2.0)
        val generator = RandomGenerator.default(1)
        val sample = distribution.sample(generator).nextBufferBlocking(1000)
        Assertions.assertEquals(7.0, Mean.evaluate(sample), 0.2)
    }
}
