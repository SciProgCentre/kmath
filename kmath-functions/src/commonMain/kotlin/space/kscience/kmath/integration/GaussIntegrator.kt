/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.integration

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Field
import space.kscience.kmath.structures.*

/**
 * Set of univariate integration ranges. First components correspond to ranges themselves, second components to number of
 * integration nodes per range
 */
public class UnivariateIntegrandRanges(public val ranges: List<Pair<ClosedRange<Double>, Int>>) : IntegrandFeature {
    public constructor(vararg pairs: Pair<ClosedRange<Double>, Int>) : this(pairs.toList())
}

/**
 * A simple one-pass integrator based on Gauss rule
 */
public class GaussIntegrator<T : Any>(
    public val algebra: Field<T>,
) : UnivariateIntegrator<T> {

    private fun buildRule(integrand: UnivariateIntegrand<T>): Pair<Buffer<Double>, Buffer<Double>> {
        val factory = integrand.getFeature<GaussIntegratorRuleFactory>() ?: GaussLegendreRuleFactory
        val predefinedRanges = integrand.getFeature<UnivariateIntegrandRanges>()
        if (predefinedRanges == null || predefinedRanges.ranges.isEmpty()) {
            val numPoints = integrand.getFeature<IntegrandMaxCalls>()?.maxCalls ?: 100
            val range = integrand.getFeature<IntegrationRange>()?.range ?: 0.0..1.0
            return factory.build(numPoints, range)
        } else {
            val ranges = predefinedRanges.ranges
            var counter = 0
            val length = ranges.sumOf { it.second }
            val pointsArray = DoubleArray(length)
            val weightsArray = DoubleArray(length)

            for (range in ranges) {
                val rule = factory.build(range.second, range.first)
                repeat(rule.first.size) { i ->
                    pointsArray[counter] = rule.first[i]
                    weightsArray[counter] = rule.second[i]
                    counter++
                }

            }
            return pointsArray.asBuffer() to weightsArray.asBuffer()
        }
    }

    override fun process(integrand: UnivariateIntegrand<T>): UnivariateIntegrand<T> = with(algebra) {
        val f = integrand.function
        val (points, weights) = buildRule(integrand)
        var res = zero
        var c = zero
        for (i in points.indices) {
            val x = points[i]
            val weight = weights[i]
            val y: T = weight * f(x) - c
            val t = res + y
            c = t - res - y
            res = t
        }
        return integrand.with(IntegrandValue(res),IntegrandCallsPerformed(integrand.calls + points.size))
    }

    public companion object {

    }
}

/**
 * Integrate [T]-valued univariate function using provided set of [IntegrandFeature]
 * Following features are evaluated:
 * * [GaussIntegratorRuleFactory] - A factory for computing the Gauss integration rule. By default uses [GaussLegendreRuleFactory]
 * * [IntegrationRange] - the univariate range of integration. By default uses 0..1 interval.
 * * [IntegrandMaxCalls] - the maximum number of function calls during integration. For non-iterative rules, always uses the maximum number of points. By default uses 10 points.
 * * [UnivariateIntegrandRanges] - Set of ranges and number of points per range. Defaults to given [IntegrationRange] and [IntegrandMaxCalls]
 */
@UnstableKMathAPI
public fun <T : Any> Field<T>.process(
    vararg features: IntegrandFeature,
    function: (Double) -> T,
): UnivariateIntegrand<T> = GaussIntegrator(this).process(UnivariateIntegrand(function, *features))


/**
 * Use [GaussIntegrator.Companion.integrate] to integrate the function in the current algebra with given [range] and [numPoints]
 */
@UnstableKMathAPI
public fun <T : Any> Field<T>.process(
    range: ClosedRange<Double>,
    order: Int = 10,
    intervals: Int = 10,
    vararg features: IntegrandFeature,
    function: (Double) -> T,
): UnivariateIntegrand<T> {
    require(range.endInclusive > range.start) { "The range upper bound should be higher than lower bound" }
    require(order > 1) { "The order of polynomial must be more than 1" }
    require(intervals > 0) { "Number of intervals must be positive" }
    val rangeSize = (range.endInclusive - range.start) / intervals
    val ranges = UnivariateIntegrandRanges(
        (0 until intervals).map { i -> (rangeSize * i)..(rangeSize * (i + 1)) to order }
    )
    return GaussIntegrator(this).process(
        UnivariateIntegrand(
            function,
            IntegrationRange(range),
            GaussLegendreRuleFactory,
            ranges,
            *features
        )
    )
}