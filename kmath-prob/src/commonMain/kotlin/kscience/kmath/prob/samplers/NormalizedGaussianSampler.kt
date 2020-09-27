package scientifik.kmath.prob.samplers

import scientifik.kmath.prob.Sampler

/**
 * Marker interface for a sampler that generates values from an N(0,1)
 * [Gaussian distribution](https://en.wikipedia.org/wiki/Normal_distribution).
 */
interface NormalizedGaussianSampler : Sampler<Double>
