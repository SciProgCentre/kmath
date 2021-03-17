package space.kscience.kmath.chains

/**
 * Performance optimized chain for real values
 */
public abstract class BlockingDoubleChain : Chain<Double> {
    public abstract fun nextDouble(): Double

    override suspend fun next(): Double = nextDouble()

    public fun nextBlock(size: Int): DoubleArray = DoubleArray(size) { nextDouble() }
}
