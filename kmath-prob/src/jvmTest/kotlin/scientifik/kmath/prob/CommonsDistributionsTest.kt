package scientifik.kmath.prob

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CommonsDistributionsTest {
    @Test
    fun testNormalDistribution(){
        val distribution = Distribution.normal(7.0,2.0)
        val generator = RandomGenerator.default(1)
        val sample = runBlocking {
            distribution.sample(generator).take(1000).toList()
        }
        Assertions.assertEquals(7.0, sample.average(), 0.1)
    }
}