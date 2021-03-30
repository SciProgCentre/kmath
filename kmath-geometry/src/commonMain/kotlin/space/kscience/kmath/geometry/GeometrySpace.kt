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