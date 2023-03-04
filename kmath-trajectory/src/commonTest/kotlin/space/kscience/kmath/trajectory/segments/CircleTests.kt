/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.segments

import space.kscience.kmath.geometry.Circle2D
import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.circumference
import kotlin.test.Test
import kotlin.test.assertEquals

class CircleTests {

    @Test
    fun arcTest() {
        val center = Euclidean2DSpace.vector(0.0, 0.0)
        val radius = 2.0
        val expectedCircumference = 12.56637
        val circle = Circle2D(center, radius)
        assertEquals(expectedCircumference, circle.circumference, 1e-4)
    }
}
