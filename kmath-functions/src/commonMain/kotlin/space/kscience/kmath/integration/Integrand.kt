/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.kmath.misc.FeatureSet
import space.kscience.kmath.misc.Featured
import kotlin.reflect.KClass

public interface IntegrandFeature

public interface Integrand: Featured<IntegrandFeature>{
    public val features: FeatureSet<IntegrandFeature>
    override fun <T : IntegrandFeature> getFeature(type: KClass<out T>): T?  = features.getFeature(type)
}

public inline fun <reified T : IntegrandFeature> Integrand.getFeature(): T? = getFeature(T::class)

public class IntegrandValue<T : Any>(public val value: T) : IntegrandFeature

public class IntegrandRelativeAccuracy(public val accuracy: Double) : IntegrandFeature

public class IntegrandAbsoluteAccuracy(public val accuracy: Double) : IntegrandFeature

public class IntegrandCallsPerformed(public val calls: Int) : IntegrandFeature

public val Integrand.calls: Int get() = getFeature<IntegrandCallsPerformed>()?.calls ?: 0

public class IntegrandMaxCalls(public val maxCalls: Int) : IntegrandFeature
