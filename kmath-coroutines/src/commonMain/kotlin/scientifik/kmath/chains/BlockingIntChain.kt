package scientifik.kmath.chains

/**
 * Performance optimized chain for integer values
 */
abstract class BlockingIntChain : Chain<Int> {
    abstract fun nextInt(): Int

    override suspend fun next(): Int = nextInt()

    fun nextBlock(size: Int): IntArray = IntArray(size) { nextInt() }
}
