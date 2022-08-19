/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.complex.Quaternion
import space.kscience.kmath.complex.QuaternionField
import space.kscience.kmath.complex.reciprocal
import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.linear.matrix
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import kotlin.math.pow
import kotlin.math.sqrt

internal fun Vector3D.toQuaternion(): Quaternion = Quaternion(0.0, x, y, z)

/**
 * Angle in radians denoted by this quaternion rotation
 */
public val Quaternion.theta: Double get() = kotlin.math.acos(w) * 2

/**
 * An axis of quaternion rotation
 */
public val Quaternion.vector: Vector3D
    get() {
        val sint2 = sqrt(1 - w * w)

        return object : Vector3D {
            override val x: Double get() = this@vector.x / sint2
            override val y: Double get() = this@vector.y / sint2
            override val z: Double get() = this@vector.z / sint2
            override fun toString(): String = listOf(x, y, z).toString()
        }
    }

/**
 * Rotate a vector in a [Euclidean3DSpace]
 */
public fun Euclidean3DSpace.rotate(vector: Vector3D, q: Quaternion): Vector3D = with(QuaternionField) {
    val p = vector.toQuaternion()
    (q * p * q.reciprocal).vector
}

/**
 * Use a composition of quaternions to create a rotation
 */
public fun Euclidean3DSpace.rotate(vector: Vector3D, composition: QuaternionField.() -> Quaternion): Vector3D =
    rotate(vector, QuaternionField.composition())

public fun Euclidean3DSpace.rotate(vector: Vector3D, matrix: Matrix<Double>): Vector3D {
    require(matrix.colNum == 3 && matrix.rowNum == 3) { "Square 3x3 rotation matrix is required" }
    return with(DoubleField.linearSpace) { matrix.dot(vector).asVector3D() }
}

/**
 * Convert a [Quaternion] to a rotation matrix
 */
@OptIn(UnstableKMathAPI::class)
public fun Quaternion.toRotationMatrix(
    linearSpace: LinearSpace<Double, *> = DoubleField.linearSpace,
): Matrix<Double> {
    val s = QuaternionField.norm(this).pow(-2)
    return linearSpace.matrix(3, 3)(
        1.0 - 2 * s * (y * y + z * z), 2 * s * (x * y - z * w), 2 * s * (x * z + y * w),
        2 * s * (x * y + z * w), 1.0 - 2 * s * (x * x + z * z), 2 * s * (y * z - x * w),
        2 * s * (x * z - y * w), 2 * s * (y * z + x * w), 1.0 - 2 * s * (x * x + y * y)
    )
}

/**
 * taken from https://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
 */
public fun Quaternion.Companion.fromRotationMatrix(matrix: Matrix<Double>): Quaternion {
    require(matrix.colNum == 3 && matrix.rowNum == 3) { "Rotation matrix should be 3x3 but is ${matrix.rowNum}x${matrix.colNum}" }
    val trace = matrix[0, 0] + matrix[1, 1] + matrix[2, 2]

    return if (trace > 0) {
        val s = sqrt(trace + 1.0) * 2 // S=4*qw
        Quaternion(
            w = 0.25 * s,
            x = (matrix[2, 1] - matrix[1, 2]) / s,
            y = (matrix[0, 2] - matrix[2, 0]) / s,
            z = (matrix[1, 0] - matrix[0, 1]) / s,
        )
    } else if ((matrix[0, 0] > matrix[1, 1]) && (matrix[0, 0] > matrix[2, 2])) {
        val s = sqrt(1.0 + matrix[0, 0] - matrix[1, 1] - matrix[2, 2]) * 2 // S=4*qx
        Quaternion(
            w = (matrix[2, 1] - matrix[1, 2]) / s,
            x = 0.25 * s,
            y = (matrix[0, 1] + matrix[1, 0]) / s,
            z = (matrix[0, 2] + matrix[2, 0]) / s,
        )
    } else if (matrix[1, 1] > matrix[2, 2]) {
        val s = sqrt(1.0 + matrix[1, 1] - matrix[0, 0] - matrix[2, 2]) * 2 // S=4*qy
        Quaternion(
            w = (matrix[0, 2] - matrix[2, 0]) / s,
            x = (matrix[0, 1] + matrix[1, 0]) / s,
            y = 0.25 * s,
            z = (matrix[1, 2] + matrix[2, 1]) / s,
        )
    } else {
        val s = sqrt(1.0 + matrix[2, 2] - matrix[0, 0] - matrix[1, 1]) * 2 // S=4*qz
        Quaternion(
            w = (matrix[1, 0] - matrix[0, 1]) / s,
            x = (matrix[0, 2] + matrix[2, 0]) / s,
            y = (matrix[1, 2] + matrix[2, 1]) / s,
            z = 0.25 * s,
        )
    }
}