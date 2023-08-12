/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.complex.Quaternion
import space.kscience.kmath.complex.QuaternionAlgebra
import space.kscience.kmath.complex.conjugate
import space.kscience.kmath.complex.normalized
import space.kscience.kmath.geometry.euclidean3d.Float32Space3D
import space.kscience.kmath.geometry.euclidean3d.Float32Vector3D
import space.kscience.kmath.geometry.euclidean3d.theta
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.pow

public operator fun Quaternion.times(other: Quaternion): Quaternion = QuaternionAlgebra.multiply(this, other)

public operator fun Quaternion.div(other: Quaternion): Quaternion = QuaternionAlgebra.divide(this, other)

public fun Quaternion.power(number: Number): Quaternion = QuaternionAlgebra.power(this, number)

/**
 * Linear interpolation between [from] and [to] in spherical space
 */
public fun QuaternionAlgebra.slerp(from: Quaternion, to: Quaternion, fraction: Double): Quaternion =
    (to / from).pow(fraction) * from

public fun QuaternionAlgebra.angleBetween(q1: Quaternion, q2: Quaternion): Angle = (q1.conjugate * q2).theta

public val Quaternion.inclination: Radians get() = asin(2 * (w * y - x * z)).radians

public val Quaternion.azimuth: Angle get() = atan2(2 * (w * z + x * y), 1 - 2 * (y.pow(2) + z.pow(2))).radians.normalized()

public infix fun Quaternion.dot(other: Quaternion): Double = w * other.w + x * other.x + y * other.y + z * other.z


private fun Quaternion.normalizedToEuler(): Float32Vector3D {
    val roll = atan2(2 * y * w + 2 * x * z, 1 - 2 * y * y - 2 * z * z);
    val pitch = atan2(2 * x * w - 2 * y * z, 1 - 2 * x * x - 2 * z * z);
    val yaw = asin(2 * x * y + 2 * z * w);

    return Float32Vector3D(roll, pitch, yaw)
}

/**
 * Quaternion to XYZ Cardan angles
 */
public fun Quaternion.toEuler(): Float32Vector3D = if (QuaternionAlgebra.norm(this) == 0.0) {
    Float32Space3D.zero
} else {
    normalized().normalizedToEuler()
}