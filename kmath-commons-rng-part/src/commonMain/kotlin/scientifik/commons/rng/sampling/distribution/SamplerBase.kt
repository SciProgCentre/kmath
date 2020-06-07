package scientifik.commons.rng.sampling.distribution

import scientifik.commons.rng.UniformRandomProvider

@Deprecated("Since version 1.1. Class intended for internal use only.")
open class SamplerBase protected constructor(private val rng: UniformRandomProvider?) {
    protected fun nextDouble(): Double = rng!!.nextDouble()
    protected fun nextInt(): Int = rng!!.nextInt()
    protected fun nextInt(max: Int): Int = rng!!.nextInt(max)
    protected fun nextLong(): Long = rng!!.nextLong()
    override fun toString(): String = "rng=$rng"
}
