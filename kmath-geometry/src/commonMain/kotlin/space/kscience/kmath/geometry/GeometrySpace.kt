package space.kscience.kmath.geometry

import space.kscience.kmath.operations.Space

public interface Vector

public interface GeometrySpace<V : Vector> : Space<V> {
    /**
     * L2 distance
     */
    public fun V.distanceTo(other: V): Double

    /**
     * Scalar product
     */
    public infix fun V.dot(other: V): Double
}