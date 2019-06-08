package scientifik.kmath.commons.prob

import kotlinx.coroutines.runBlocking
import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.StatefulChain
import scientifik.kmath.prob.Distribution
import scientifik.kmath.prob.RandomGenerator

data class AveragingChainState(var num: Int = 0, var value: Double = 0.0)

fun Chain<Double>.mean(): Chain<Double> = StatefulChain(AveragingChainState(), 0.0) {
    val next = this@mean.next()
    num++
    value += next
    return@StatefulChain value / num
}


fun main() {
    val normal = Distribution.normal()
    val chain = normal.sample(RandomGenerator.default).mean()

    runBlocking {
        repeat(10001) { counter ->
            val mean = chain.next()
            if(counter % 1000 ==0){
                println("[$counter] Average value is $mean")
            }
        }
    }
}