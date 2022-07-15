package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Line2D
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.segments.components.Pose2D
import space.kscience.kmath.trajectory.segments.theta
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

const val maxFloatDelta = 0.000001

fun Double.radiansToDegrees() = this * 180 / PI

fun Double.equalFloat(other: Double) = abs(this - other) < maxFloatDelta
fun Pose2D.equalsFloat(other: Pose2D) = x.equalFloat(other.x) && y.equalFloat(other.y) && theta.equalFloat(other.theta)

fun Line2D.inverse() = Line2D(direction, base)
fun Line2D.shift(shift: Int, width: Double): Line2D {
    val dX = width * sin(inverse().theta)
    val dY = width * sin(theta)

    return Line2D(
        Vector2D(base.x - dX * shift, base.y - dY * shift),
        Vector2D(direction.x - dX * shift, direction.y - dY * shift)
    )
}
