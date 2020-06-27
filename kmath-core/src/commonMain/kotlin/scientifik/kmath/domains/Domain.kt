package scientifik.kmath.domains

import scientifik.kmath.linear.Point

/**
 * A simple geometric domain
 */
interface Domain<T : Any> {
    operator fun contains(point: Point<T>): Boolean

    /**
     * Number of hyperspace dimensions
     */
    val dimension: Int
}