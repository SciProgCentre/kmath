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

