package space.kscience.kmath.chains

/**
 * Performance optimized chain for integer values
 */
public abstract class BlockingIntChain : Chain<Int> {
    public abstract fun nextInt(): Int

    override suspend fun next(): Int = nextInt()

    public fun nextBlock(size: Int): IntArray = IntArray(size) { nextInt() }
}
