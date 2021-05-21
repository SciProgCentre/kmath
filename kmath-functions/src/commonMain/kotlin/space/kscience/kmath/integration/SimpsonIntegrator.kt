/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.operations.Field
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory

/**
 * [IntegrationRange] - the univariate range of integration. By default uses 0..1 interval.
 * [IntegrandMaxCalls] - the maximum number of function calls during integration. For non-iterative rules, always uses the maximum number of points. By default uses 10 points.
 */
public class SimpsonIntegrator<T : Any>(
    public val algebra: Field<T>,
    public val bufferFactory: BufferFactory<T>,
) : UnivariateIntegrator<T> {
    override fun integrate(integrand: UnivariateIntegrand<T>): UnivariateIntegrand<T> = with(algebra) {
        val numPoints = integrand.getFeature<IntegrandMaxCalls>()?.maxCalls ?: 100
        require(numPoints >= 4)
        val range = integrand.getFeature<IntegrationRange>()?.range ?: 0.0..1.0
        val h: Double = (range.endInclusive - range.start) / (numPoints - 1)
        val points: Buffer<T> = bufferFactory(numPoints) { i ->
            integrand.function(range.start + i * h)
        }// equally distributed point

        fun simpson(index: Int) = h / 3 * (points[index - 1] + 4 * points[index] + points[index + 1])
        var res = zero
        res += simpson(1) / 1.5 //border points with 1.5 factor
        for (i in 2 until (points.size - 2)) {
            res += simpson(i) / 2
        }
        res += simpson(points.size - 2) / 1.5 //border points with 1.5 factor
        return integrand + IntegrandValue(res) + IntegrandCallsPerformed(integrand.calls + points.size)
    }
}

public inline val <reified T : Any> Field<T>.simpsonIntegrator: SimpsonIntegrator<T>
    get() = SimpsonIntegrator(this, Buffer.Companion::auto)