package scientifik.kmath.nd4j

internal fun widenToLongArray(ia: IntArray): LongArray = LongArray(ia.size) { ia[it].toLong() }
internal fun narrowToIntArray(la: LongArray): IntArray = IntArray(la.size) { la[it].toInt() }
