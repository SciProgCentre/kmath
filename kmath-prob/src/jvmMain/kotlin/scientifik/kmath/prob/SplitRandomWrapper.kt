package scientifik.kmath.prob

import java.util.*

class SplitRandomWrapper(random: SplittableRandom) : RandomGenerator {

    var random = random
        private set

    constructor(seed: Long) : this(SplittableRandom(seed))

    override fun nextDouble(): Double = random.nextDouble()

    override fun nextInt(): Int = random.nextInt()

    override fun nextLong(): Long = random.nextLong()

    override fun nextBlock(size: Int): ByteArray = ByteArray(size).also { random.nextBytes(it) }

    override fun fork(): RandomGenerator {
        synchronized(this) {
            return SplitRandomWrapper(random.split()).also { this.random = random.split() }
        }
    }
}