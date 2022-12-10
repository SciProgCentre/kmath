/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.internal

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.first
import space.kscience.kmath.nd.last
import space.kscience.kmath.operations.asSequence
import space.kscience.kmath.structures.IntBuffer
import space.kscience.kmath.structures.VirtualBuffer
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.indices
import space.kscience.kmath.tensors.core.IntTensor
import space.kscience.kmath.tensors.core.OffsetIntBuffer

/**
 * Concatenate a list of arrays
 */
internal fun List<OffsetIntBuffer>.concat(): IntBuffer {
    val array = IntArray(sumOf { it.size })
    var pointer = 0
    while (pointer < array.size) {
        for (bufferIndex in indices) {
            val buffer = get(bufferIndex)
            for (innerIndex in buffer.indices) {
                array[pointer] = buffer[innerIndex]
                pointer++
            }
        }
    }
    return array.asBuffer()
}


internal fun IntTensor.vectors(): VirtualBuffer<IntTensor> {
    val n = shape.size
    val vectorOffset = shape[n - 1]
    val vectorShape = shape.last(1)

    return VirtualBuffer(linearSize / vectorOffset) { index ->
        val offset = index * vectorOffset
        IntTensor(vectorShape, source.view(offset, vectorShape.first()))
    }
}


internal fun IntTensor.vectorSequence(): Sequence<IntTensor> = vectors().asSequence()


internal val IntTensor.matrices: VirtualBuffer<IntTensor>
    get(){
        val n = shape.size
        check(n >= 2) { "Expected tensor with 2 or more dimensions, got size $n" }
        val matrixOffset = shape[n - 1] * shape[n - 2]
        val matrixShape = ShapeND(shape[n - 2], shape[n - 1])

        return VirtualBuffer(linearSize / matrixOffset) { index ->
            val offset = index * matrixOffset
            IntTensor(matrixShape, source.view(offset))
        }
    }

internal fun IntTensor.matrixSequence(): Sequence<IntTensor> = matrices.asSequence()