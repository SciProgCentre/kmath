/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

/**
 * An entity that contains a set of features defined by their types
 */
public interface Featured<F : Any> {
    public fun <T : F> getFeature(type: FeatureKey<T>): T?
}

public typealias FeatureKey<T> = KClass<out T>

public interface Feature<F : Feature<F>> {

    /**
     * A key used for extraction
     */
    @Suppress("UNCHECKED_CAST")
    public val key: FeatureKey<F>
        get() = this::class as FeatureKey<F>
}

/**
 * A container for a set of features
 */
@JvmInline
public value class FeatureSet<F : Feature<F>> private constructor(public val features: Map<FeatureKey<F>, F>) : Featured<F> {
    @Suppress("UNCHECKED_CAST")
    override fun <T : F> getFeature(type: FeatureKey<T>): T? = features[type]?.let { it as T }

    public inline fun <reified T : F> getFeature(): T? = getFeature(T::class)

    public fun <T : F> with(feature: T, type: FeatureKey<F> = feature.key): FeatureSet<F> =
        FeatureSet(features + (type to feature))

    public fun with(other: FeatureSet<F>): FeatureSet<F> = FeatureSet(features + other.features)

    public fun with(vararg otherFeatures: F): FeatureSet<F> =
        FeatureSet(features + otherFeatures.associateBy { it.key })

    public fun with(otherFeatures: Iterable<F>): FeatureSet<F> =
        FeatureSet(features + otherFeatures.associateBy { it.key })

    public operator fun iterator(): Iterator<F> = features.values.iterator()

    override fun toString(): String = features.values.joinToString(prefix = "[ ", postfix = " ]")


    public companion object {
        public fun <F : Feature<F>> of(vararg features: F): FeatureSet<F> = FeatureSet(features.associateBy { it.key })
        public fun <F : Feature<F>> of(features: Iterable<F>): FeatureSet<F> =
            FeatureSet(features.associateBy { it.key })
    }
}
