/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

import space.kscience.attributes.*
import kotlin.reflect.typeOf

public interface IntegrandAttribute<T> : Attribute<T>

public interface Integrand<T> : AttributeContainer {

    public fun modify(block: AttributesBuilder.() -> Unit): Integrand<T>

    public fun <A : Any> withAttribute(attribute: Attribute<A>, value: A): Integrand<T>

    public companion object
}

public operator fun <T> Integrand<*>.get(attribute: Attribute<T>): T? = attributes[attribute]

public class IntegrandValue<T>(type: SafeType<T>) : PolymorphicAttribute<T>(type), IntegrandAttribute<T>

public inline val <reified T : Any> Integrand<T>.Value: IntegrandValue<T> get() = IntegrandValue(safeTypeOf())

public fun <T> AttributesBuilder.value(value: T){
    val type: SafeType<T> = typeOf<T>()
    IntegrandValue(type).invoke(value)
}

/**
 * Value of the integrand if it is present or null
 */
public inline val <reified T : Any> Integrand<T>.valueOrNull: T? get() = attributes[Value]

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
