/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Euclidean2DSpaceTest {
    @Test
    fun zero() {
        assertVectorEquals(Euclidean2DSpace.vector(0.0, 0.0), Euclidean2DSpace.zero)
    }

    @Test
    fun norm() {
        with(Euclidean2DSpace) {
            assertEquals(0.0, norm(zero))
            assertEquals(1.0, norm(vector(1.0, 0.0)))
            assertEquals(sqrt(2.0), norm(vector(1.0, 1.0)))
            assertEquals(sqrt(5.002001), norm(vector(-2.0, 1.001)))
        }
    }

    @Test
    fun dotProduct() {
        with(Euclidean2DSpace) {
            assertEquals(0.0, zero dot zero)
            assertEquals(0.0, zero dot vector(1.0, 0.0))
            assertEquals(0.0, vector(-2.0, 0.001) dot zero)
            assertEquals(0.0, vector(1.0, 0.0) dot vector(0.0, 1.0))

            assertEquals(1.0, vector(1.0, 0.0) dot vector(1.0, 0.0))
            assertEquals(-2.0, vector(0.0, 1.0) dot vector(1.0, -2.0))
            assertEquals(2.0, vector(1.0, 1.0) dot vector(1.0, 1.0))
            assertEquals(4.001001, vector(-2.0, 1.001) dot vector(-2.0, 0.001))

            assertEquals(-4.998, vector(1.0, 2.0) dot vector(-5.0, 0.001))
        }
    }

    @Test
    fun add() {
        with(Euclidean2DSpace) {
            assertVectorEquals(
                vector(-2.0, 0.001),
                vector(-2.0, 0.001) + zero
            )
            assertVectorEquals(
                vector(-3.0, 3.001),
                vector(2.0, 3.0) + vector(-5.0, 0.001)
            )
        }
    }

    @Test
    fun multiply() {
        with(Euclidean2DSpace) {
            assertVectorEquals(vector(-4.0, 0.0), vector(-2.0, 0.0) * 2)
            assertVectorEquals(vector(4.0, 0.0), vector(-2.0, 0.0) * -2)
            assertVectorEquals(vector(300.0, 0.0003), vector(100.0, 0.0001) * 3)
        }
    }
}
