/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.Strides
import kotlin.math.max

/**
 * This [Strides] implementation follows the last dimension first convention
 * For more information: https://numpy.org/doc/stable/reference/generated/numpy.ndarray.strides.html
 *
 * @param shape the shape of the tensor.
 */
public class TensorLinearStructure(override val shape: IntArray) : Strides {
    override val strides: IntArray
        get() = stridesFromShape(shape)

    override fun index(offset: Int): IntArray =
        indexFromOffset(offset, strides, shape.size)

    override val linearSize: Int
        get() = shape.reduce(Int::times)

    public companion object {

        public fun stridesFromShape(shape: IntArray): IntArray {
            val nDim = shape.size
            val res = IntArray(nDim)
            if (nDim == 0)
                return res

            var current = nDim - 1
            res[current] = 1

            while (current > 0) {
                res[current - 1] = max(1, shape[current]) * res[current]
                current--
            }
            return res
        }

        public fun indexFromOffset(offset: Int, strides: IntArray, nDim: Int): IntArray {
            val res = IntArray(nDim)
            var current = offset
            var strideIndex = 0

            while (strideIndex < nDim) {
                res[strideIndex] = (current / strides[strideIndex])
                current %= strides[strideIndex]
                strideIndex++
            }
            return res
        }
    }

}