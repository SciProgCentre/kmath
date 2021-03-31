package space.kscience.kmath.chains

/**
 * Chunked, specialized chain for real values.
 */
public interface BlockingDoubleChain : Chain<Double> {
    public override suspend fun next(): Double

    /**
     * Returns an [DoubleArray] chunk of [size] values of [next].
     */
    public suspend fun nextBlock(size: Int): DoubleArray = DoubleArray(size) { next() }
}
