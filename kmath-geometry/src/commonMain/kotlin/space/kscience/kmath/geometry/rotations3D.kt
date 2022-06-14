/*
 * Copyright 2018-2021 KMath contributors.
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
import space.kscience.kmath.operations.invoke
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
            override val x: Double get() = this@vector.x/sint2
            override val y: Double get() = this@vector.y/sint2
            override val z: Double get() = this@vector.z/sint2
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
 * taken from https://d3cw3dd2w32x2b.cloudfront.net/wp-content/uploads/2015/01/matrix-to-quat.pdf
 */
public fun Quaternion.Companion.fromRotationMatrix(matrix: Matrix<Double>): Quaternion {
    val t: Double
    val q = if (matrix[2, 2] < 0) {
        if (matrix[0, 0] > matrix[1, 1]) {
            t = 1 + matrix[0, 0] - matrix[1, 1] - matrix[2, 2]
            Quaternion(t, matrix[0, 1] + matrix[1, 0], matrix[2, 0] + matrix[0, 2], matrix[1, 2] - matrix[2, 1])
        } else {
            t = 1 - matrix[0, 0] + matrix[1, 1] - matrix[2, 2]
            Quaternion(matrix[0, 1] + matrix[1, 0], t, matrix[1, 2] + matrix[2, 1], matrix[2, 0] - matrix[0, 2])
        }
    } else {
        if (matrix[0, 0] < -matrix[1, 1]) {
            t = 1 - matrix[0, 0] - matrix[1, 1] + matrix[2, 2]
            Quaternion(matrix[2, 0] + matrix[0, 2], matrix[1, 2] + matrix[2, 1], t, matrix[0, 1] - matrix[1, 0])
        } else {
            t = 1 + matrix[0, 0] + matrix[1, 1] + matrix[2, 2]
            Quaternion(matrix[1, 2] - matrix[2, 1], matrix[2, 0] - matrix[0, 2], matrix[0, 1] - matrix[1, 0], t)
        }
    }
    return QuaternionField.invoke { q * (0.5 / sqrt(t)) }
}