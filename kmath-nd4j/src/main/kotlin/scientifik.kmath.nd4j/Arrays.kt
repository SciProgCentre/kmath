package scientifik.kmath.nd4j

internal fun narrowToIntArray(la: LongArray): IntArray = IntArray(la.size) { la[it].toInt() }