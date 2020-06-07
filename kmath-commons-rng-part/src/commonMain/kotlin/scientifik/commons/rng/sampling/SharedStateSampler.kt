package scientifik.commons.rng.sampling

import scientifik.commons.rng.UniformRandomProvider

interface SharedStateSampler<R> {
    fun withUniformRandomProvider(rng: UniformRandomProvider): R
}
