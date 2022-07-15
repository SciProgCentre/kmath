package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.Line2D
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.trajectory.radiansToDegrees
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class LineTests {

    @Test
    fun lineTest() {
        val line = Line2D(Vector2D(0.0, 0.0), Vector2D(100.0, 100.0))
        assertEquals(sqrt(100.0.pow(2) + 100.0.pow(2)), line.length)
        assertEquals(45.0, line.theta.radiansToDegrees())
    }

    @Test
    fun lineAngleTest() {
        val zero = Vector2D(0.0, 0.0)
        val north = Line2D(Euclidean2DSpace.zero, Vector2D(0.0, 2.0))
        assertEquals(0.0, north.theta.radiansToDegrees())
        val east = Line2D(Euclidean2DSpace.zero, Vector2D(2.0, 0.0))
        assertEquals(90.0, east.theta.radiansToDegrees())
        val south = Line2D(Euclidean2DSpace.zero, Vector2D(0.0, -2.0))
        assertEquals(180.0, south.theta.radiansToDegrees())
        val west = Line2D(Euclidean2DSpace.zero, Vector2D(-2.0, 0.0))
        assertEquals(270.0, west.theta.radiansToDegrees())
    }
}
