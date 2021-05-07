/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.tensor

/**
 * Casts [Tensor] of [Double] to [DoubleTensor]
 */
public fun Tensor<Double>.toDoubleTensor(): DoubleTensor = this.tensor

/**
 * Casts [Tensor] of [Int] to [IntTensor]
 */
public fun Tensor<Int>.toIntTensor(): IntTensor = this.tensor

/**
 * Returns [DoubleArray] of tensor elements
 */
public fun DoubleTensor.toDoubleArray(): DoubleArray {
    return DoubleArray(numElements) { i ->
        mutableBuffer[bufferStart + i]
    }
}

/**
 * Returns [IntArray] of tensor elements
 */
public fun IntTensor.toIntArray(): IntArray {
    return IntArray(numElements) { i ->
        mutableBuffer[bufferStart + i]
    }
}
