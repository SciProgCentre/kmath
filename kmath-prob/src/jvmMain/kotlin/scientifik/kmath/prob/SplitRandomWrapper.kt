package scientifik.kmath.prob

import java.util.*

class SplitRandomWrapper(val random: SplittableRandom) : RandomGenerator {

    constructor(seed: Long) : this(SplittableRandom(seed))

    override fun nextDouble(): Double = random.nextDouble()

    override fun nextInt(): Int = random.nextInt()

    override fun nextLong(): Long = random.nextLong()

    override fun nextBlock(size: Int): ByteArray = ByteArray(size).also { random.nextBytes(it) }

    override fun fork(): RandomGenerator = SplitRandomWrapper(random.split())
}