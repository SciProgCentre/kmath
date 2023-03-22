/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.test.Test
import kotlin.test.assertEquals

internal class Euclidean3DSpaceTest {
    @Test
    fun zero() {
        assertVectorEquals(Euclidean3DSpace.vector(0.0, 0.0, 0.0), Euclidean3DSpace.zero)
    }

    @Test
    fun distance() {
        with(Euclidean3DSpace) {
            assertEquals(0.0, zero.distanceTo(zero))
            assertEquals(1.0, zero.distanceTo(vector(1.0, 0.0, 0.0)))
            assertEquals(kotlin.math.sqrt(5.000001), vector(1.0, -2.0, 0.001).distanceTo(zero))
            assertEquals(0.0, vector(1.0, -2.0, 0.001).distanceTo(vector(1.0, -2.0, 0.001)))
            assertEquals(0.0, vector(1.0, 0.0, 0.0).distanceTo(vector(1.0, 0.0, 0.0)))
            assertEquals(kotlin.math.sqrt(2.0), vector(1.0, 0.0, 0.0).distanceTo(vector(1.0, 1.0, 1.0)))
            assertEquals(3.1622778182822584, vector(0.0, 1.0, 0.0).distanceTo(vector(1.0, -2.0, 0.001)))
            assertEquals(0.0, vector(1.0, -2.0, 0.001).distanceTo(vector(1.0, -2.0, 0.001)))
            assertEquals(9.695050335093676, vector(1.0, 2.0, 3.0).distanceTo(vector(7.0, -5.0, 0.001)))
        }
    }

    @Test
    fun norm() {
        with(Euclidean3DSpace) {
            assertEquals(0.0, zero.norm())
            assertEquals(1.0, vector(1.0, 0.0, 0.0).norm())
            assertEquals(kotlin.math.sqrt(3.0), vector(1.0, 1.0, 1.0).norm())
            assertEquals(kotlin.math.sqrt(5.000001), vector(1.0, -2.0, 0.001).norm())
        }
    }

    @Test
    fun dotProduct() {
        with(Euclidean3DSpace) {
            assertEquals(0.0, zero dot zero)
            assertEquals(0.0, zero dot vector(1.0, 0.0, 0.0))
            assertEquals(0.0, vector(1.0, -2.0, 0.001) dot zero)

            assertEquals(1.0, vector(1.0, 0.0, 0.0) dot vector(1.0, 0.0, 0.0))
            assertEquals(1.0, vector(1.0, 0.0, 0.0) dot vector(1.0, 1.0, 1.0))
            assertEquals(-2.0, vector(0.0, 1.0, 0.0) dot vector(1.0, -2.0, 0.001))
            assertEquals(3.0, vector(1.0, 1.0, 1.0) dot vector(1.0, 1.0, 1.0))
            assertEquals(5.000001, vector(1.0, -2.0, 0.001) dot vector(1.0, -2.0, 0.001))

            assertEquals(-2.997, vector(1.0, 2.0, 3.0) dot vector(7.0, -5.0, 0.001))
        }
    }

    @Test
    fun add() = with(Euclidean3DSpace) {
        assertVectorEquals(
            vector(1.0, -2.0, 0.001),
            vector(1.0, -2.0, 0.001) + zero
        )
        assertVectorEquals(
            vector(8.0, -3.0, 3.001),
            vector(1.0, 2.0, 3.0) + vector(7.0, -5.0, 0.001)
        )
    }

    @Test
    fun multiply() = with(Euclidean3DSpace) {
        assertVectorEquals(vector(2.0, -4.0, 0.0), vector(1.0, -2.0, 0.0) * 2)
    }

    @Test
    fun vectorProduct() = with(Euclidean3DSpace) {
        assertVectorEquals(zAxis, vectorProduct(xAxis, yAxis))
        assertVectorEquals(zAxis, xAxis cross yAxis)
        assertVectorEquals(-zAxis, vectorProduct(yAxis, xAxis))
    }

    @Test
    fun doubleVectorProduct() = with(Euclidean3DSpace) {
        val a = vector(1, 2, -3)
        val b = vector(-1, 0, 1)
        val c = vector(4, 5, 6)

        val res = a cross (b cross c)
        val expected = b * (a dot c) - c * (a dot b)
        assertVectorEquals(expected, res)
    }

}
