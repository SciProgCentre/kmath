package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.circumference
import space.kscience.kmath.trajectory.ArcSegment
import space.kscience.kmath.trajectory.radiansToDegrees
import kotlin.test.Test
import kotlin.test.assertEquals

class ArcTests {

    @Test
    fun arcTest() = with(Euclidean2DSpace){
        val circle = Circle2D(vector(0.0, 0.0), 2.0)
        val arc = ArcSegment.of(circle.center, vector(-2.0, 0.0), vector(0.0, 2.0), ArcSegment.Direction.RIGHT)
        assertEquals(circle.circumference / 4, arc.length, 1.0)
        assertEquals(0.0, arc.start.theta.radiansToDegrees())
        assertEquals(90.0, arc.end.theta.radiansToDegrees())
    }
}
