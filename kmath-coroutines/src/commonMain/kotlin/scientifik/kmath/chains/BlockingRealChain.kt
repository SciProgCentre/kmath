package scientifik.kmath.chains

/**
 * Performance optimized chain for real values
 */
abstract class BlockingRealChain : Chain<Double> {
    suspend fun nextBlock(size: Int): DoubleArray = DoubleArray(size) { next() }
}
