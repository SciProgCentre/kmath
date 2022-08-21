/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.dimensions

import kotlin.reflect.KClass

/**
 * Represents a quantity of dimensions in certain structure. **This interface must be implemented only by objects.**
 *
 * @property dim The number of dimensions.
 */
public interface Dimension {
    public val dim: Int

    public companion object
}

public fun <D : Dimension> KClass<D>.dim(): Int = Dimension.resolve(this).dim

public expect fun <D : Dimension> Dimension.Companion.resolve(type: KClass<D>): D

/**
 * Finds or creates [Dimension] with [Dimension.dim] equal to [dim].
 */
public expect fun Dimension.Companion.of(dim: Int): Dimension

/**
 * Finds [Dimension.dim] of given type [D].
 */
public inline fun <reified D : Dimension> Dimension.Companion.dim(): Int = D::class.dim()

/**
 * Type representing 1 dimension.
 */
public object D1 : Dimension {
    override val dim: Int get() = 1
}

/**
 * Type representing 2 dimensions.
 */
public object D2 : Dimension {
    override val dim: Int get() = 2
}

/**
 * Type representing 3 dimensions.
 */
public object D3 : Dimension {
    override val dim: Int get() = 3
}
