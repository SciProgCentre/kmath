package scientifik.kmath.domains

import scientifik.kmath.linear.Point

/**
 * A simple geometric domain.
 *
 * @param T the type of element of this domain.
 */
interface Domain<T : Any> {
    /**
     * Checks if the specified point is contained in this domain.
     */
    operator fun contains(point: Point<T>): Boolean

    /**
     * Number of hyperspace dimensions.
     */
    val dimension: Int
}
