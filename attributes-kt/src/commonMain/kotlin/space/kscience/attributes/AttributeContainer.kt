/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * A container for attributes. [attributes] could be made mutable by implementation
 */
public interface AttributeContainer {
    public val attributes: Attributes
}