/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

import kotlin.jvm.JvmInline
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Safe variant ok Kotlin [KType] that ensures that the type parameter is of the same type ask [kType]
 *
 * @param kType raw [KType]
 */
@JvmInline
public value class SafeType<out T> @PublishedApi internal constructor(public val kType: KType)

public inline fun <reified T> safeTypeOf(): SafeType<T> = SafeType(typeOf<T>())

/**
 * Derive Kotlin [KClass] from this type and fail if the type is not a class (should not happen)
 */
@Suppress("UNCHECKED_CAST")
@UnstableAttributesAPI
public val <T> SafeType<T>.kClass: KClass<T & Any> get() = kType.classifier as KClass<T & Any>

/**
 * An interface containing [type] for dynamic type checking.
 */
public interface WithType<out T> {
    public val type: SafeType<T>
}