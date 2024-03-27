/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.functions.PiecewisePolynomial
import space.kscience.kmath.functions.integrate
import space.kscience.kmath.interpolation.PolynomialInterpolator
import space.kscience.kmath.interpolation.SplineInterpolator
import space.kscience.kmath.interpolation.interpolatePolynomials
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64Buffer
import space.kscience.kmath.structures.MutableBufferFactory

/**
 * Compute analytical indefinite integral of this [PiecewisePolynomial], keeping all intervals intact
 */
@OptIn(PerformancePitfall::class)
@UnstableKMathAPI
public fun <T : Comparable<T>> PiecewisePolynomial<T>.integrate(algebra: Field<T>): PiecewisePolynomial<T> =
    PiecewisePolynomial(pieces.map { it.first to it.second.integrate(algebra) })

/**
 * Compute definite integral of given [PiecewisePolynomial] piece by piece in a given [range]
 * Requires [UnivariateIntegrationNodes] or [IntegrationRange] and [IntegrandMaxCalls]
 *
 * TODO use context receiver for algebra
 */
@UnstableKMathAPI
public fun <T : Comparable<T>> PiecewisePolynomial<T>.integrate(
    algebra: Field<T>, range: ClosedRange<T>,
): T = algebra.sum(
    pieces.map { (region, poly) ->
        val intersectedRange = maxOf(range.start, region.start)..minOf(range.endInclusive, region.endInclusive)
        //Check if polynomial range is not used
        if (intersectedRange.start == intersectedRange.endInclusive) algebra.zero
        else poly.integrate(algebra, intersectedRange)
    }
)

/**
 * A generic spline-interpolation-based analytic integration
 * * [IntegrationRange]&mdash;the univariate range of integration. By default, uses `0..1` interval.
 * * [IntegrandMaxCalls]&mdash;the maximum number of function calls during integration. For non-iterative rules, always uses
 * the maximum number of points. By default, uses 10 points.
 */
@UnstableKMathAPI
public class SplineIntegrator<T : Comparable<T>>(
    public val algebra: Field<T>,
    public val bufferFactory: MutableBufferFactory<T>,
) : UnivariateIntegrator<T> {
    override fun integrate(integrand: UnivariateIntegrand<T>): UnivariateIntegrand<T> = algebra {
        val range = integrand[IntegrationRange] ?: 0.0..1.0

        val interpolator: PolynomialInterpolator<T> = SplineInterpolator(algebra, bufferFactory)

        val nodes: Buffer<Double> = integrand[UnivariateIntegrationNodes] ?: run {
            val numPoints = integrand[IntegrandMaxCalls] ?: 100
            val step = (range.endInclusive - range.start) / (numPoints - 1)
            Float64Buffer(numPoints) { i -> range.start + i * step }
        }

        val values = nodes.mapToBuffer(bufferFactory) { integrand.function(it) }
        val polynomials = interpolator.interpolatePolynomials(
            nodes.mapToBuffer(bufferFactory) { number(it) },
            values
        )
        val res = polynomials.integrate(algebra, number(range.start)..number(range.endInclusive))
        integrand.withAttributes {
            IntegrandValue(res)
            IntegrandCallsPerformed(integrand.calls + nodes.size)
        }
    }
}

/**
 * A simplified double-based spline-interpolation-based analytic integration
 * * [IntegrationRange]&mdash;the univariate range of integration. By default, uses `0.0..1.0` interval.
 * * [IntegrandMaxCalls]&mdash;the maximum number of function calls during integration. For non-iterative rules, always
 * uses the maximum number of points. By default, uses 10 points.
 */
@UnstableKMathAPI
public object DoubleSplineIntegrator : UnivariateIntegrator<Double> {
    override fun integrate(integrand: UnivariateIntegrand<Double>): UnivariateIntegrand<Double> {
        val range = integrand[IntegrationRange] ?: 0.0..1.0
        val interpolator: PolynomialInterpolator<Double> = SplineInterpolator(Float64Field, Float64Field.bufferFactory)

        val nodes: Buffer<Double> = integrand[UnivariateIntegrationNodes] ?: run {
            val numPoints = integrand[IntegrandMaxCalls] ?: 100
            val step = (range.endInclusive - range.start) / (numPoints - 1)
            Float64Buffer(numPoints) { i -> range.start + i * step }
        }

        val values = nodes.mapToBuffer(Float64Field.bufferFactory) { integrand.function(it) }
        val polynomials = interpolator.interpolatePolynomials(nodes, values)
        val res = polynomials.integrate(Float64Field, range)
        return integrand.withAttributes {
            IntegrandValue(res)
            IntegrandCallsPerformed(integrand.calls + nodes.size)
        }
    }
}

@Suppress("unused")
@UnstableKMathAPI
public inline val Float64Field.splineIntegrator: UnivariateIntegrator<Double>
    get() = DoubleSplineIntegrator