package kscience.kmath.nd4j

internal fun IntArray.toLongArray(): LongArray = LongArray(size) { this[it].toLong() }
internal fun LongArray.toIntArray(): IntArray = IntArray(size) { this[it].toInt() }
