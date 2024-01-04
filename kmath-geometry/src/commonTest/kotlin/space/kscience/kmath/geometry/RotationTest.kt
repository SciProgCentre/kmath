/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.complex.Quaternion
import space.kscience.kmath.complex.normalized
import space.kscience.kmath.geometry.euclidean3d.*
import space.kscience.kmath.structures.Float64Buffer
import space.kscience.kmath.testutils.assertBufferEquals
import kotlin.test.Test

class RotationTest {

    @Test
    fun differentRotations() = with(Float64Space3D) {
        val vector = vector(1.0, 1.0, 1.0)
        val q = Quaternion(1.0, 2.0, -3.0, 4.0).normalized()
        val rotatedByQ = rotate(vector, q)
        val matrix = q.toRotationMatrix()
        val rotatedByM = rotate(vector, matrix)

        assertBufferEquals(rotatedByQ, rotatedByM, 1e-4)
    }

    @Test
    fun matrixConversion() {

        val q = Quaternion(1.0, 2.0, -3.0, 4.0).normalized()

        val matrix = q.toRotationMatrix()

        assertBufferEquals(q, Quaternion.fromRotationMatrix(matrix))
    }

    @Test
    fun fromRotation() {
        val q = Quaternion.fromRotation(0.3.radians, Float64Space3D.vector(1.0, 1.0, 1.0))

        assertBufferEquals(Float64Buffer(0.9887711, 0.0862781, 0.0862781, 0.0862781), q)
    }

    @Test
    fun fromEuler() {
        val q = Quaternion.fromEuler(0.1.radians, 0.2.radians, 0.3.radians, RotationOrder.ZXY)
        assertBufferEquals(Float64Buffer(0.9818562, 0.0342708, 0.1060205, 0.1534393), q)

        val q1 = Quaternion.fromEuler(0.1.radians, 0.2.radians, 0.3.radians, RotationOrder.XYZ)
        assertBufferEquals(Float64Buffer(0.9818562, 0.0640713, 0.0911575, 0.1534393), q1)
    }
}