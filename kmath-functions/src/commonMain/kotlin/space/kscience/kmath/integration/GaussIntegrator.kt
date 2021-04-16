/* 
 * Copyright 2015 Alexander Nozik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package space.kscience.kmath.integration

import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices

/**
 * A simple one-pass integrator based on Gauss rule
 */
public class GaussIntegrator<T : Comparable<T>> internal constructor(
    public val algebra: Ring<T>,
    private val points: Buffer<T>,
    private val weights: Buffer<T>,
) : UnivariateIntegrator<T> {

    init {
        require(points.size == weights.size) { "Inconsistent points and weights sizes" }
        require(points.indices.all { i -> i == 0 || points[i] > points[i - 1] }) { "Integration nodes must be sorted" }
    }

    override fun integrate(integrand: UnivariateIntegrand<T>): UnivariateIntegrand<T> = with(algebra) {
        val f = integrand.function
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
        return integrand + IntegrandValue(res) + IntegrandCalls(integrand.calls + points.size)
    }

    public companion object {

        /**
         * Integrate given [function] in a [range] with Gauss-Legendre quadrature with [numPoints] points.
         */
        public fun integrate(
            range: ClosedRange<Double>,
            numPoints: Int = 100,
            ruleFactory: GaussIntegratorRuleFactory<Double> = GaussLegendreDoubleRuleFactory,
            features: List<IntegrandFeature> = emptyList(),
            function: (Double) -> Double,
        ): UnivariateIntegrand<Double> {
            val (points, weights) = ruleFactory.build(numPoints, range)
            return GaussIntegrator(DoubleField, points, weights).integrate(
                UnivariateIntegrand(function, IntegrationRange(range))
            )
        }

//        public fun integrate(
//            borders: List<Double>,
//            numPoints: Int = 10,
//            ruleFactory: GaussIntegratorRuleFactory<Double> = GaussLegendreDoubleRuleFactory,
//            features: List<IntegrandFeature> = emptyList(),
//            function: (Double) -> Double,
//        ): UnivariateIntegrand<Double> {
//            require(borders.indices.all { i -> i == 0 || borders[i] > borders[i - 1] }){"Borders are not sorted"}
//
//            val (points, weights) = ruleFactory.build(numPoints, range)
//            return GaussIntegrator(DoubleField, points, weights).integrate(
//                UnivariateIntegrand(function, IntegrationRange(range))
//            )
//        }
    }
}