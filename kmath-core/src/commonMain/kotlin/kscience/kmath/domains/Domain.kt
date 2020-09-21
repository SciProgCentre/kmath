package kscience.kmath.domains

import kscience.kmath.linear.Point

/**
 * A simple geometric domain.
 *
 * @param T the type of element of this domain.
 */
public interface Domain<T : Any> {
    /**
     * Checks if the specified point is contained in this domain.
     */
    public operator fun contains(point: Point<T>): Boolean

    /**
     * Number of hyperspace dimensions.
     */
    public val dimension: Int
}
