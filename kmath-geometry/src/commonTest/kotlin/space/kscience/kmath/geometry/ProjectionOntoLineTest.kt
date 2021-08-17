package space.kscience.kmath.geometry

import kotlin.test.Test
import kotlin.test.assertTrue

internal class ProjectionOntoLineTest {
    @Test
    fun projectionIntoOx() {
        with(Euclidean2DSpace) {
            val ox = Line(zero, Vector2D(1.0, 0.0))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                assertVectorEquals(Vector2D(x, 0.0), projectToLine(Vector2D(x, y), ox))
            }
        }
    }

    @Test
    fun projectionIntoOy() {
        with(Euclidean2DSpace) {
            val line = Line(zero, Vector2D(0.0, 1.0))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                assertVectorEquals(Vector2D(0.0, y), projectToLine(Vector2D(x, y), line))
            }
        }
    }

    @Test
    fun projectionIntoYEqualsX() {
        with(Euclidean2DSpace) {
            val line = Line(zero, Vector2D(1.0, 1.0))

            assertVectorEquals(zero, projectToLine(zero, line))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val d = (y - x) / 2.0
                assertVectorEquals(Vector2D(x + d, y - d), projectToLine(Vector2D(x, y), line))
            }
        }
    }

    @Test
    fun projectionOntoLine2d() {
        with(Euclidean2DSpace) {
            val a = 5.0
            val b = -3.0
            val c = -15.0
            val line = Line(Vector2D(3.0, 0.0), Vector2D(3.0, 5.0))

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val xProj = (b * (b * x - a * y) - a * c) / (a * a + b * b)
                val yProj = (a * (-b * x + a * y) - b * c) / (a * a + b * b)
                assertVectorEquals(Vector2D(xProj, yProj), projectToLine(Vector2D(x, y), line))
            }
        }
    }

    @Test
    fun projectionOntoLine3d() {
        val line = Line3D(
            base = Vector3D(1.0, 3.5, 0.07),
            direction = Vector3D(2.0, -0.0037, 11.1111)
        )

        with(Euclidean3DSpace) {
            val testDomain = (-10.0..10.0).generateList(0.15)
            for (x in testDomain) {
                for (y in testDomain) {
                    for (z in testDomain) {
                        val v = Vector3D(x, y, z)
                        val result = projectToLine(v, line)

                        // assert that result is on line
                        assertTrue(isCollinear(result - line.base, line.direction))
                        // assert that PV vector is orthogonal to direction vector
                        assertTrue(isOrthogonal(v - result, line.direction))
                    }
                }
            }
        }
    }
}