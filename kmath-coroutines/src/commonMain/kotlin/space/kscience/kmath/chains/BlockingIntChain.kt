package space.kscience.kmath.chains

import space.kscience.kmath.structures.IntBuffer

/**
 * Performance optimized chain for integer values
 */
public interface BlockingIntChain : BlockingBufferChain<Int> {
    override fun nextBufferBlocking(size: Int): IntBuffer

    override suspend fun fork(): BlockingIntChain
}