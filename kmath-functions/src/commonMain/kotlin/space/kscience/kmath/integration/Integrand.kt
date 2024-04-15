/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.attributes.*

public interface IntegrandAttribute<T> : Attribute<T>

public interface Integrand<T> : AttributeContainer {

    public val type: SafeType<T>

    /**
     * Create a copy of this integrand with a new set of attributes
     */
    public fun withAttributes(attributes: Attributes): Integrand<T>

    public companion object
}

public operator fun <T> Integrand<*>.get(attribute: Attribute<T>): T? = attributes[attribute]

public sealed class IntegrandValue<T> private constructor() : IntegrandAttribute<T> {
    public companion object : IntegrandValue<Any?>() {
        @Suppress("UNCHECKED_CAST")
        public fun <T> forType(): IntegrandValue<T> = this as IntegrandValue<T>
    }
}

public fun <T> AttributesBuilder<Integrand<T>>.value(value: T) {
    IntegrandValue.forType<T>().invoke(value)
}

/**
 * Value of the integrand if it is present or null
 */
public inline val <reified T : Any> Integrand<T>.valueOrNull: T? get() = attributes[IntegrandValue.forType<T>()]

/**
 * Value of the integrand or error
 */
public inline val <reified T : Any> Integrand<T>.value: T get() = valueOrNull ?: error("No value in the integrand")

public object IntegrandRelativeAccuracy : IntegrandAttribute<Double>

public object IntegrandAbsoluteAccuracy : IntegrandAttribute<Double>

public object IntegrandCallsPerformed : IntegrandAttribute<Int>

public val Integrand<*>.calls: Int get() = attributes[IntegrandCallsPerformed] ?: 0

public object IntegrandMaxCalls : IntegrandAttribute<Int>

public object IntegrandIterationsRange : IntegrandAttribute<IntRange>
