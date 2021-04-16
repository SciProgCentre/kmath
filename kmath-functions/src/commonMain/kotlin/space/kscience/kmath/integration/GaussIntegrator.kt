/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.integration

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Field
import space.kscience.kmath.structures.*


/**
 * A simple one-pass integrator based on Gauss rule
 */
public class GaussIntegrator<T : Any>(
    public val algebra: Field<T>,
) : UnivariateIntegrator<T> {

    private fun buildRule(integrand: UnivariateIntegrand<T>): Pair<Buffer<Double>, Buffer<Double>> {
        val factory = integrand.getFeature<GaussIntegratorRuleFactory>() ?: GaussLegendreRuleFactory
        val numPoints = integrand.getFeature<IntegrandMaxCalls>()?.maxCalls ?: 100
        val range = integrand.getFeature<IntegrationRange<Double>>()?.range ?: 0.0..1.0
        return factory.build(numPoints, range)
    }

    override fun integrate(integrand: UnivariateIntegrand<T>): UnivariateIntegrand<T> = with(algebra) {
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
        return integrand + IntegrandValue(res) + IntegrandCallsPerformed(integrand.calls + points.size)
    }

    public companion object {

    }
}

/**
 * Integrate [T]-valued univariate function using provided set of [IntegrandFeature]
 * Following features are evaluated:
 * * [GaussIntegratorRuleFactory] - A factory for computing the Gauss integration rule. By default uses [GaussLegendreRuleFactory]
 * * [IntegrationRange] - the univariate range of integration. By default uses 0..1 interval.
 * * [IntegrandMaxCalls] - the maximum number of function calls during integration. For non-iterative rules, always uses the maximum number of points. By default uses 100 points.
 */
@UnstableKMathAPI
public fun <T : Any> Field<T>.integrate(
    vararg features: IntegrandFeature,
    function: (Double) -> T,
): UnivariateIntegrand<T> = GaussIntegrator(this).integrate(UnivariateIntegrand(function, *features))


/**
 * Use [GaussIntegrator.Companion.integrate] to integrate the function in the current algebra with given [range] and [numPoints]
 */
@UnstableKMathAPI
public fun <T : Any> Field<T>.integrate(
    range: ClosedRange<Double>,
    numPoints: Int = 100,
    vararg features: IntegrandFeature,
    function: (Double) -> T,
): UnivariateIntegrand<T> = GaussIntegrator(this).integrate(
    UnivariateIntegrand(
        function,
        IntegrationRange(range),
        GaussLegendreRuleFactory,
        IntegrandMaxCalls(numPoints),
        *features
    )
)