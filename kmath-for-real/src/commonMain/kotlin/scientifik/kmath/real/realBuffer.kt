package scientifik.kmath.real

import scientifik.kmath.structures.DoubleBuffer

/**
 * Simplified [DoubleBuffer] to array comparison
 */
fun DoubleBuffer.contentEquals(vararg doubles: Double) = array.contentEquals(doubles)