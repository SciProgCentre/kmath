/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.chains

/**
 * Performance optimized chain for real values
 */
public abstract class BlockingRealChain : Chain<Double> {
    public abstract fun nextDouble(): Double

    override suspend fun next(): Double = nextDouble()

    public fun nextBlock(size: Int): DoubleArray = DoubleArray(size) { nextDouble() }
}
