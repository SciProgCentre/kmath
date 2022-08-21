package space.kscience.kmath.trajectory

import space.kscience.kmath.geometry.Euclidean2DSpace
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

const val maxFloatDelta = 0.000001

fun Double.radiansToDegrees() = this * 180 / PI

fun Double.equalFloat(other: Double) = abs(this - other) < maxFloatDelta
fun Pose2D.equalsFloat(other: Pose2D) = x.equalFloat(other.x) && y.equalFloat(other.y) && theta.equalFloat(other.theta)

fun StraightSegment.inverse() = StraightSegment(end, start)
fun StraightSegment.shift(shift: Int, width: Double): StraightSegment = with(Euclidean2DSpace){
    val dX = width * sin(inverse().theta)
    val dY = width * sin(theta)

    return StraightSegment(
        vector(start.x - dX * shift, start.y - dY * shift),
        vector(end.x - dX * shift, end.y - dY * shift)
    )
}
