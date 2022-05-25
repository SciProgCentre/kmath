/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.interpolation

import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.functions.PiecewisePolynomial
import space.kscience.kmath.functions.Polynomial
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.MutableBufferFactory

/**
 * Generic spline interpolator. Not recommended for performance critical places, use platform-specific and type
 * specific ones.
 *
 * Based on
 * https://github.com/apache/commons-math/blob/eb57d6d457002a0bb5336d789a3381a24599affe/src/main/java/org/apache/commons/math4/analysis/interpolation/SplineInterpolator.java
 */
public class SplineInterpolator<T : Comparable<T>>(
    override val algebra: Field<T>,
    public val bufferFactory: MutableBufferFactory<T>,
) : PolynomialInterpolator<T> {
    //TODO possibly optimize zeroed buffers

    @OptIn(UnstableKMathAPI::class)
    override fun interpolatePolynomials(points: XYColumnarData<T, T, T>): PiecewisePolynomial<T> = algebra {
        require(points.size >= 3) { "Can't use spline interpolator with less than 3 points" }
        insureSorted(points)
        // Number of intervals.  The number of data points is n + 1.
        val n = points.size - 1
        // Differences between knot points
        val h = bufferFactory(n) { i -> points.x[i + 1] - points.x[i] }
        val mu = bufferFactory(n) { zero }
        val z = bufferFactory(n + 1) { zero }

        for (i in 1 until n) {
            val g = 2.0 * (points.x[i + 1] - points.x[i - 1]) - h[i - 1] * mu[i - 1]
            mu[i] = h[i] / g
            z[i] =
                ((points.y[i + 1] * h[i - 1] - points.y[i] * (points.x[i + 1] - points.x[i - 1]) + points.y[i - 1] * h[i]) * 3.0 /
                        (h[i - 1] * h[i]) - h[i - 1] * z[i - 1]) / g
        }

        // cubic spline coefficients --  b is linear, c quadratic, d is cubic (original y's are constants)

        PiecewisePolynomial(points.x[points.size - 1]) {
            var cOld = zero

            for (j in n - 1 downTo 0) {
                val c = z[j] - mu[j] * cOld
                val a = points.y[j]
                val b = (points.y[j + 1] - points.y[j]) / h[j] - h[j] * (cOld + 2.0 * c) / 3.0
                val d = (cOld - c) / (3.0 * h[j])
                val x0 = points.x[j]
                val x02 = x0 * x0
                val x03 = x02 * x0
                //Shift coefficients to represent absolute polynomial instead of one with an offset
                val polynomial = Polynomial(
                    a - b * x0 + c * x02 - d * x03,
                    b - 2 * c * x0 + 3 * d * x02,
                    c - 3 * d * x0,
                    d
                )
                cOld = c
                putLeft(x0, polynomial)
            }
        }
    }
}


public fun <T : Comparable<T>> Field<T>.splineInterpolator(
    bufferFactory: MutableBufferFactory<T>,
): SplineInterpolator<T> = SplineInterpolator(this, bufferFactory)

public val DoubleField.splineInterpolator: SplineInterpolator<Double>
    get() = SplineInterpolator(this, ::DoubleBuffer)