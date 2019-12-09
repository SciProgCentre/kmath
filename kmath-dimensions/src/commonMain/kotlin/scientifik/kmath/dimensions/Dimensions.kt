package scientifik.kmath.dimensions

/**
 * An interface which is not used in runtime. Designates a size of some structure.
 * All descendants should be singleton objects.
 */
interface Dimension {
    val dim: UInt

    companion object {
        @Suppress("NOTHING_TO_INLINE")
        inline fun of(dim: UInt): Dimension {
            return when (dim) {
                1u -> D1
                2u -> D2
                3u -> D3
                else -> object : Dimension {
                    override val dim: UInt = dim
                }
            }
        }
    }
}

expect inline fun <reified D : Dimension>  Dimension.Companion.dim(): UInt

object D1 : Dimension {
    override val dim: UInt = 1u
}

object D2 : Dimension {
    override val dim: UInt = 2u
}

object D3 : Dimension {
    override val dim: UInt = 3u
}