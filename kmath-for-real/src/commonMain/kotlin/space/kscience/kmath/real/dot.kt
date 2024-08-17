/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.structures.Float64


/**
 * Optimized dot product for real matrices
 */
public infix fun Matrix<Float64>.dot(other: Matrix<Float64>): Matrix<Float64> = Double.algebra.linearSpace.run {
    this@dot dot other
}