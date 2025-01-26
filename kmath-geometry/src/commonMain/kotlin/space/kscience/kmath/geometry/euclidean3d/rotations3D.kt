/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry.euclidean3d

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.complex.*
import space.kscience.kmath.geometry.*
import space.kscience.kmath.linear.*
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Float64
import kotlin.math.*

public operator fun Quaternion.times(other: Quaternion): Quaternion = QuaternionAlgebra.multiply(this, other)

public operator fun Quaternion.div(other: Quaternion): Quaternion = QuaternionAlgebra.divide(this, other)

public fun Quaternion.power(number: Number): Quaternion = QuaternionAlgebra.power(this, number)

/**
 * Linear interpolation between [from] and [to] in spherical space
 */
public fun QuaternionAlgebra.slerp(from: Quaternion, to: Quaternion, fraction: Double): Quaternion =
    (to / from).pow(fraction) * from

public fun QuaternionAlgebra.angleBetween(q1: Quaternion, q2: Quaternion): Angle = (q1.conjugate * q2).theta

public infix fun Quaternion.dot(other: Quaternion): Double = w * other.w + x * other.x + y * other.y + z * other.z


/**
 * Represent a vector as quaternion with zero a rotation angle.
 */
internal fun Vector3D<Float64>.asQuaternion(): Quaternion = Quaternion(0.0, x, y, z)


/**
 * Angle in radians denoted by this quaternion rotation
 */
public val Quaternion.theta: Radians get() = (kotlin.math.acos(normalized().w) * 2).radians

/**
 * Create a normalized Quaternion from rotation angle and rotation vector
 */
public fun Quaternion.Companion.fromRotation(theta: Angle, vector: Float64Vector3D): Quaternion {
    val s = sin(theta / 2)
    val c = cos(theta / 2)
    val norm = with(Float64Space3D) { vector.norm() }
    return Quaternion(c, vector.x * s / norm, vector.y * s / norm, vector.z * s / norm)
}

/**
 * An axis of quaternion rotation
 */
public val Quaternion.vector: Float64Vector3D
    get() {
        return object : Float64Vector3D {
            private val sint2 = sqrt(1 - w * w)
            override val x: Double get() = this@vector.x / sint2
            override val y: Double get() = this@vector.y / sint2
            override val z: Double get() = this@vector.z / sint2
            override fun toString(): String = listOf(x, y, z).toString()
        }
    }

/**
 * Rotate a vector in a [Float64Space3D] with [quaternion]
 */
public fun Float64Space3D.rotate(vector: Vector3D<Float64>, quaternion: Quaternion): Float64Vector3D =
    with(QuaternionAlgebra) {
        val p = vector.asQuaternion()
        (quaternion * p * quaternion.reciprocal).vector
    }

/**
 * Use a composition of quaternions to create a rotation
 */
@UnstableKMathAPI
public fun Float64Space3D.rotate(
    vector: Float64Vector3D,
    composition: QuaternionAlgebra.() -> Quaternion,
): Float64Vector3D =
    rotate(vector, QuaternionAlgebra.composition())

/**
 * Rotate a [Float64] vector in 3D space with a rotation matrix
 */
public fun Float64Space3D.rotate(vector: Float64Vector3D, matrix: Matrix<Float64>): Vector3D<Float64> {
    require(matrix.colNum == 3 && matrix.rowNum == 3) { "Square 3x3 rotation matrix is required" }
    return with(linearSpace) { (matrix dot vector).asVector3D() }
}

/**
 * Convert a [Quaternion] to a rotation matrix
 */
@OptIn(UnstableKMathAPI::class)
public fun Quaternion.toRotationMatrix(
    linearSpace: LinearSpace<Double, *> = Float64Field.linearSpace,
): Matrix<Float64> {
    val s = QuaternionAlgebra.norm(this).pow(-2)
    return linearSpace.MatrixBuilder(3, 3).fill(
        1.0 - 2 * s * (y * y + z * z), 2 * s * (x * y - z * w), 2 * s * (x * z + y * w),
        2 * s * (x * y + z * w), 1.0 - 2 * s * (x * x + z * z), 2 * s * (y * z - x * w),
        2 * s * (x * z - y * w), 2 * s * (y * z + x * w), 1.0 - 2 * s * (x * x + y * y)
    )
}

