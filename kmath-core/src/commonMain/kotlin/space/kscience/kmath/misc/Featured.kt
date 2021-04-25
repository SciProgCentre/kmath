/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import kotlin.reflect.KClass

/**
 * A entity that contains a set of features defined by their types
 */
public interface Featured<F : Any> {
    public fun <T : F> getFeature(type: KClass<out T>): T?
}

/**
 * A container for a set of features
 */
public class FeatureSet<F : Any> private constructor(public val features: Map<KClass<out F>, Any>) : Featured<F> {
    @Suppress("UNCHECKED_CAST")
    override fun <T : F> getFeature(type: KClass<out T>): T? = features[type] as? T

    public inline fun <reified T : F> getFeature(): T? = getFeature(T::class)

    public fun <T : F> with(feature: T, type: KClass<out T> = feature::class): FeatureSet<F> =
        FeatureSet(features + (type to feature))

    public fun with(other: FeatureSet<F>): FeatureSet<F> = FeatureSet(features + other.features)

    public fun with(vararg otherFeatures: F): FeatureSet<F> =
        FeatureSet(features + otherFeatures.associateBy { it::class })

    public companion object {
        public fun <F : Any> of(vararg features: F): FeatureSet<F> = FeatureSet(features.associateBy { it::class })
    }
}
