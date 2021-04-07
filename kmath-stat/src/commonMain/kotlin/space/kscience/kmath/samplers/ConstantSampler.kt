package space.kscience.kmath.samplers

import space.kscience.kmath.chains.BlockingBufferChain
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.structures.Buffer

public class ConstantSampler<T : Any>(public val const: T) : Sampler<T> {
    override fun sample(generator: RandomGenerator): BlockingBufferChain<T> = object : BlockingBufferChain<T> {
        override fun nextBufferBlocking(size: Int): Buffer<T> = Buffer.boxing(size) { const }
        override suspend fun fork(): BlockingBufferChain<T> = this
    }
}