/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.attributes.*
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64Buffer

public class UnivariateIntegrand<T>(
    override val type: SafeType<T>,
    override val attributes: Attributes,
    public val function: (Double) -> T,
) : Integrand<T> {

    override fun withAttributes(attributes: Attributes): UnivariateIntegrand<T> =
        UnivariateIntegrand(type, attributes, function)
}

public fun <T, A : Any> UnivariateIntegrand<T>.withAttribute(
    attribute: Attribute<A>,
    value: A,
): UnivariateIntegrand<T> = withAttributes(attributes.withAttribute(attribute, value))

public fun <T> UnivariateIntegrand<T>.withAttributes(
    block: AttributesBuilder<UnivariateIntegrand<T>>.() -> Unit,
): UnivariateIntegrand<T> = withAttributes(attributes.modify(block))

public inline fun <reified T : Any> UnivariateIntegrand(
    attributeBuilder: AttributesBuilder<UnivariateIntegrand<T>>.() -> Unit,
    noinline function: (Double) -> T,
): UnivariateIntegrand<T> = UnivariateIntegrand(safeTypeOf(), Attributes(attributeBuilder), function)

public typealias UnivariateIntegrator<T> = Integrator<T, UnivariateIntegrand<T>>

public object IntegrationRange : IntegrandAttribute<ClosedRange<Double>>


/**
 * Set of univariate integration ranges. First components correspond to the ranges themselves, second components to
 * the number of integration nodes per range.
 */
public class UnivariateIntegrandRanges(public val ranges: List<Pair<ClosedRange<Double>, Int>>) {
    public constructor(vararg pairs: Pair<ClosedRange<Double>, Int>) : this(pairs.toList())

    override fun toString(): String {
        val rangesString = ranges.joinToString(separator = ",") { (range, points) ->
            "${range.start}..${range.endInclusive} : $points"
        }
        return "UnivariateRanges($rangesString)"
    }

    public companion object : IntegrandAttribute<UnivariateIntegrandRanges>
}

public object UnivariateIntegrationNodes : IntegrandAttribute<Buffer<Double>>

public fun AttributesBuilder<UnivariateIntegrand<*>>.integrationNodes(vararg nodes: Double) {
    UnivariateIntegrationNodes(Float64Buffer(nodes))
}

/**
 * A shortcut method to integrate a [function] with additional [features]. Range must be provided in features.
 * The [function] is placed in the end position to allow passing a lambda.
 */
@UnstableKMathAPI
public inline fun <reified T : Any> UnivariateIntegrator<T>.integrate(
    attributesBuilder: AttributesBuilder<UnivariateIntegrand<T>>.() -> Unit,
    noinline function: (Double) -> T,
): UnivariateIntegrand<T> = integrate(UnivariateIntegrand(attributesBuilder, function))

/**
 * A shortcut method to integrate a [function] in [range] with additional features.
 * The [function] is placed in the end position to allow passing a lambda.
 */
@UnstableKMathAPI
public inline fun <reified T : Any> UnivariateIntegrator<T>.integrate(
    range: ClosedRange<Double>,
    attributeBuilder: AttributesBuilder<UnivariateIntegrand<T>>.() -> Unit = {},
    noinline function: (Double) -> T,
): UnivariateIntegrand<T> {

    return integrate(
        UnivariateIntegrand(
            attributeBuilder = {
                IntegrationRange(range)
                attributeBuilder()
            },
            function = function
        )
    )
}
