package scientifik.kmath.prob

import kotlinx.atomicfu.atomic
import kotlin.random.Random

class CountingRandomWrapper(val seed: Long) : RandomGenerator {
    private val counter = atomic(0)
    private val random = Random(seed)

    override fun nextDouble(): Double = random.nextDouble()

    override fun nextInt(): Int = random.nextInt()

    override fun nextLong(): Long = random.nextLong()

    override fun nextBlock(size: Int): ByteArray = random.nextBytes(size)

    override fun fork(): RandomGenerator = CountingRandomWrapper(seed + counter.addAndGet(10))

}