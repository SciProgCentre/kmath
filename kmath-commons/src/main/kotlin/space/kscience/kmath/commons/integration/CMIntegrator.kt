package space.kscience.kmath.commons.integration

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator
import space.kscience.kmath.integration.*
import space.kscience.kmath.misc.UnstableKMathAPI

/**
 * Integration wrapper for Common-maths UnivariateIntegrator
 */
public class CMIntegrator(
    private val defaultMaxCalls: Int = 200,
    public val integratorBuilder: (Integrand) -> org.apache.commons.math3.analysis.integration.UnivariateIntegrator,
) : UnivariateIntegrator<Double> {

    public class TargetRelativeAccuracy(public val value: Double) : IntegrandFeature
    public class TargetAbsoluteAccuracy(public val value: Double) : IntegrandFeature

    public class MinIterations(public val value: Int) : IntegrandFeature
    public class MaxIterations(public val value: Int) : IntegrandFeature

    override fun integrate(integrand: UnivariateIntegrand<Double>): UnivariateIntegrand<Double> {
        val integrator = integratorBuilder(integrand)
        val maxCalls = integrand.getFeature<IntegrandMaxCalls>()?.maxCalls ?: defaultMaxCalls
        val remainingCalls = maxCalls - integrand.calls
        val range = integrand.getFeature<IntegrationRange<Double>>()?.range
            ?: error("Integration range is not provided")
        val res = integrator.integrate(remainingCalls, integrand.function, range.start, range.endInclusive)

        return integrand +
                IntegrandValue(res) +
                IntegrandAbsoluteAccuracy(integrator.absoluteAccuracy) +
                IntegrandRelativeAccuracy(integrator.relativeAccuracy) +
                IntegrandCalls(integrator.evaluations + integrand.calls)
    }


    public companion object {
        /**
         * Create a Simpson integrator based on [SimpsonIntegrator]
         */
        public fun simpson(defaultMaxCalls: Int = 200): CMIntegrator = CMIntegrator(defaultMaxCalls) { integrand ->
            val absoluteAccuracy = integrand.getFeature<TargetAbsoluteAccuracy>()?.value
                ?: SimpsonIntegrator.DEFAULT_ABSOLUTE_ACCURACY
            val relativeAccuracy = integrand.getFeature<TargetRelativeAccuracy>()?.value
                ?: SimpsonIntegrator.DEFAULT_ABSOLUTE_ACCURACY
            val minIterations = integrand.getFeature<MinIterations>()?.value
                ?: SimpsonIntegrator.DEFAULT_MIN_ITERATIONS_COUNT
            val maxIterations = integrand.getFeature<MaxIterations>()?.value
                ?: SimpsonIntegrator.SIMPSON_MAX_ITERATIONS_COUNT

            SimpsonIntegrator(relativeAccuracy, absoluteAccuracy, minIterations, maxIterations)
        }

        /**
         * Create a Gauss-Legandre integrator based on [IterativeLegendreGaussIntegrator]
         */
        public fun legandre(numPoints: Int, defaultMaxCalls: Int = numPoints * 5): CMIntegrator =
            CMIntegrator(defaultMaxCalls) { integrand ->
                val absoluteAccuracy = integrand.getFeature<TargetAbsoluteAccuracy>()?.value
                    ?: IterativeLegendreGaussIntegrator.DEFAULT_ABSOLUTE_ACCURACY
                val relativeAccuracy = integrand.getFeature<TargetRelativeAccuracy>()?.value
                    ?: IterativeLegendreGaussIntegrator.DEFAULT_ABSOLUTE_ACCURACY
                val minIterations = integrand.getFeature<MinIterations>()?.value
                    ?: IterativeLegendreGaussIntegrator.DEFAULT_MIN_ITERATIONS_COUNT
                val maxIterations = integrand.getFeature<MaxIterations>()?.value
                    ?: IterativeLegendreGaussIntegrator.DEFAULT_MAX_ITERATIONS_COUNT

                IterativeLegendreGaussIntegrator(
                    numPoints,
                    relativeAccuracy,
                    absoluteAccuracy,
                    minIterations,
                    maxIterations
                )
            }
    }
}

@UnstableKMathAPI
public var MutableList<IntegrandFeature>.targetAbsoluteAccuracy: Double?
    get() = filterIsInstance<CMIntegrator.TargetAbsoluteAccuracy>().lastOrNull()?.value
    set(value) {
        value?.let { add(CMIntegrator.TargetAbsoluteAccuracy(value)) }
    }

@UnstableKMathAPI
public var MutableList<IntegrandFeature>.targetRelativeAccuracy: Double?
    get() = filterIsInstance<CMIntegrator.TargetRelativeAccuracy>().lastOrNull()?.value
    set(value) {
        value?.let { add(CMIntegrator.TargetRelativeAccuracy(value)) }
    }