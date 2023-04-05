/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.commons.random

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.misc.toIntExact
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.samplers.GaussianSampler
import space.kscience.kmath.stat.next


public class CMRandomGeneratorWrapper(
    public val factory: (IntArray) -> RandomGenerator,
) : org.apache.commons.math3.random.RandomGenerator {
    private var generator: RandomGenerator = factory(intArrayOf())

    override fun nextBoolean(): Boolean = generator.nextBoolean()
    override fun nextFloat(): Float = generator.nextDouble().toFloat()

    override fun setSeed(seed: Int) {
        generator = factory(intArrayOf(seed))
    }

    override fun setSeed(seed: IntArray) {
        generator = factory(seed)
    }

    override fun setSeed(seed: Long) {
        setSeed(seed.toIntExact())
    }

    override fun nextBytes(bytes: ByteArray) {
        generator.fillBytes(bytes)
    }

    override fun nextInt(): Int = generator.nextInt()
    override fun nextInt(n: Int): Int = generator.nextInt(n)
    override fun nextGaussian(): Double = runBlocking { GaussianSampler(0.0, 1.0).next(generator) }
    override fun nextDouble(): Double = generator.nextDouble()
    override fun nextLong(): Long = generator.nextLong()
}
