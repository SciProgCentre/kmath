/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

public interface Attribute<T>

/**
 * An attribute that could be either present or absent
 */
public interface FlagAttribute : Attribute<Unit>

/**
 * An attribute with a default value
 */
public interface AttributeWithDefault<T> : Attribute<T> {
    public val default: T
}

/**
 * Attribute containing a set of values
 */
public interface SetAttribute<V> : Attribute<Set<V>>

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
