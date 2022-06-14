/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.complex.Quaternion
import space.kscience.kmath.testutils.assertBufferEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class RotationTest {

    @Test
    fun rotations() = with(Euclidean3DSpace) {
        val vector = Vector3D(1.0, 1.0, 1.0)
        val q = Quaternion(1.0, 2.0, -3.0, 4.0)
        val rotatedByQ = rotate(vector, q)
        val matrix = q.toRotationMatrix()
        val rotatedByM = rotate(vector,matrix)

        assertBufferEquals(rotatedByQ, rotatedByM, 1e-4)
    }

    @Test
    fun rotationConversion() {

        val q = Quaternion(1.0, 2.0, -3.0, 4.0)

        val matrix = q.toRotationMatrix()

        assertEquals(q, Quaternion.fromRotationMatrix(matrix))
    }
}