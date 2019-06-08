package scientifik.kmath.commons.prob

import kotlinx.coroutines.flow.take
import scientifik.kmath.chains.flow
import scientifik.kmath.prob.Distribution
import scientifik.kmath.prob.RandomGenerator

fun main() {
    val normal = Distribution.normal()
    normal.sample(RandomGenerator.default)
}