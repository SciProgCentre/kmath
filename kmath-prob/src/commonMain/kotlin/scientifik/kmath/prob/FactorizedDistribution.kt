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

class NamedDistributionWrapper<T : Any>(val name: String, val distribution: Distribution<T>) : NamedDistribution<T> {
    override fun probability(arg: Map<String, T>): Double = distribution.probability(
        arg[name] ?: error("Argument with name $name not found in input parameters")
    )

    override fun sample(generator: RandomGenerator): Chain<Map<String, T>> {
        val chain = distribution.sample(generator)
        return SimpleChain {
            mapOf(name to chain.next())
        }
    }
}

class DistributionBuilder<T: Any>{
    private val distributions = ArrayList<NamedDistribution<T>>()

    infix fun String.to(distribution: Distribution<T>){
        distributions.add(NamedDistributionWrapper(this,distribution))
    }
}