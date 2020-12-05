package kscience.kmath.stat

import kotlinx.coroutines.runBlocking
import kscience.kmath.chains.Chain
import kscience.kmath.chains.collectWithState

/**
 * The state of distribution averager
 */
private data class AveragingChainState(var num: Int = 0, var value: Double = 0.0)

/**
 * Averaging
 */
private fun Chain<Double>.mean(): Chain<Double> = collectWithState(AveragingChainState(), { it.copy() }) { chain ->
    val next = chain.next()
    num++
    value += next
    return@collectWithState value / num
}


fun main() {
    val normal = Distribution.normal()
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