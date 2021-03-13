package space.kscience.kmath.real

import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.nd.Matrix


/**
 * Optimized dot product for real matrices
 */
public infix fun Matrix<Double>.dot(other: Matrix<Double>): Matrix<Double> = LinearSpace.real.run{
    this@dot dot other
}