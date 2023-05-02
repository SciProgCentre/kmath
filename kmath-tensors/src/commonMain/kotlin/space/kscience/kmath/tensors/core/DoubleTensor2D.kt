/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.linearSize
import space.kscience.kmath.structures.PermutedMutableBuffer
import space.kscience.kmath.structures.permute

public class DoubleTensor2D(
    override val rowNum: Int,
    override val colNum: Int,
    source: OffsetDoubleBuffer,
) : DoubleTensor(ShapeND(rowNum, colNum), source), MutableStructure2D<Double> {

    override fun get(i: Int, j: Int): Double = source[i * colNum + j]

    @OptIn(PerformancePitfall::class)
    override fun get(index: IntArray): Double = getDouble(index)

    override fun set(i: Int, j: Int, value: Double) {
        source[i * colNum + j] = value
    }

    @OptIn(PerformancePitfall::class)
    override val rows: List<OffsetDoubleBuffer>
        get() = List(rowNum) { i ->
            source.view(i * colNum, colNum)
        }


    @OptIn(PerformancePitfall::class)
    override val columns: List<PermutedMutableBuffer<Double>>
        get() = List(colNum) { j ->
            val indices = IntArray(rowNum) { i -> j + i * colNum }
            source.permute(indices)
        }

    override val shape: ShapeND get() = super<DoubleTensor>.shape

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, Double>> = super<MutableStructure2D>.elements()
}

/**
 * A zero-copy cast to 2D structure. Changes in resulting structure are reflected on original tensor.
 */
public fun DoubleTensor.asDoubleTensor2D(): DoubleTensor2D {
    require(shape.size == 2) { "Only 2D tensors could be cast to 2D" }
    return DoubleTensor2D(shape[0], shape[1], source)
}


public inline fun DoubleTensor.forEachMatrix(block: (index: IntArray, matrix: DoubleTensor2D) -> Unit) {
    val n = shape.size
    check(n >= 2) { "Expected tensor with 2 or more dimensions, got size $n" }
    val matrixOffset = shape[n - 1] * shape[n - 2]
    val matrixShape = ShapeND(shape[n - 2], shape[n - 1])

    val size = matrixShape.linearSize
    for (i in 0 until linearSize / matrixOffset) {
        val offset = i * matrixOffset
        val index = indices.index(offset).sliceArray(0 until (shape.size - 2))
        block(index, DoubleTensor(matrixShape, source.view(offset, size)).asDoubleTensor2D())
    }
}
