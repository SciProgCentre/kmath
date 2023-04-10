/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.MutableStructure1D
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.structures.MutableBuffer

public class DoubleTensor1D(
    source: OffsetDoubleBuffer,
) : DoubleTensor(ShapeND(source.size), source), MutableStructure1D<Double> {

    @PerformancePitfall
    override fun get(index: IntArray): Double = super<MutableStructure1D>.get(index)

    @PerformancePitfall
    override fun set(index: IntArray, value: Double) {
        super<MutableStructure1D>.set(index, value)
    }

    override val size: Int get() = source.size

    override fun get(index: Int): Double = source[index]

    override fun set(index: Int, value: Double) {
        source[index] = value
    }

    override fun copy(): MutableBuffer<Double> = source.copy()

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, Double>> = super<MutableStructure1D>.elements()
}

/**
 * A zero-copy cast to 1D structure. Changes in resulting structure are reflected on original tensor.
 */
public fun DoubleTensor.asDoubleTensor1D(): DoubleTensor1D {
    require(shape.size == 1) { "Only 1D tensors could be cast to 1D" }
    return DoubleTensor1D(source)
}