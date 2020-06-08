package scientifik.kmath.commons.rng.sampling.distribution

import scientifik.kmath.commons.rng.sampling.SharedStateSampler

interface SharedStateContinuousSampler : ContinuousSampler,
    SharedStateSampler<SharedStateContinuousSampler?> {
}
