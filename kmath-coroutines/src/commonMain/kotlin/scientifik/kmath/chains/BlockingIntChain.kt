package scientifik.kmath.chains

/**
 * Performance optimized chain for integer values
 */
abstract class BlockingIntChain : Chain<Int> {
    suspend fun nextBlock(size: Int): IntArray = IntArray(size) { next() }
}
