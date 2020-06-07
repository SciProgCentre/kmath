package scientifik.commons.rng.sampling.distribution

import scientifik.commons.rng.sampling.SharedStateSampler

interface SharedStateContinuousSampler : ContinuousSampler,
    SharedStateSampler<SharedStateContinuousSampler?> {
}
