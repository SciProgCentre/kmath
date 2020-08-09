package scientifik.kmath.chains

/**
 * Performance optimized chain for real values
 */
abstract class BlockingRealChain : Chain<Double> {
    abstract fun nextDouble(): Double

    override suspend fun next(): Double = nextDouble()

    fun nextBlock(size: Int): DoubleArray = DoubleArray(size) { nextDouble() }
}
