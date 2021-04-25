/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.FeatureSet
import kotlin.reflect.KClass

public class MultivariateIntegrand<T : Any> internal constructor(
    override val features: FeatureSet<IntegrandFeature>,
    public val function: (Point<T>) -> T,
) : Integrand

public fun <T : Any> MultivariateIntegrand<T>.with(vararg newFeatures: IntegrandFeature): MultivariateIntegrand<T> =
    MultivariateIntegrand(features.with(*newFeatures), function)

@Suppress("FunctionName")
public fun <T : Any> MultivariateIntegrand(
    vararg features: IntegrandFeature,
    function: (Point<T>) -> T,
): MultivariateIntegrand<T> = MultivariateIntegrand(FeatureSet.of(*features), function)

public val <T : Any> MultivariateIntegrand<T>.value: T? get() = getFeature<IntegrandValue<T>>()?.value
