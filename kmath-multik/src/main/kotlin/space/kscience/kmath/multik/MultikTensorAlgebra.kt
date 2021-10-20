/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

@file:Suppress("unused")

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.nd.mapInPlace
import space.kscience.kmath.operations.*
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra

@JvmInline
public value class MultikTensor<T>(public val array: MutableMultiArray<T, DN>) : Tensor<T> {
    override val shape: Shape get() = array.shape

    override fun get(index: IntArray): T = array[index]

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> =
        array.multiIndices.iterator().asSequence().map { it to get(it) }

    override fun set(index: IntArray, value: T) {
        array[index] = value
    }
}

private fun <T, D : Dimension> MultiArray<T, D>.asD1Array(): D1Array<T> {
    if (this is NDArray<T, D>)
        return this.asD1Array()
    else throw ClassCastException("Cannot cast MultiArray to NDArray.")
}


private fun <T, D : Dimension> MultiArray<T, D>.asD2Array(): D2Array<T> {
    if (this is NDArray<T, D>)
        return this.asD2Array()
    else throw ClassCastException("Cannot cast MultiArray to NDArray.")
}

public class MultikTensorAlgebra<T : Number> internal constructor(
    public val type: DataType,
    public val elementAlgebra: Ring<T>,
    public val comparator: Comparator<T>
) : TensorAlgebra<T> {

    /**
     * Convert a tensor to [MultikTensor] if necessary. If tensor is converted, changes on the resulting tensor
     * are not reflected back onto the source
     */
    public fun Tensor<T>.asMultik(): MultikTensor<T> {
        return if (this is MultikTensor) {
            this
        } else {
            val res = mk.zeros<T, DN>(shape, type).asDNArray()
            for (index in res.multiIndices) {
                res[index] = this[index]
            }
            res.wrap()
        }
    }

    public fun MutableMultiArray<T, DN>.wrap(): MultikTensor<T> = MultikTensor(this)

    override fun Tensor<T>.valueOrNull(): T? = if (shape contentEquals intArrayOf(1)) {
        get(intArrayOf(0))
    } else null

    override fun T.plus(other: Tensor<T>): MultikTensor<T> =
        other.plus(this)

    override fun Tensor<T>.plus(value: T): MultikTensor<T> =
        asMultik().array.deepCopy().apply { plusAssign(value) }.wrap()

    override fun Tensor<T>.plus(other: Tensor<T>): MultikTensor<T> =
        asMultik().array.plus(other.asMultik().array).wrap()

    override fun Tensor<T>.plusAssign(value: T) {
        if (this is MultikTensor) {
            array.plusAssign(value)
        } else {
            mapInPlace { _, t -> elementAlgebra.add(t, value) }
        }
    }

    override fun Tensor<T>.plusAssign(other: Tensor<T>) {
        if (this is MultikTensor) {
            array.plusAssign(other.asMultik().array)
        } else {
            mapInPlace { index, t -> elementAlgebra.add(t, other[index]) }
        }
    }

    override fun T.minus(other: Tensor<T>): MultikTensor<T> = (-(other.asMultik().array - this)).wrap()

    override fun Tensor<T>.minus(value: T): MultikTensor<T> =
        asMultik().array.deepCopy().apply { minusAssign(value) }.wrap()

    override fun Tensor<T>.minus(other: Tensor<T>): MultikTensor<T> =
        asMultik().array.minus(other.asMultik().array).wrap()

    override fun Tensor<T>.minusAssign(value: T) {
        if (this is MultikTensor) {
            array.minusAssign(value)
        } else {
            mapInPlace { _, t -> elementAlgebra.run { t - value } }
        }
    }

    override fun Tensor<T>.minusAssign(other: Tensor<T>) {
        if (this is MultikTensor) {
            array.minusAssign(other.asMultik().array)
        } else {
            mapInPlace { index, t -> elementAlgebra.run { t - other[index] } }
        }
    }

    override fun T.times(other: Tensor<T>): MultikTensor<T> =
        other.asMultik().array.deepCopy().apply { timesAssign(this@times) }.wrap()

    override fun Tensor<T>.times(value: T): Tensor<T> =
        asMultik().array.deepCopy().apply { timesAssign(value) }.wrap()

    override fun Tensor<T>.times(other: Tensor<T>): MultikTensor<T> =
        asMultik().array.times(other.asMultik().array).wrap()

    override fun Tensor<T>.timesAssign(value: T) {
        if (this is MultikTensor) {
            array.timesAssign(value)
        } else {
            mapInPlace { _, t -> elementAlgebra.multiply(t, value) }
        }
    }

    override fun Tensor<T>.timesAssign(other: Tensor<T>) {
        if (this is MultikTensor) {
            array.timesAssign(other.asMultik().array)
        } else {
            mapInPlace { index, t -> elementAlgebra.multiply(t, other[index]) }
        }
    }

    override fun Tensor<T>.unaryMinus(): MultikTensor<T> =
        asMultik().array.unaryMinus().wrap()

    override fun Tensor<T>.get(i: Int): MultikTensor<T> = asMultik().array.mutableView(i).wrap()

    override fun Tensor<T>.transpose(i: Int, j: Int): MultikTensor<T> = asMultik().array.transpose(i, j).wrap()

    override fun Tensor<T>.view(shape: IntArray): MultikTensor<T> {
        require(shape.all { it > 0 })
        require(shape.fold(1, Int::times) == this.shape.size) {
            "Cannot reshape array of size ${this.shape.size} into a new shape ${
                shape.joinToString(
                    prefix = "(",
                    postfix = ")"
                )
            }"
        }

        val mt = asMultik().array
        return if (mt.shape.contentEquals(shape)) {
            (this as MultikTensor<T>).array
        } else {
            NDArray(mt.data, mt.offset, shape, dim = DN(shape.size), base = mt.base ?: mt)
        }.wrap()
    }

    override fun Tensor<T>.viewAs(other: Tensor<T>): MultikTensor<T> = view(other.shape)

    override fun Tensor<T>.dot(other: Tensor<T>): MultikTensor<T> =
        if (this.shape.size == 1 && other.shape.size == 1) {
            Multik.ndarrayOf(
                asMultik().array.asD1Array() dot other.asMultik().array.asD1Array()
            ).asDNArray().wrap()
        } else if (this.shape.size == 2 && other.shape.size == 2) {
            (asMultik().array.asD2Array() dot other.asMultik().array.asD2Array()).asDNArray().wrap()
        } else if(this.shape.size == 2 && other.shape.size == 1) {
            (asMultik().array.asD2Array() dot other.asMultik().array.asD1Array()).asDNArray().wrap()
        } else {
            TODO("Not implemented for broadcasting")
        }

    override fun diagonalEmbedding(diagonalEntries: Tensor<T>, offset: Int, dim1: Int, dim2: Int): MultikTensor<T> {
        TODO("Diagonal embedding not implemented")
    }

    override fun Tensor<T>.sum(): T = asMultik().array.reduceMultiIndexed { _: IntArray, acc: T, t: T ->
        elementAlgebra.add(acc, t)
    }

    override fun Tensor<T>.sum(dim: Int, keepDim: Boolean): MultikTensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.min(): T =
        asMultik().array.minWith(comparator) ?: error("No elements in tensor")

    override fun Tensor<T>.min(dim: Int, keepDim: Boolean): MultikTensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.max(): T =
        asMultik().array.maxWith(comparator) ?: error("No elements in tensor")


    override fun Tensor<T>.max(dim: Int, keepDim: Boolean): MultikTensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.argMax(dim: Int, keepDim: Boolean): MultikTensor<T> {
        TODO("Not yet implemented")
    }
}

public val DoubleField.multikTensorAlgebra: MultikTensorAlgebra<Double>
    get() = MultikTensorAlgebra(DataType.DoubleDataType, DoubleField) { o1, o2 -> o1.compareTo(o2) }

public val FloatField.multikTensorAlgebra: MultikTensorAlgebra<Float>
    get() = MultikTensorAlgebra(DataType.FloatDataType, FloatField) { o1, o2 -> o1.compareTo(o2) }

public val ShortRing.multikTensorAlgebra: MultikTensorAlgebra<Short>
    get() = MultikTensorAlgebra(DataType.ShortDataType, ShortRing) { o1, o2 -> o1.compareTo(o2) }

public val IntRing.multikTensorAlgebra: MultikTensorAlgebra<Int>
    get() = MultikTensorAlgebra(DataType.IntDataType, IntRing) { o1, o2 -> o1.compareTo(o2) }

public val LongRing.multikTensorAlgebra: MultikTensorAlgebra<Long>
    get() = MultikTensorAlgebra(DataType.LongDataType, LongRing) { o1, o2 -> o1.compareTo(o2) }