package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.radiansToDegrees
import space.kscience.kmath.trajectory.segments.components.Circle
import kotlin.test.Test
import kotlin.test.assertEquals

class ArcTests {

    @Test
    fun arcTest() {
        val center = Vector2D(0.0, 0.0)
        val radius = 2.0
        val expectedCircumference = 12.56637
        val circle = Circle(center, radius)
        assertEquals(expectedCircumference, circle.circumference, 1.0)

        val arc = Arc(center, radius, Vector2D(-2.0, 0.0), Vector2D(0.0, 2.0), Arc.Direction.RIGHT)
        assertEquals(expectedCircumference / 4, arc.length, 1.0)
        assertEquals(0.0, arc.pose1.theta.radiansToDegrees())
        assertEquals(90.0, arc.pose2.theta.radiansToDegrees())
    }
}
