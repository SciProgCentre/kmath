/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.commons.integration

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator
import space.kscience.attributes.Attributes
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.integration.*
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator as CMUnivariateIntegrator

/**
 * Integration wrapper for Common-maths UnivariateIntegrator
 */
public class CMIntegrator(
    private val defaultMaxCalls: Int = 200,
    public val integratorBuilder: (Integrand<Double>) -> CMUnivariateIntegrator,
) : UnivariateIntegrator<Double> {

    override fun integrate(integrand: UnivariateIntegrand<Double>): UnivariateIntegrand<Double> {
        val integrator = integratorBuilder(integrand)
        val maxCalls = integrand[IntegrandMaxCalls] ?: defaultMaxCalls
        val remainingCalls = maxCalls - integrand.calls
        val range = integrand[IntegrationRange] ?: error("Integration range is not provided")
        val res = integrator.integrate(remainingCalls, integrand.function, range.start, range.endInclusive)

        return integrand.withAttributes {
            value(res)
            IntegrandAbsoluteAccuracy(integrator.absoluteAccuracy)
            IntegrandRelativeAccuracy(integrator.relativeAccuracy)
            IntegrandCallsPerformed(integrator.evaluations + integrand.calls)
        }
    }


    public companion object {
        /**
         * Create a Simpson integrator based on [SimpsonIntegrator]
         */
        public fun simpson(defaultMaxCalls: Int = 200): CMIntegrator = CMIntegrator(defaultMaxCalls) { integrand ->
            val absoluteAccuracy = integrand[IntegrandAbsoluteAccuracy] ?: SimpsonIntegrator.DEFAULT_ABSOLUTE_ACCURACY
            val relativeAccuracy = integrand[IntegrandRelativeAccuracy] ?: SimpsonIntegrator.DEFAULT_ABSOLUTE_ACCURACY
            val iterations = integrand[IntegrandIterationsRange]
                ?: SimpsonIntegrator.DEFAULT_MIN_ITERATIONS_COUNT..SimpsonIntegrator.SIMPSON_MAX_ITERATIONS_COUNT


            SimpsonIntegrator(relativeAccuracy, absoluteAccuracy, iterations.first, iterations.last)
        }

        /**
         * Create a Gauss-Legandre integrator based on [IterativeLegendreGaussIntegrator]
         */
        public fun legandre(numPoints: Int, defaultMaxCalls: Int = numPoints * 5): CMIntegrator =
            CMIntegrator(defaultMaxCalls) { integrand ->
                val absoluteAccuracy = integrand[IntegrandAbsoluteAccuracy]
                    ?: IterativeLegendreGaussIntegrator.DEFAULT_ABSOLUTE_ACCURACY
                val relativeAccuracy = integrand[IntegrandRelativeAccuracy]
                    ?: IterativeLegendreGaussIntegrator.DEFAULT_ABSOLUTE_ACCURACY
                val iterations = integrand[IntegrandIterationsRange]
                    ?: IterativeLegendreGaussIntegrator.DEFAULT_MIN_ITERATIONS_COUNT..IterativeLegendreGaussIntegrator.DEFAULT_MAX_ITERATIONS_COUNT

                IterativeLegendreGaussIntegrator(
                    numPoints,
                    relativeAccuracy,
                    absoluteAccuracy,
                    iterations.first,
                    iterations.last
                )
            }
    }
}

@UnstableKMathAPI
public val Attributes.targetAbsoluteAccuracy: Double?
    get() = get(IntegrandAbsoluteAccuracy)

@UnstableKMathAPI
public val Attributes.targetRelativeAccuracy: Double?
    get() = get(IntegrandRelativeAccuracy)
