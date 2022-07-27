package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.radiansToDegrees
import space.kscience.kmath.trajectory.segments.components.Circle
import kotlin.test.Test
import kotlin.test.assertEquals

class ArcTests {

    @Test
    fun arcTest() {
        val circle = Circle(Vector2D(0.0, 0.0), 2.0)
        val arc = Arc.of(circle.center, Vector2D(-2.0, 0.0), Vector2D(0.0, 2.0), Arc.Direction.RIGHT)
        assertEquals(circle.circumference / 4, arc.length, 1.0)
        assertEquals(0.0, arc.start.theta.radiansToDegrees())
        assertEquals(90.0, arc.end.theta.radiansToDegrees())
    }
}
