/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * An attribute that has a type parameter for value
 * @param type parameter-type
 */
public abstract class PolymorphicAttribute<T>(public val type: SafeType<T>) : Attribute<T> {
    override fun equals(other: Any?): Boolean = other != null &&
            (this::class == other::class) &&
            (other as? PolymorphicAttribute<*>)?.type == this.type

    override fun hashCode(): Int = this::class.hashCode() + type.hashCode()
}


/**
 * Get a polymorphic attribute using attribute factory
 */
@UnstableAttributesAPI
public operator fun <T> Attributes.get(attributeKeyBuilder: () -> PolymorphicAttribute<T>): T? =
    get(attributeKeyBuilder())

/**
 * Set a polymorphic attribute using its factory
 */
@UnstableAttributesAPI
public operator fun <O, T> AttributesBuilder<O>.set(attributeKeyBuilder: () -> PolymorphicAttribute<T>, value: T) {
    set(attributeKeyBuilder(), value)
}
