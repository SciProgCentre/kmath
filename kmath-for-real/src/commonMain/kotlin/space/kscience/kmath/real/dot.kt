/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.linear.Matrix


/**
 * Optimized dot product for real matrices
 */
public infix fun Matrix<Double>.dot(other: Matrix<Double>): Matrix<Double> = LinearSpace.double.run {
    this@dot dot other
}