/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.Norm
import space.kscience.kmath.operations.ScaleOperations

/**
 * A geometry vector space
 * @param V the type of vector object
 * @param D the type of distance
 */
public interface GeometrySpace<V : Any, D: Comparable<D>> : Group<V>, ScaleOperations<V>, Norm<V, D> {
    /**
     * L2 distance
     */
    public fun V.distanceTo(other: V): D

    /**
     * Scalar product
     */
    public infix fun V.dot(other: V): Double

    /**
     * Default precision for geometry objects comparison
     */
    public val defaultPrecision: D

    public companion object
}