/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.interpolation

import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.functions.PiecewisePolynomial
import space.kscience.kmath.functions.Polynomial
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.invoke

@OptIn(UnstableKMathAPI::class)
internal fun <T : Comparable<T>> insureSorted(points: XYColumnarData<*, T, *>) {
    for (i in 0 until points.size - 1)
        require(points.x[i + 1] > points.x[i]) { "Input data is not sorted at index $i" }
}

/**
 * Reference JVM implementation: https://github.com/apache/commons-math/blob/master/src/main/java/org/apache/commons/math4/analysis/interpolation/LinearInterpolator.java
 */
public class LinearInterpolator<T : Comparable<T>>(override val algebra: Field<T>) : PolynomialInterpolator<T> {

    @OptIn(UnstableKMathAPI::class)
    override fun interpolatePolynomials(points: XYColumnarData<T, T, T>): PiecewisePolynomial<T> = algebra {
        require(points.size > 0) { "Point array should not be empty" }
        insureSorted(points)

        PiecewisePolynomial(points.x[0]) {
            for (i in 0 until points.size - 1) {
                val slope = (points.y[i + 1] - points.y[i]) / (points.x[i + 1] - points.x[i])
                val const = points.y[i] - slope * points.x[i]
                val polynomial = Polynomial(const, slope)
                putRight(points.x[i + 1], polynomial)
            }
        }
    }
}

public val <T : Comparable<T>> Field<T>.linearInterpolator: LinearInterpolator<T>
    get() = LinearInterpolator(this)
