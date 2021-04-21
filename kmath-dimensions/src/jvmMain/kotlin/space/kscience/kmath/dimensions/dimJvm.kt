/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.dimensions

import kotlin.reflect.KClass

public actual fun <D : Dimension> Dimension.Companion.resolve(type: KClass<D>): D =
    type.objectInstance ?: error("No object instance for dimension class")

public actual fun Dimension.Companion.of(dim: UInt): Dimension = when (dim) {
    1u -> D1
    2u -> D2
    3u -> D3

    else -> object : Dimension {
        override val dim: UInt get() = dim
    }
}