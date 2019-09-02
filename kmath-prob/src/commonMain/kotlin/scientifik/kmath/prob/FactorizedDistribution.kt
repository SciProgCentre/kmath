package scientifik.kmath.prob

import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.SimpleChain

/**
 * A multivariate distribution which takes a map of parameters
 */
interface NamedDistribution<T> : Distribution<Map<String, T>>

/**
 * A multivariate distribution that has independent distributions for separate axis
 */
class FactorizedDistribution<T>(val distributions: Collection<NamedDistribution<T>>) : NamedDistribution<T> {

    override fun probability(arg: Map<String, T>): Double {
        return distributions.fold(1.0) { acc, distr -> acc * distr.probability(arg) }
    }

    override fun sample(generator: RandomGenerator): Chain<Map<String, T>> {
        val chains = distributions.map { it.sample(generator) }
        return SimpleChain<Map<String, T>> {
            chains.fold(emptyMap()) { acc, chain -> acc + chain.next() }
        }
    }
}