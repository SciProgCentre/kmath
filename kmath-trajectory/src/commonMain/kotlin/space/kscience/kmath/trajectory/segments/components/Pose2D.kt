package space.kscience.kmath.trajectory.segments.components

import space.kscience.kmath.geometry.Vector2D

public data class Pose2D(
    override val x: Double,
    override val y: Double,
    public val theta: Double
) : Vector2D {
    internal companion object {
        internal fun of(vector: Vector2D, theta: Double) = Pose2D(vector.x, vector.y, theta)
    }
}
