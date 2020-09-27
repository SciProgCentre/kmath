package kscience.kmath.chains

/**
 * Performance optimized chain for real values
 */
public interface BlockingRealChain : Chain<Double> {
    public override suspend fun next(): Double
    public suspend fun nextBlock(size: Int): DoubleArray = DoubleArray(size) { next() }
}
