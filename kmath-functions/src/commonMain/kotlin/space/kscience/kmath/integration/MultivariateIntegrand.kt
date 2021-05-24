/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.linear.Point
import kotlin.reflect.KClass

public class MultivariateIntegrand<T : Any> internal constructor(
    private val featureMap: Map<KClass<*>, IntegrandFeature>,
    public val function: (Point<T>) -> T,
) : Integrand {

    override val features: Set<IntegrandFeature> get() = featureMap.values.toSet()

    @Suppress("UNCHECKED_CAST")
    override fun <T : IntegrandFeature> getFeature(type: KClass<T>): T? = featureMap[type] as? T

    public operator fun <F : IntegrandFeature> plus(pair: Pair<KClass<out F>, F>): MultivariateIntegrand<T> =
        MultivariateIntegrand(featureMap + pair, function)

    public operator fun <F : IntegrandFeature> plus(feature: F): MultivariateIntegrand<T> =
        plus(feature::class to feature)
}

@Suppress("FunctionName")
public fun <T : Any> MultivariateIntegrand(
    vararg features: IntegrandFeature,
    function: (Point<T>) -> T,
): MultivariateIntegrand<T> = MultivariateIntegrand(features.associateBy { it::class }, function)

public val <T : Any> MultivariateIntegrand<T>.value: T? get() = getFeature<IntegrandValue<T>>()?.value
