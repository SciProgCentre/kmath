/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.tensor

/**
 * Casts [Tensor<Double>] to [DoubleTensor]
 */
public fun Tensor<Double>.toDoubleTensor(): DoubleTensor = this.tensor

/**
 * Casts [Tensor<Int>] to [IntTensor]
 */
public fun Tensor<Int>.toIntTensor(): IntTensor = this.tensor

/**
 * @return [DoubleArray] of tensor elements
 */
public fun DoubleTensor.toDoubleArray(): DoubleArray {
    return DoubleArray(numElements) { i ->
        mutableBuffer[bufferStart + i]
    }
}

/**
 * @return [IntArray] of tensor elements
 */
public fun IntTensor.toIntArray(): IntArray {
    return IntArray(numElements) { i ->
        mutableBuffer[bufferStart + i]
    }
}

/**
 * Casts [Array<DoubleArray>] to [DoubleTensor]
 */
public fun Array<DoubleArray>.toDoubleTensor(): DoubleTensor {
    val n = size
    check(n > 0) { "An empty array cannot be casted to tensor" }
    val m = first().size
    check(m > 0) { "Inner arrays must have at least 1 argument" }
    check(all { size == m }) { "Inner arrays must be the same size" }

    val shape = intArrayOf(n, m)
    val buffer = this.flatMap { arr -> arr.map { it } }.toDoubleArray()

    return DoubleTensor(shape, buffer, 0)
}

/**
 * Casts [Array<IntArray>] to [IntTensor]
 */
public fun Array<IntArray>.toIntTensor(): IntTensor {
    val n = size
    check(n > 0) { "An empty array cannot be casted to tensor" }
    val m = first().size
    check(m > 0) { "Inner arrays must have at least 1 argument" }
    check(all { size == m }) { "Inner arrays must be the same size" }

    val shape = intArrayOf(n, m)
    val buffer = this.flatMap { arr -> arr.map { it } }.toIntArray()

    return IntTensor(shape, buffer, 0)
}
