/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.plus
import space.kscience.kmath.operations.times
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Euclidean2DSpaceTest {
    @Test
    fun zero() {
        assertVectorEquals(Vector2D(0.0, 0.0), Euclidean2DSpace.zero)
    }

    @Test
    fun norm() {
        with(Euclidean2DSpace) {
            assertEquals(0.0, zero.norm())
            assertEquals(1.0, Vector2D(1.0, 0.0).norm())
            assertEquals(sqrt(2.0), Vector2D(1.0, 1.0).norm())
            assertEquals(sqrt(5.002001), Vector2D(-2.0, 1.001).norm())
        }
    }

    @Test
    fun dotProduct() {
        with(Euclidean2DSpace) {
            assertEquals(0.0, zero dot zero)
            assertEquals(0.0, zero dot Vector2D(1.0, 0.0))
            assertEquals(0.0, Vector2D(-2.0, 0.001) dot zero)
            assertEquals(0.0, Vector2D(1.0, 0.0) dot Vector2D(0.0, 1.0))

            assertEquals(1.0, Vector2D(1.0, 0.0) dot Vector2D(1.0, 0.0))
            assertEquals(-2.0, Vector2D(0.0, 1.0) dot Vector2D(1.0, -2.0))
            assertEquals(2.0, Vector2D(1.0, 1.0) dot Vector2D(1.0, 1.0))
            assertEquals(4.001001, Vector2D(-2.0, 1.001) dot Vector2D(-2.0, 0.001))

            assertEquals(-4.998, Vector2D(1.0, 2.0) dot Vector2D(-5.0, 0.001))
        }
    }

    @Test
    fun add() {
        with(Euclidean2DSpace) {
            assertVectorEquals(
                Vector2D(-2.0, 0.001),
                Vector2D(-2.0, 0.001) + zero
            )
            assertVectorEquals(
                Vector2D(-3.0, 3.001),
                Vector2D(2.0, 3.0) + Vector2D(-5.0, 0.001)
            )
        }
    }

    @Test
    fun multiply() {
        with(Euclidean2DSpace) {
            assertVectorEquals(Vector2D(-4.0, 0.0), Vector2D(-2.0, 0.0) * 2)
            assertVectorEquals(Vector2D(4.0, 0.0), Vector2D(-2.0, 0.0) * -2)
            assertVectorEquals(Vector2D(300.0, 0.0003), Vector2D(100.0, 0.0001) * 3)
        }
    }
}
