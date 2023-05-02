/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.operations.sum

/**
 * Use double pass Simpson rule integration with a fixed number of points.
 * Requires [UnivariateIntegrandRanges] or [IntegrationRange] and [IntegrandMaxCalls].
 * * [IntegrationRange]&mdash;the univariate range of integration. By default, uses `0..1` interval.
 * * [IntegrandMaxCalls]&mdash;the maximum number of function calls during integration. For non-iterative rules, always
 * uses the maximum number of points. By default, uses 10 points.
 */
@UnstableKMathAPI
public class SimpsonIntegrator<T : Any>(
    public val algebra: Field<T>,
) : UnivariateIntegrator<T> {

    private fun integrateRange(
        integrand: UnivariateIntegrand<T>, range: ClosedRange<Double>, numPoints: Int,
    ): T = algebra {
        val h: Double = (range.endInclusive - range.start) / (numPoints - 1)
        val values: List<T> = List(numPoints) { i ->
            integrand.function(range.start + i * h)
        }// equally distributed point

        //TODO don't use list, reassign values instead
        fun simpson(index: Int) = h / 3 * (values[index - 1] + 4 * values[index] + values[index + 1])

        var res = zero
        res += simpson(1) / 1.5 //border points with 1.5 factor
        for (i in 2 until (values.size - 2)) {
            //each half-interval is computed twice, therefore /2
            res += simpson(i) / 2
        }
        res += simpson(values.size - 2) / 1.5 //border points with 1.5 factor
        return res
    }

    override fun process(integrand: UnivariateIntegrand<T>): UnivariateIntegrand<T> {
        val ranges = integrand.getFeature<UnivariateIntegrandRanges>()
        return if (ranges != null) {
            val res = algebra.sum(ranges.ranges.map { integrateRange(integrand, it.first, it.second) })
            integrand + IntegrandValue(res) + IntegrandCallsPerformed(integrand.calls + ranges.ranges.sumOf { it.second })
        } else {
            val numPoints = integrand.getFeature<IntegrandMaxCalls>()?.maxCalls ?: 100
            require(numPoints >= 4) { "Simpson integrator requires at least 4 nodes" }
            val range = integrand.getFeature<IntegrationRange>()?.range ?: 0.0..1.0
            val res = integrateRange(integrand, range, numPoints)
            integrand + IntegrandValue(res) + IntegrandCallsPerformed(integrand.calls + numPoints)
        }
    }
}

@UnstableKMathAPI
public val <T : Any> Field<T>.simpsonIntegrator: SimpsonIntegrator<T> get() = SimpsonIntegrator(this)

/**
 * Use double pass Simpson rule integration with a fixed number of points.
 * Requires [UnivariateIntegrandRanges] or [IntegrationRange] and [IntegrandMaxCalls].
 * * [IntegrationRange]&mdash;the univariate range of integration. By default, uses `0.0..1.0` interval.
 * * [IntegrandMaxCalls]&mdash;the maximum number of function calls during integration. For non-iterative rules, always uses
 * the maximum number of points. By default, uses 10 points.
 */
public object DoubleSimpsonIntegrator : UnivariateIntegrator<Double> {
    private fun integrateRange(
        integrand: UnivariateIntegrand<Double>, range: ClosedRange<Double>, numPoints: Int,
    ): Double {
        val h: Double = (range.endInclusive - range.start) / (numPoints - 1)
        val values = DoubleArray(numPoints) { i ->
            integrand.function(range.start + i * h)
        }// equally distributed point

        fun simpson(index: Int) = h / 3 * (values[index - 1] + 4 * values[index] + values[index + 1])

        var res = 0.0
        res += simpson(1) / 1.5 //border points with 1.5 factor
        for (i in 2 until (values.size - 2)) {
            //each half-interval is computed twice, therefore /2
            res += simpson(i) / 2
        }
        res += simpson(values.size - 2) / 1.5 //border points with 1.5 factor
        return res
    }

    override fun process(integrand: UnivariateIntegrand<Double>): UnivariateIntegrand<Double> {
        val ranges = integrand.getFeature<UnivariateIntegrandRanges>()
        return if (ranges != null) {
            val res = ranges.ranges.sumOf { integrateRange(integrand, it.first, it.second) }
            integrand + IntegrandValue(res) + IntegrandCallsPerformed(integrand.calls + ranges.ranges.sumOf { it.second })
        } else {
            val numPoints = integrand.getFeature<IntegrandMaxCalls>()?.maxCalls ?: 100
            require(numPoints >= 4) { "Simpson integrator requires at least 4 nodes" }
            val range = integrand.getFeature<IntegrationRange>()?.range ?: 0.0..1.0
            val res = integrateRange(integrand, range, numPoints)
            integrand + IntegrandValue(res) + IntegrandCallsPerformed(integrand.calls + numPoints)
        }
    }
}

public val DoubleField.simpsonIntegrator: DoubleSimpsonIntegrator get() = DoubleSimpsonIntegrator