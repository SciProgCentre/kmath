package scientifik.kmath.commons.rng

interface UniformRandomProvider {
    fun nextBytes(bytes: ByteArray)

    fun nextBytes(
        bytes: ByteArray,
        start: Int,
        len: Int
    )

    fun nextInt(): Int
    fun nextInt(n: Int): Int
    fun nextLong(): Long
    fun nextLong(n: Long): Long
    fun nextBoolean(): Boolean
    fun nextFloat(): Float
    fun nextDouble(): Double
}