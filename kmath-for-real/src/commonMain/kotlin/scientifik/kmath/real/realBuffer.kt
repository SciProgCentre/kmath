package scientifik.kmath.real

import scientifik.kmath.structures.DoubleBuffer

/**
 * C
 */
fun DoubleBuffer.contentEquals(vararg doubles: Double) = array.contentEquals(doubles)