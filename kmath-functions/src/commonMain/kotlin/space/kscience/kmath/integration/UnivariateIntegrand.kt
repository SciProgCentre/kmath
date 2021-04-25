/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.misc.UnstableKMathAPI
import kotlin.jvm.JvmInline


public class UnivariateIntegrand<T : Any> internal constructor(
    override val features: FeatureSet<IntegrandFeature>,
    public val function: (Double) -> T,
) : Integrand

public fun <T : Any> UnivariateIntegrand<T>.with(vararg newFeatures: IntegrandFeature): UnivariateIntegrand<T> =
    UnivariateIntegrand(features.with(*newFeatures), function)

@Suppress("FunctionName")
public fun <T : Any> UnivariateIntegrand(
    function: (Double) -> T,
    vararg features: IntegrandFeature,
): UnivariateIntegrand<T> = UnivariateIntegrand(FeatureSet.of(*features), function)

public typealias UnivariateIntegrator<T> = Integrator<UnivariateIntegrand<T>>

@JvmInline
public value class IntegrationRange(public val range: ClosedRange<Double>) : IntegrandFeature

public val <T : Any> UnivariateIntegrand<T>.value: T? get() = getFeature<IntegrandValue<T>>()?.value

/**
 * A shortcut method to integrate a [function] in [range] with additional [features].
 * The [function] is placed in the end position to allow passing a lambda.
 */
@UnstableKMathAPI
public fun UnivariateIntegrator<Double>.integrate(
    range: ClosedRange<Double>,
    vararg features: IntegrandFeature,
    function: (Double) -> Double,
): Double = process(
    UnivariateIntegrand(function, IntegrationRange(range), *features)
).value ?: error("Unexpected: no value after integration.")

/**
 * A shortcut method to integrate a [function] in [range] with additional [features].
 * The [function] is placed in the end position to allow passing a lambda.
 */
@UnstableKMathAPI
public fun UnivariateIntegrator<Double>.integrate(
    range: ClosedRange<Double>,
    function: (Double) -> Double,
    featureBuilder: (MutableList<IntegrandFeature>.() -> Unit) = {},
): Double {
    //TODO use dedicated feature builder class instead or add extensions to MutableList<IntegrandFeature>
    val features = buildList {
        featureBuilder()
        add(IntegrationRange(range))
    }
    return process(
        UnivariateIntegrand(function, *features.toTypedArray())
    ).value ?: error("Unexpected: no value after integration.")
}
