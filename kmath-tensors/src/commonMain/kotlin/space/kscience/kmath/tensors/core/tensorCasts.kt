/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.toBufferedTensor
import space.kscience.kmath.tensors.core.internal.toTensor

/**
 * Casts [Tensor] of [Double] to [DoubleTensor]
 */
public fun StructureND<Double>.asDoubleTensor(): DoubleTensor = when (this) {
    is DoubleTensor -> this
    else -> this.toBufferedTensor().toTensor()
}

/**
 * Casts [Tensor] of [Int] to [IntTensor]
 */
public fun StructureND<Int>.asIntTensor(): IntTensor = when (this) {
    is IntTensor -> this
    else -> this.toBufferedTensor().toTensor()
}

/**
 * Returns a copy-protected [DoubleArray] of tensor elements
 */
public fun DoubleTensor.copyArray(): DoubleArray {
    //TODO use ArrayCopy
    return DoubleArray(numElements) { i ->
        mutableBuffer[bufferStart + i]
    }
}

/**
 * Returns a copy-protected [IntArray] of tensor elements
 */
public fun IntTensor.copyArray(): IntArray {
    return IntArray(numElements) { i ->
        mutableBuffer[bufferStart + i]
    }
}
