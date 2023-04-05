/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry


/**
 * A closed polygon in 2D space
 */
public interface Polygon<T> {
    public val points: List<Vector2D<T>>
}