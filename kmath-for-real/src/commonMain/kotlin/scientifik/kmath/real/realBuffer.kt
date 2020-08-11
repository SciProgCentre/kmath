package scientifik.kmath.real

import scientifik.kmath.structures.RealBuffer

/**
 * Simplified [RealBuffer] to array comparison
 */
fun RealBuffer.contentEquals(vararg doubles: Double) = array.contentEquals(doubles)