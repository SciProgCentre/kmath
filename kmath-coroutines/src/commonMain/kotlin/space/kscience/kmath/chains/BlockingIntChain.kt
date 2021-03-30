package space.kscience.kmath.chains

/**
 * Performance optimized chain for integer values
 */
public interface BlockingIntChain : Chain<Int> {
    public override suspend fun next(): Int
    public suspend fun nextBlock(size: Int): IntArray = IntArray(size) { next() }
}
