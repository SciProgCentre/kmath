/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.attributes.*
import space.kscience.kmath.linear.Point

public class MultivariateIntegrand<T> internal constructor(
    override val attributes: Attributes,
    public val function: (Point<T>) -> T,
) : Integrand<T> {

    override fun modify(block: AttributesBuilder.() -> Unit): MultivariateIntegrand<T> =
        MultivariateIntegrand(attributes.modify(block), function)

    override fun <A : Any> withAttribute(attribute: Attribute<A>, value: A): MultivariateIntegrand<T> =
        MultivariateIntegrand(attributes.withAttribute(attribute, value), function)
}

public fun <T : Any> MultivariateIntegrand(
    attributeBuilder: AttributesBuilder.() -> Unit,
    function: (Point<T>) -> T,
): MultivariateIntegrand<T> = MultivariateIntegrand(Attributes(attributeBuilder), function)
