/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.dimensions

import kotlin.reflect.KClass

private val dimensionMap: MutableMap<Int, Dimension> = hashMapOf(1 to D1, 2 to D2, 3 to D3)

@Suppress("UNCHECKED_CAST")
public actual fun <D : Dimension> Dimension.Companion.resolve(type: KClass<D>): D = dimensionMap
    .entries
    .map(MutableMap.MutableEntry<Int, Dimension>::value)
    .find { it::class == type } as? D
    ?: error("Can't resolve dimension $type")

public actual fun Dimension.Companion.of(dim: Int): Dimension = dimensionMap.getOrPut(dim) {
    object : Dimension {
        override val dim: Int get() = dim
    }
}
