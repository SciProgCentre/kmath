package scientifik.kmath.geometry

import scientifik.kmath.operations.Space

interface Vector

interface GeometrySpace<V: Vector>: Space<V> {
    /**
     * L2 distance
     */
    fun V.distanceTo(other: V): Double

    /**
     * Scalar product
     */
    infix fun V.dot(other: V): Double
}