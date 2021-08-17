package space.kscience.kmath.geometry

/**
 * Project vector onto a line.
 * @param vector to project
 * @param line line to which vector should be projected
 */
public fun <V : Vector> GeometrySpace<V>.projectToLine(vector: V, line: Line<V>): V = with(line) {
    base + (direction dot (vector - base)) / (direction dot direction) * direction
}

/**
 * Project vector onto a hyperplane, which is defined by a normal and base.
 * In 2D case it is the projection to a line, in 3d case it is the one to a plane.
 * @param vector to project
 * @param normal normal (perpendicular) vector to a hyper-plane to which vector should be projected
 * @param base point belonging to a hyper-plane to which vector should be projected
 */
public fun <V : Vector> GeometrySpace<V>.projectAlong(vector: V, normal: V, base: V): V =
    vector + normal * ((base - vector) dot normal) / (normal dot normal)
