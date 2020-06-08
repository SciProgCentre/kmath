package scientifik.kmath.commons.rng.sampling.distribution

import scientifik.kmath.commons.rng.sampling.SharedStateSampler

interface SharedStateDiscreteSampler : DiscreteSampler,
    SharedStateSampler<SharedStateDiscreteSampler?> { // Composite interface
}
