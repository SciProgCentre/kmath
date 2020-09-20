package kscience.kmath.real

import kscience.kmath.structures.RealBuffer

/**
 * Simplified [RealBuffer] to array comparison
 */
public fun RealBuffer.contentEquals(vararg doubles: Double): Boolean = array.contentEquals(doubles)
