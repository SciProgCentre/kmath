/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.ndarray.data.*
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.tensors.api.Tensor
import kotlin.jvm.JvmInline

@JvmInline
public value class MultikTensor<T>(public val array: MutableMultiArray<T, DN>) : Tensor<T> {
    override val shape: ShapeND get() = ShapeND(array.shape)

    @PerformancePitfall
    override fun get(index: IntArray): T = array[index]

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> =
        array.multiIndices.iterator().asSequence().map { it to get(it) }

    @PerformancePitfall
    override fun set(index: IntArray, value: T) {
        array[index] = value
    }
}


internal fun <T, D : Dimension> MultiArray<T, D>.asD1Array(): D1Array<T> {
    if (this is NDArray<T, D>)
        return this.asD1Array()
    else throw ClassCastException("Cannot cast MultiArray to NDArray.")
}


internal fun <T, D : Dimension> MultiArray<T, D>.asD2Array(): D2Array<T> {
    if (this is NDArray<T, D>)
        return this.asD2Array()
    else throw ClassCastException("Cannot cast MultiArray to NDArray.")
}