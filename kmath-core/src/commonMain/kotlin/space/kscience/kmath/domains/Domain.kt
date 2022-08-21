/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.domains

import space.kscience.kmath.linear.Point

/**
 * A simple geometric domain.
 *
 * @param T the type of element of this domain.
 */
public interface Domain<in T : Any> {
    /**
     * Checks if the specified point is contained in this domain.
     */
    public operator fun contains(point: Point<T>): Boolean

    /**
     * Number of hyperspace dimensions.
     */
    public val dimension: Int
}