/**
 * Convert a quaternion to a rotation matrix
 *
 * taken from https://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
 */
public fun Quaternion.Companion.fromRotationMatrix(matrix: Matrix<Float64>): Quaternion {
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

public enum class RotationOrder {
    // proper Euler
    XZX,
    XYX,
    YXY,
    YZY,
    ZYZ,
    ZXZ,

    //Tait–Bryan
    XZY,
    XYZ,
    YXZ,
    YZX,
    ZYX,
    ZXY
}

/**
 * Create a quaternion from Euler angles
 *
 * Based on https://github.com/mrdoob/three.js/blob/master/src/math/Quaternion.js
 */
public fun Quaternion.Companion.fromEuler(
    a: Angle,
    b: Angle,
    c: Angle,
    rotationOrder: RotationOrder,
): Quaternion {
    val c1 = cos(a / 2)
    val c2 = cos(b / 2)
    val c3 = cos(c / 2)

    val s1 = sin(a / 2)
    val s2 = sin(b / 2)
    val s3 = sin(c / 2)

    return when (rotationOrder) {

        RotationOrder.XYZ -> Quaternion(
            c1 * c2 * c3 - s1 * s2 * s3,
            s1 * c2 * c3 + c1 * s2 * s3,
            c1 * s2 * c3 - s1 * c2 * s3,
            c1 * c2 * s3 + s1 * s2 * c3
        )

        RotationOrder.YXZ -> Quaternion(
            c1 * c2 * c3 + s1 * s2 * s3,
            s1 * c2 * c3 + c1 * s2 * s3,
            c1 * s2 * c3 - s1 * c2 * s3,
            c1 * c2 * s3 - s1 * s2 * c3
        )

        RotationOrder.ZXY -> Quaternion(
            c1 * c2 * c3 - s1 * s2 * s3,
            s1 * c2 * c3 - c1 * s2 * s3,
            c1 * s2 * c3 + s1 * c2 * s3,
            c1 * c2 * s3 + s1 * s2 * c3
        )


        RotationOrder.ZYX -> Quaternion(
            c1 * c2 * c3 + s1 * s2 * s3,
            s1 * c2 * c3 - c1 * s2 * s3,
            c1 * s2 * c3 + s1 * c2 * s3,
            c1 * c2 * s3 - s1 * s2 * c3
        )

        RotationOrder.YZX -> Quaternion(
            c1 * c2 * c3 - s1 * s2 * s3,
            s1 * c2 * c3 + c1 * s2 * s3,
            c1 * s2 * c3 + s1 * c2 * s3,
            c1 * c2 * s3 - s1 * s2 * c3
        )

        RotationOrder.XZY -> Quaternion(
            c1 * c2 * c3 + s1 * s2 * s3,
            s1 * c2 * c3 - c1 * s2 * s3,
            c1 * s2 * c3 - s1 * c2 * s3,
            c1 * c2 * s3 + s1 * s2 * c3
        )

        else -> TODO("Proper Euler rotation orders are not supported yet")
    }
}

/**
 * A vector consisting of angles
 */
public data class AngleVector(override val x: Angle, override val y: Angle, override val z: Angle) : Vector3D<Angle> {
    public companion object
}

public fun Quaternion.Companion.fromEuler(
    angles: AngleVector,
    rotationOrder: RotationOrder,
): Quaternion = fromEuler(angles.x, angles.y, angles.z, rotationOrder)

/**
 * Based on https://github.com/mrdoob/three.js/blob/master/src/math/Euler.js
 */
public fun AngleVector.Companion.fromRotationMatrix(
    matrix: Matrix<Float64>,
    rotationOrder: RotationOrder,
    gimbaldLockThreshold: Double = 0.9999999,
): AngleVector = when (rotationOrder) {

    RotationOrder.XYZ -> {
        if (abs(matrix[0, 2]) < gimbaldLockThreshold) {
            AngleVector(
                atan2(-matrix[1, 2], matrix[2, 2]).radians,
                asin(matrix[0, 2].coerceIn(-1.0, 1.0)).radians,
                atan2(-matrix[0, 1], matrix[0, 0]).radians
            )

        } else {
            AngleVector(
                atan2(matrix[2, 1], matrix[1, 1]).radians,
                asin(matrix[0, 2].coerceIn(-1.0, 1.0)).radians,
                Angle.zero
            )
        }
    }

    RotationOrder.YXZ -> {
        if (abs(matrix[1, 2]) < gimbaldLockThreshold) {
            AngleVector(
                x = asin(-matrix[1, 2].coerceIn(-1.0, 1.0)).radians,
                y = atan2(matrix[0, 2], matrix[2, 2]).radians,
                z = atan2(matrix[1, 0], matrix[1, 1]).radians,
            )
        } else {
            AngleVector(
                x = asin(-matrix[1, 2].coerceIn(-1.0, 1.0)).radians,
                y = atan2(-matrix[2, 0], matrix[0, 0]).radians,
                z = Angle.zero,
            )

        }
    }

    RotationOrder.ZXY -> {
        if (abs(matrix[2, 1]) < gimbaldLockThreshold) {
            AngleVector(
                x = asin(matrix[2, 1].coerceIn(-1.0, 1.0)).radians,
                y = atan2(-matrix[2, 0], matrix[2, 2]).radians,
                z = atan2(-matrix[0, 1], matrix[1, 1]).radians,
            )

        } else {
            AngleVector(
                x = asin(matrix[2, 1].coerceIn(-1.0, 1.0)).radians,
                y = Angle.zero,
                z = atan2(matrix[1, 0], matrix[0, 0]).radians,
            )
        }
    }

    RotationOrder.ZYX -> {
        if (abs(matrix[2, 0]) < gimbaldLockThreshold) {
            AngleVector(
                x = atan2(matrix[2, 1], matrix[2, 2]).radians,
                y = asin(-matrix[2, 0].coerceIn(-1.0, 1.0)).radians,
                z = atan2(matrix[1, 0], matrix[0, 0]).radians,
            )
        } else {
            AngleVector(
                x = Angle.zero,
                y = asin(-matrix[2, 0].coerceIn(-1.0, 1.0)).radians,
                z = atan2(-matrix[0, 1], matrix[1, 1]).radians,
            )
        }
    }

    RotationOrder.YZX -> {
        if (abs(matrix[1, 0]) < gimbaldLockThreshold) {
            AngleVector(
                x = atan2(-matrix[1, 2], matrix[1, 1]).radians,
                y = atan2(-matrix[2, 0], matrix[0, 0]).radians,
                z = asin(matrix[1, 0].coerceIn(-1.0, 1.0)).radians,
            )
        } else {
            AngleVector(
                x = Angle.zero,
                y = atan2(matrix[0, 2], matrix[2, 2]).radians,
                z = asin(matrix[1, 0].coerceIn(-1.0, 1.0)).radians,
            )
        }
    }

    RotationOrder.XZY -> {
        if (abs(matrix[0, 1]) < gimbaldLockThreshold) {
            AngleVector(
                x = atan2(matrix[2, 1], matrix[1, 1]).radians,
                y = atan2(matrix[0, 2], matrix[0, 0]).radians,
                z = asin(-matrix[0, 1].coerceIn(-1.0, 1.0)).radians,
            )
        } else {
            AngleVector(
                x = atan2(-matrix[1, 2], matrix[2, 2]).radians,
                y = Angle.zero,
                z = asin(-matrix[0, 1].coerceIn(-1.0, 1.0)).radians,
            )
        }
    }

    else -> TODO("Proper Euler rotation orders are not supported yet")
}