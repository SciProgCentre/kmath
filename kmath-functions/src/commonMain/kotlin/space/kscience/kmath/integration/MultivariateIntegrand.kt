/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.FeatureSet

public class MultivariateIntegrand<T : Any> internal constructor(
    override val features: FeatureSet<IntegrandFeature>,
    public val function: (Point<T>) -> T,
) : Integrand {

    public operator fun <F : IntegrandFeature> plus(feature: F): MultivariateIntegrand<T> =
        MultivariateIntegrand(features.with(feature), function)
}

@Suppress("FunctionName")
public fun <T : Any> MultivariateIntegrand(
    vararg features: IntegrandFeature,
    function: (Point<T>) -> T,
): MultivariateIntegrand<T> = MultivariateIntegrand(FeatureSet.of(*features), function)

public val <T : Any> MultivariateIntegrand<T>.value: T? get() = getFeature<IntegrandValue<T>>()?.value
