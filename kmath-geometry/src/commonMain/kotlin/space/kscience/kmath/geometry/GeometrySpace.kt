/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.ScaleOperations

public interface Vector

public interface GeometrySpace<V : Vector> : Group<V>, ScaleOperations<V> {
    /**
     * L2 distance
     */
    public fun V.distanceTo(other: V): Double

    /**
     * Scalar product
     */
    public infix fun V.dot(other: V): Double
}