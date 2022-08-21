/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.commons.integration

import org.apache.commons.math3.analysis.integration.gauss.GaussIntegrator
import org.apache.commons.math3.analysis.integration.gauss.GaussIntegratorFactory
import space.kscience.kmath.integration.*

/**
 * A simple one-pass integrator based on Gauss rule
 */
public class CMGaussRuleIntegrator(
    private val numpoints: Int,
    private var type: GaussRule = GaussRule.LEGANDRE,
) : UnivariateIntegrator<Double> {

    override fun process(integrand: UnivariateIntegrand<Double>): UnivariateIntegrand<Double> {
        val range = integrand.getFeature<IntegrationRange>()?.range
            ?: error("Integration range is not provided")
        val integrator: GaussIntegrator = getIntegrator(range)
        //TODO check performance
        val res: Double = integrator.integrate(integrand.function)
        return integrand + IntegrandValue(res) + IntegrandCallsPerformed(integrand.calls + numpoints)
    }

    private fun getIntegrator(range: ClosedRange<Double>): GaussIntegrator {
        return when (type) {
            GaussRule.LEGANDRE -> factory.legendre(
                numpoints,
                range.start,
                range.endInclusive
            )
            GaussRule.LEGANDREHP -> factory.legendreHighPrecision(
                numpoints,
                range.start,
                range.endInclusive
            )
            GaussRule.UNIFORM -> GaussIntegrator(
                getUniformRule(
                    range.start,
                    range.endInclusive,
                    numpoints
                )
            )
        }
    }

    private fun getUniformRule(
        min: Double,
        max: Double,
        numPoints: Int,
    ): org.apache.commons.math3.util.Pair<DoubleArray, DoubleArray> {
        assert(numPoints > 2)
        val points = DoubleArray(numPoints)
        val weights = DoubleArray(numPoints)
        val step = (max - min) / (numPoints - 1)
        points[0] = min
        for (i in 1 until numPoints) {
            points[i] = points[i - 1] + step
            weights[i] = step
        }
        return org.apache.commons.math3.util.Pair<DoubleArray, DoubleArray>(points, weights)
    }

    public enum class GaussRule {
        UNIFORM, LEGANDRE, LEGANDREHP
    }

    public companion object {
        private val factory: GaussIntegratorFactory = GaussIntegratorFactory()

        public fun integrate(
            range: ClosedRange<Double>,
            numPoints: Int = 100,
            type: GaussRule = GaussRule.LEGANDRE,
            function: (Double) -> Double,
        ): Double = CMGaussRuleIntegrator(numPoints, type).process(
            UnivariateIntegrand(function, IntegrationRange(range))
        ).value
    }
}