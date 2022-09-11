/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.tensors.api.Tensor


public fun StructureND<Double>.copyToTensor(): DoubleTensor = if (this is DoubleTensor) {
    DoubleTensor(shape, source.copy())
} else {
    DoubleTensor(
        shape,
        TensorLinearStructure(this.shape).map(this::get).toDoubleArray().asBuffer(),
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
            tensor[it] = get(it).toDouble()
        }
        return tensor
    }
}

/**
 * Casts [Tensor] of [Double] to [DoubleTensor]
 */
public fun StructureND<Double>.asDoubleTensor(): DoubleTensor = when (this) {
    is DoubleTensor -> this
    else -> copyToTensor()
}

/**
 * Casts [Tensor] of [Int] to [IntTensor]
 */
public fun StructureND<Int>.asIntTensor(): IntTensor = when (this) {
    is IntTensor -> this
    else -> IntTensor(
        this.shape,
        TensorLinearStructure(this.shape).map(this::get).toIntArray().asBuffer()
    )
}