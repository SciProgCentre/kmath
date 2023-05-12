/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.RowStrides
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.Strides
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.tensors.api.Tensor

/**
 * Represents [Tensor] over a [MutableBuffer] intended to be used through [DoubleTensor] and [IntTensor]
 */
public abstract class BufferedTensor<T>(
    override val shape: ShapeND,
) : Tensor<T> {

    public abstract val source: MutableBuffer<T>

    /**
     * Buffer strides based on [RowStrides] implementation
     */
    override val indices: Strides get() = RowStrides(shape)

    /**
     * Number of elements in tensor
     */
    public val linearSize: Int get() = indices.linearSize


    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = indices.asSequence().map {
        it to get(it)
    }
}
