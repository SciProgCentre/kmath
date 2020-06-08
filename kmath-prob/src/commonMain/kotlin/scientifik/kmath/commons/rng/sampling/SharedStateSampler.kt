package scientifik.kmath.commons.rng.sampling

import scientifik.kmath.commons.rng.UniformRandomProvider

interface SharedStateSampler<R> {
    fun withUniformRandomProvider(rng: UniformRandomProvider): R
}
