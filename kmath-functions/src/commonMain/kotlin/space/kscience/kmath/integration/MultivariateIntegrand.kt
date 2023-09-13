/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.attributes.*
import space.kscience.kmath.linear.Point

public class MultivariateIntegrand<T>(
    override val type: SafeType<T>,
    override val attributes: Attributes,
    public val function: (Point<T>) -> T,
) : Integrand<T> {

    override fun withAttributes(attributes: Attributes): MultivariateIntegrand<T> =
        MultivariateIntegrand(type, attributes, function)

}

public fun <T, A : Any> MultivariateIntegrand<T>.withAttribute(
    attribute: Attribute<A>,
    value: A,
): MultivariateIntegrand<T> = withAttributes(attributes.withAttribute(attribute, value))

public fun <T> MultivariateIntegrand<T>.withAttributes(
    block: TypedAttributesBuilder<MultivariateIntegrand<T>>.() -> Unit,
): MultivariateIntegrand<T> = withAttributes(attributes.modify(block))

public inline fun <reified T : Any> MultivariateIntegrand(
    attributeBuilder: TypedAttributesBuilder<MultivariateIntegrand<T>>.() -> Unit,
    noinline function: (Point<T>) -> T,
): MultivariateIntegrand<T> = MultivariateIntegrand(safeTypeOf<T>(), Attributes(attributeBuilder), function)
