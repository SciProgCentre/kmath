/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.chains.BlockingDoubleChain
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.nextDoubleBuffer

/**
 * A possibly stateful chain producing random values.
 *
 * @property generator the underlying [RandomGenerator] instance.
 */
public class RandomChain<out R>(
    public val generator: RandomGenerator,
    private val gen: suspend RandomGenerator.() -> R,
) : Chain<R> {
    override suspend fun next(): R = generator.gen()
    override suspend fun fork(): Chain<R> = RandomChain(generator.fork(), gen)
}

/**
 * Create a generic random chain with provided [generator]
 */
public fun <R> RandomGenerator.chain(generator: suspend RandomGenerator.() -> R): RandomChain<R> = RandomChain(this, generator)

/**
 * A type-specific double chunk random chain
 */
public class UniformDoubleChain(public val generator: RandomGenerator) : BlockingDoubleChain {
    override fun nextBufferBlocking(size: Int): DoubleBuffer = nextDoubleBuffer(size)
    override suspend fun nextBuffer(size: Int): DoubleBuffer = nextBufferBlocking(size)

    override suspend fun fork(): UniformDoubleChain = UniformDoubleChain(generator.fork())
}

