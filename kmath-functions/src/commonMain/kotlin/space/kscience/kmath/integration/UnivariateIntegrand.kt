/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer

public class UnivariateIntegrand<T> internal constructor(
    override val features: FeatureSet<IntegrandFeature>,
    public val function: (Double) -> T,
) : Integrand {
    public operator fun <F : IntegrandFeature> plus(feature: F): UnivariateIntegrand<T> =
        UnivariateIntegrand(features.with(feature), function)
}

@Suppress("FunctionName")
public fun <T : Any> UnivariateIntegrand(
    function: (Double) -> T,
    vararg features: IntegrandFeature,
): UnivariateIntegrand<T> = UnivariateIntegrand(FeatureSet.of(*features), function)

public typealias UnivariateIntegrator<T> = Integrator<UnivariateIntegrand<T>>

public class IntegrationRange(public val range: ClosedRange<Double>) : IntegrandFeature {
    override fun toString(): String = "Range(${range.start}..${range.endInclusive})"
}

/**
 * Set of univariate integration ranges. First components correspond to the ranges themselves, second components to
 * number of integration nodes per range.
 */
public class UnivariateIntegrandRanges(public val ranges: List<Pair<ClosedRange<Double>, Int>>) : IntegrandFeature {
    public constructor(vararg pairs: Pair<ClosedRange<Double>, Int>) : this(pairs.toList())

    override fun toString(): String {
        val rangesString = ranges.joinToString(separator = ",") { (range, points) ->
            "${range.start}..${range.endInclusive} : $points"
        }
        return "UnivariateRanges($rangesString)"
    }
}

public class UnivariateIntegrationNodes(public val nodes: Buffer<Double>) : IntegrandFeature {
    public constructor(vararg nodes: Double) : this(DoubleBuffer(nodes))

    override fun toString(): String = "UnivariateNodes($nodes)"
}


/**
 * Value of the integrand if it is present or null
 */
public val <T : Any> UnivariateIntegrand<T>.valueOrNull: T? get() = getFeature<IntegrandValue<T>>()?.value

/**
 * Value of the integrand or error
 */
public val <T : Any> UnivariateIntegrand<T>.value: T get() = valueOrNull ?: error("No value in the integrand")

/**
 * A shortcut method to integrate a [function] with additional [features]. Range must be provided in features.
 * The [function] is placed in the end position to allow passing a lambda.
 */
@UnstableKMathAPI
public fun <T : Any> UnivariateIntegrator<T>.integrate(
    vararg features: IntegrandFeature,
    function: (Double) -> T,
): UnivariateIntegrand<T> = process(UnivariateIntegrand(function, *features))

/**
 * A shortcut method to integrate a [function] in [range] with additional [features].
 * The [function] is placed in the end position to allow passing a lambda.
 */
@UnstableKMathAPI
public fun <T : Any> UnivariateIntegrator<T>.integrate(
    range: ClosedRange<Double>,
    vararg features: IntegrandFeature,
    function: (Double) -> T,
): UnivariateIntegrand<T> = process(UnivariateIntegrand(function, IntegrationRange(range), *features))

/**
 * A shortcut method to integrate a [function] in [range] with additional features.
 * The [function] is placed in the end position to allow passing a lambda.
 */
@UnstableKMathAPI
public fun <T : Any> UnivariateIntegrator<T>.integrate(
    range: ClosedRange<Double>,
    featureBuilder: MutableList<IntegrandFeature>.() -> Unit = {},
    function: (Double) -> T,
): UnivariateIntegrand<T> {
    //TODO use dedicated feature builder class instead or add extensions to MutableList<IntegrandFeature>
    val features = buildList {
        featureBuilder()
        add(IntegrationRange(range))
    }
    return process(UnivariateIntegrand(function, *features.toTypedArray()))
}
