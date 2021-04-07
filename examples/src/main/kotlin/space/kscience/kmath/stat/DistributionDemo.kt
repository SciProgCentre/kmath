package space.kscience.kmath.stat

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.collectWithState
import space.kscience.kmath.distributions.NormalDistribution

/**
 * The state of distribution averager.
 */
private data class AveragingChainState(var num: Int = 0, var value: Double = 0.0)

/**
 * Averaging.
 */
private fun Chain<Double>.mean(): Chain<Double> = collectWithState(AveragingChainState(), { it.copy() }) { chain ->
    val next = chain.next()
    num++
    value += next
    return@collectWithState value / num
}


fun main() {
    val normal = NormalDistribution(0.0, 2.0)
    val chain = normal.sample(RandomGenerator.default).mean()

    runBlocking {
        repeat(10001) { counter ->
            val mean = chain.next()
            if (counter % 1000 == 0) {
                println("[$counter] Average value is $mean")
            }
        }
    }
}
