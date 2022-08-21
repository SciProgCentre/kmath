/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("DimensionJVM")

package space.kscience.kmath.dimensions

import kotlin.reflect.KClass

public actual fun <D : Dimension> Dimension.Companion.resolve(type: KClass<D>): D =
    type.objectInstance ?: error("No object instance for dimension class")

public actual fun Dimension.Companion.of(dim: Int): Dimension = when (dim) {
    1 -> D1
    2 -> D2
    3 -> D3

    else -> object : Dimension {
        override val dim: Int get() = dim
    }
}
