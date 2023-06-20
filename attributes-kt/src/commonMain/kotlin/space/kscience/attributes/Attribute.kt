/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

import kotlin.reflect.KType

public interface Attribute<T>


public interface AttributeWithDefault<T> : Attribute<T> {
    public val default: T
}

public interface SetAttribute<V> : Attribute<Set<V>>

/**
 * An attribute that has a type parameter for value
 */
public abstract class PolymorphicAttribute<T>(public val type: KType) : Attribute<T> {
    override fun equals(other: Any?): Boolean = (other as? PolymorphicAttribute<*>)?.type == this.type

    override fun hashCode(): Int {
        return type.hashCode()
    }
}
