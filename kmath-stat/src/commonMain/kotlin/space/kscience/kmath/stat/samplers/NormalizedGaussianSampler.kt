package space.kscience.kmath.stat.samplers

import space.kscience.kmath.stat.Sampler

/**
 * Marker interface for a sampler that generates values from an N(0,1)
 * [Gaussian distribution](https://en.wikipedia.org/wiki/Normal_distribution).
 */
public interface NormalizedGaussianSampler : Sampler<Double>
