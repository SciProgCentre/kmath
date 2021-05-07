/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.MutableBufferND
import space.kscience.kmath.structures.asMutableBuffer
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.algebras.TensorLinearStructure

internal fun BufferedTensor<Int>.asTensor(): IntTensor =
    IntTensor(this.shape, this.mutableBuffer.array(), this.bufferStart)

internal fun BufferedTensor<Double>.asTensor(): DoubleTensor =
    DoubleTensor(this.shape, this.mutableBuffer.array(), this.bufferStart)

internal fun <T> Tensor<T>.copyToBufferedTensor(): BufferedTensor<T> =
    BufferedTensor(
        this.shape,
        TensorLinearStructure(this.shape).indices().map(this::get).toMutableList().asMutableBuffer(), 0
    )

internal fun <T> Tensor<T>.toBufferedTensor(): BufferedTensor<T> = when (this) {
    is BufferedTensor<T> -> this
    is MutableBufferND<T> -> if (this.strides.strides contentEquals TensorLinearStructure(this.shape).strides)
        BufferedTensor(this.shape, this.mutableBuffer, 0) else this.copyToBufferedTensor()
    else -> this.copyToBufferedTensor()
}

internal val Tensor<Double>.tensor: DoubleTensor
    get() = when (this) {
        is DoubleTensor -> this
        else -> this.toBufferedTensor().asTensor()
    }

internal val Tensor<Int>.tensor: IntTensor
    get() = when (this) {
        is IntTensor -> this
        else -> this.toBufferedTensor().asTensor()
    }
