/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * A container for [Attributes]
 */
public interface AttributeContainer {
    public val attributes: Attributes
}

/**
 * A scope, where attribute keys could be resolved
 */
public interface AttributeScope<O>

