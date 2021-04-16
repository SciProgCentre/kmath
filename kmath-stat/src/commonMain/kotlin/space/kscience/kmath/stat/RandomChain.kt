/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.chains.Chain

/**
 * A possibly stateful chain producing random values.
 */
public class RandomChain<out R>(
    public val generator: RandomGenerator,
    private val gen: suspend RandomGenerator.() -> R
) : Chain<R> {
    override suspend fun next(): R = generator.gen()

    override fun fork(): Chain<R> = RandomChain(generator.fork(), gen)
}

public fun <R> RandomGenerator.chain(gen: suspend RandomGenerator.() -> R): RandomChain<R> = RandomChain(this, gen)
