/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.stat.RandomGenerator

public interface BlockingDoubleSampler: Sampler<Double>{
    override fun sample(generator: RandomGenerator): BlockingDoubleChain
}


/**
 * Marker interface for a sampler that generates values from an N(0,1)
 * [Gaussian distribution](https://en.wikipedia.org/wiki/Normal_distribution).
 */
public fun interface NormalizedGaussianSampler : BlockingDoubleSampler{
    public companion object
}
