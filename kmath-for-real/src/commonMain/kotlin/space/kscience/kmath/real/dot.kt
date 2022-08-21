/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.operations.algebra


/**
 * Optimized dot product for real matrices
 */
public infix fun Matrix<Double>.dot(other: Matrix<Double>): Matrix<Double> = Double.algebra.linearSpace.run {
    this@dot dot other
}