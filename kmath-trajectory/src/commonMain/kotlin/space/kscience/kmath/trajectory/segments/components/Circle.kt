package space.kscience.kmath.trajectory.segments.components

import space.kscience.kmath.geometry.Vector2D
import kotlin.math.PI

public open class Circle(
    internal val center: Vector2D,
    internal val radius: Double
) {
    internal val circumference = radius * 2 * PI
}
