/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.combineWithState
import space.kscience.kmath.distributions.NormalDistribution
import space.kscience.kmath.random.RandomGenerator

private data class AveragingChainState(var num: Int = 0, var value: Double = 0.0)

/**
 * Averaging.
 */
private fun Chain<Double>.mean(): Chain<Double> = combineWithState(AveragingChainState(), { it.copy() }) { chain ->
    val next = chain.next()
    num++
    value += next
    return@combineWithState value / num
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
