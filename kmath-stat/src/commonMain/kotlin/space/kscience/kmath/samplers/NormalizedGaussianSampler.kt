/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler

public interface BlockingDoubleSampler : Sampler<Double> {
    override fun sample(generator: RandomGenerator): BlockingDoubleChain
}


/**
 * Marker interface for a sampler that generates values from an N(0,1)
 * [Gaussian distribution](https://en.wikipedia.org/wiki/Normal_distribution).
 */
public fun interface NormalizedGaussianSampler : BlockingDoubleSampler {
    public companion object
}
