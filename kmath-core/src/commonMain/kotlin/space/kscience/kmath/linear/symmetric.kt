/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.structures.BufferAccessor2D
import space.kscience.kmath.structures.MutableBuffer

public object SymmetricMatrixFeature : MatrixFeature

/**
 * Naive implementation of a symmetric matrix builder, that adds a [SymmetricMatrixFeature] tag. The resulting matrix contains
 * full `size^2` number of elements, but caches elements during calls to save [builder] calls. [builder] is always called in the
 * upper triangle region meaning that `i <= j`
 */
public fun <T : Any, LS : LinearSpace<T, *>> LS.buildSymmetricMatrix(
    size: Int,
    builder: (i: Int, j: Int) -> T,
): Matrix<T> = BufferAccessor2D<T?>(size, size, MutableBuffer.Companion::boxing).run {
    val cache = factory(size * size) { null }
    buildMatrix(size, size) { i, j ->
        val cached = cache[i, j]
        if (cached == null) {
            val value = if (i <= j) builder(i, j) else builder(j, i)
            cache[i, j] = value
            cache[j, i] = value
            value
        } else {
            cached
        }
    } + SymmetricMatrixFeature
}