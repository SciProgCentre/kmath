/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.integration

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Field
import space.kscience.kmath.structures.*


/**
 * A simple one-pass integrator based on Gauss rule
 */
public class GaussIntegrator<T : Any>(
    public val algebra: Field<T>,
    public val bufferFactory: BufferFactory<T>,
) : UnivariateIntegrator<T> {

    private fun buildRule(integrand: UnivariateIntegrand<T>): Pair<Buffer<T>, Buffer<T>> {
        val factory = integrand.getFeature<GaussIntegratorRuleFactory<T>>()
            ?: GenericGaussLegendreRuleFactory(algebra, bufferFactory)
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
            val x: T = points[i]
            val w: T = weights[i]
            val y: T = w * f(x) - c
            val t = res + y
            c = t - res - y
            res = t
        }
        return integrand + IntegrandValue(res) + IntegrandCallsPerformed(integrand.calls + points.size)
    }

    public companion object {

        /**
         * Integrate [T]-valued univariate function using provided set of [IntegrandFeature]
         * Following features are evaluated:
         * * [GaussIntegratorRuleFactory] - A factory for computing the Gauss integration rule. By default uses [GenericGaussLegendreRuleFactory]
         * * [IntegrationRange] - the univariate range of integration. By default uses 0..1 interval.
         * * [IntegrandMaxCalls] - the maximum number of function calls during integration. For non-iterative rules, always uses the maximum number of points. By default uses 100 points.
         */
        public fun <T : Any> integrate(
            algebra: Field<T>,
            bufferFactory: BufferFactory<T> = ::ListBuffer,
            vararg features: IntegrandFeature,
            function: (T) -> T,
        ): UnivariateIntegrand<T> =
            GaussIntegrator(algebra, bufferFactory).integrate(UnivariateIntegrand(function, *features))

        /**
         * Integrate in real numbers
         */
        public fun integrate(
            vararg features: IntegrandFeature,
            function: (Double) -> Double,
        ): UnivariateIntegrand<Double> = integrate(DoubleField, ::DoubleBuffer, features = features, function)

        /**
         * Integrate given [function] in a [range] with Gauss-Legendre quadrature with [numPoints] points.
         * The [range] is automatically transformed into [T] using [Field.number]
         */
        @UnstableKMathAPI
        public fun  <T : Any> legendre(
            algebra: Field<T>,
            range: ClosedRange<Double>,
            numPoints: Int = 100,
            bufferFactory: BufferFactory<T> = ::ListBuffer,
            vararg features: IntegrandFeature,
            function: (T) -> T,
        ): UnivariateIntegrand<T> = GaussIntegrator(algebra, bufferFactory).integrate(
            UnivariateIntegrand(
                function,
                IntegrationRange(range),
                DoubleGaussLegendreRuleFactory,
                IntegrandMaxCalls(numPoints),
                *features
            )
        )

        /**
         * Integrate given [function] in a [range] with Gauss-Legendre quadrature with [numPoints] points.
         */
        @UnstableKMathAPI
        public fun legendre(
            range: ClosedRange<Double>,
            numPoints: Int = 100,
            vararg features: IntegrandFeature,
            function: (Double) -> Double,
        ): UnivariateIntegrand<Double> = GaussIntegrator(DoubleField, ::DoubleBuffer).integrate(
            UnivariateIntegrand(
                function,
                IntegrationRange(range),
                DoubleGaussLegendreRuleFactory,
                IntegrandMaxCalls(numPoints),
                *features
            )
        )
    }
}