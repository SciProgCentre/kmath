package scientifik.commons.rng.sampling.distribution

import scientifik.commons.rng.sampling.SharedStateSampler

interface SharedStateDiscreteSampler : DiscreteSampler,
    SharedStateSampler<SharedStateDiscreteSampler?> { // Composite interface
}
