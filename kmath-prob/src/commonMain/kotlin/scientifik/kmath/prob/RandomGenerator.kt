package scientifik.kmath.prob

/**
 * A basic generator
 */
interface RandomGenerator {
    fun nextDouble(): Double
    fun nextInt(): Int
    fun nextLong(): Long
    fun nextBlock(size: Int): ByteArray

    /**
     * Fork the current state of generator
     */
    fun fork(): RandomGenerator

    companion object
}