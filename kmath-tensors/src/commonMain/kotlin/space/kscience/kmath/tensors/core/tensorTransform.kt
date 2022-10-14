/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.*
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.tensors.api.Tensor


/**
 * Create a mutable copy of given [StructureND].
 */
public fun StructureND<Double>.copyToTensor(): DoubleTensor = if (this is DoubleTensor) {
    DoubleTensor(shape, source.copy())
} else if (this is DoubleBufferND && indices is RowStrides) {
    DoubleTensor(shape, buffer.copy())
} else {
    DoubleTensor(
        shape,
        RowStrides(this.shape).map(this::getDouble).toDoubleArray().asBuffer(),
    )
}

public fun StructureND<Int>.toDoubleTensor(): DoubleTensor {
    return if (this is IntTensor) {
        DoubleTensor(
            shape,
            DoubleBuffer(linearSize) { source[it].toDouble() }
        )
    } else {
        val tensor = DoubleTensorAlgebra.zeroesLike(this)
        indices.forEach {
            tensor[it] = getInt(it).toDouble()
        }
        return tensor
    }
}

/**
 * Transforms [StructureND] of [Double] to [DoubleTensor]. Zero copy if possible, but is not guaranteed
 */
public fun StructureND<Double>.asDoubleTensor(): DoubleTensor = if (this is DoubleTensor) {
    this
} else if (this is DoubleBufferND && indices is RowStrides) {
    DoubleTensor(shape, buffer)
} else {
    copyToTensor()
}

/**
 * Casts [Tensor] of [Int] to [IntTensor]
 */
public fun StructureND<Int>.asIntTensor(): IntTensor = when (this) {
    is IntTensor -> this
    else -> IntTensor(
        shape,
        RowStrides(shape).map(this::getInt).toIntArray().asBuffer()
    )
}