/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.complex.Quaternion
import space.kscience.kmath.complex.QuaternionAlgebra
import space.kscience.kmath.complex.conjugate
import space.kscience.kmath.geometry.euclidean3d.theta

public operator fun Quaternion.times(other: Quaternion): Quaternion = QuaternionAlgebra.multiply(this, other)

public operator fun Quaternion.div(other: Quaternion): Quaternion = QuaternionAlgebra.divide(this, other)

public fun Quaternion.power(number: Number): Quaternion = QuaternionAlgebra.power(this, number)

/**
 * Linear interpolation between [from] and [to] in spherical space
 */
public fun QuaternionAlgebra.slerp(from: Quaternion, to: Quaternion, fraction: Double): Quaternion =
    (to / from).pow(fraction) * from

/**
 * Scalar angle between two quaternions
 */
public fun QuaternionAlgebra.angleBetween(q1: Quaternion, q2: Quaternion): Angle = (q1.conjugate * q2).theta

/**
 * Euclidean product of two quaternions
 */
public infix fun Quaternion.dot(other: Quaternion): Double = w * other.w + x * other.x + y * other.y + z * other.z