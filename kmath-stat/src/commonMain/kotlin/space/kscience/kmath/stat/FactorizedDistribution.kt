package space.kscience.kmath.stat

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.SimpleChain

/**
 * A multivariate distribution which takes a map of parameters
 */
public interface NamedDistribution<T> : Distribution<Map<String, T>>

/**
 * A multivariate distribution that has independent distributions for separate axis
 */
public class FactorizedDistribution<T>(public val distributions: Collection<NamedDistribution<T>>) :
    NamedDistribution<T> {
    override fun probability(arg: Map<String, T>): Double =
        distributions.fold(1.0) { acc, distr -> acc * distr.probability(arg) }

    override fun sample(generator: RandomGenerator): Chain<Map<String, T>> {
        val chains = distributions.map { it.sample(generator) }
        return SimpleChain { chains.fold(emptyMap()) { acc, chain -> acc + chain.next() } }
    }
}

public class NamedDistributionWrapper<T : Any>(public val name: String, public val distribution: Distribution<T>) :
    NamedDistribution<T> {
    override fun probability(arg: Map<String, T>): Double = distribution.probability(
        arg[name] ?: error("Argument with name $name not found in input parameters")
    )

    override fun sample(generator: RandomGenerator): Chain<Map<String, T>> {
        val chain = distribution.sample(generator)
        return SimpleChain { mapOf(name to chain.next()) }
    }
}

public class DistributionBuilder<T : Any> {
    private val distributions = ArrayList<NamedDistribution<T>>()

    public infix fun String.to(distribution: Distribution<T>) {
        distributions.add(NamedDistributionWrapper(this, distribution))
    }
}
