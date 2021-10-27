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
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.mapInPlace
import space.kscience.kmath.operations.*
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra
import space.kscience.kmath.tensors.api.TensorPartialDivisionAlgebra

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

public abstract class MultikTensorAlgebra<T, A : Ring<T>> : TensorAlgebra<T, A> where T : Number, T : Comparable<T> {

    public abstract val type: DataType

    override fun structureND(shape: Shape, initializer: A.(IntArray) -> T): MultikTensor<T> {
        val strides = DefaultStrides(shape)
        val memoryView = initMemoryView<T>(strides.linearSize, type)
        strides.indices().forEachIndexed { linearIndex, tensorIndex ->
            memoryView[linearIndex] = elementAlgebra.initializer(tensorIndex)
        }
        return MultikTensor(NDArray(memoryView, shape = shape, dim = DN(shape.size)))
    }

    override fun StructureND<T>.map(transform: A.(T) -> T): MultikTensor<T> = if (this is MultikTensor) {
        val data = initMemoryView<T>(array.size, type)
        var count = 0
        for (el in array) data[count++] = elementAlgebra.transform(el)
        NDArray(data, shape = shape, dim = array.dim).wrap()
    } else {
        structureND(shape) { index ->
            transform(get(index))
        }
    }

    override fun StructureND<T>.mapIndexed(transform: A.(index: IntArray, T) -> T): MultikTensor<T> =
        if (this is MultikTensor) {
            val array = asMultik().array
            val data = initMemoryView<T>(array.size, type)
            val indexIter = array.multiIndices.iterator()
            var index = 0
            for (item in array) {
                if (indexIter.hasNext()) {
                    data[index++] = elementAlgebra.transform(indexIter.next(), item)
                } else {
                    throw ArithmeticException("Index overflow has happened.")
                }
            }
            NDArray(data, shape = array.shape, dim = array.dim).wrap()
        } else {
            structureND(shape) { index ->
                transform(index, get(index))
            }
        }

    override fun zip(left: StructureND<T>, right: StructureND<T>, transform: A.(T, T) -> T): MultikTensor<T> {
        require(left.shape.contentEquals(right.shape)) { "ND array shape mismatch" } //TODO replace by ShapeMismatchException
        val leftArray = left.asMultik().array
        val rightArray = right.asMultik().array
        val data = initMemoryView<T>(leftArray.size, type)
        var counter = 0
        val leftIterator = leftArray.iterator()
        val rightIterator = rightArray.iterator()
        //iterating them together
        while (leftIterator.hasNext()) {
            data[counter++] = elementAlgebra.transform(leftIterator.next(), rightIterator.next())
        }
        return NDArray(data, shape = leftArray.shape, dim = leftArray.dim).wrap()
    }

    /**
     * Convert a tensor to [MultikTensor] if necessary. If tensor is converted, changes on the resulting tensor
     * are not reflected back onto the source
     */
    public fun StructureND<T>.asMultik(): MultikTensor<T> = if (this is MultikTensor) {
        this
    } else {
        val res = mk.zeros<T, DN>(shape, type).asDNArray()
        for (index in res.multiIndices) {
            res[index] = this[index]
        }
        res.wrap()
    }

    public fun MutableMultiArray<T, *>.wrap(): MultikTensor<T> = MultikTensor(this.asDNArray())

    override fun StructureND<T>.valueOrNull(): T? = if (shape contentEquals intArrayOf(1)) {
        get(intArrayOf(0))
    } else null

    override fun T.plus(other: StructureND<T>): MultikTensor<T> =
        other.plus(this)

    override fun StructureND<T>.plus(arg: T): MultikTensor<T> =
        asMultik().array.deepCopy().apply { plusAssign(arg) }.wrap()

    override fun StructureND<T>.plus(other: StructureND<T>): MultikTensor<T> =
        asMultik().array.plus(other.asMultik().array).wrap()

    override fun Tensor<T>.plusAssign(value: T) {
        if (this is MultikTensor) {
            array.plusAssign(value)
        } else {
            mapInPlace { _, t -> elementAlgebra.add(t, value) }
        }
    }

    override fun Tensor<T>.plusAssign(other: StructureND<T>) {
        if (this is MultikTensor) {
            array.plusAssign(other.asMultik().array)
        } else {
            mapInPlace { index, t -> elementAlgebra.add(t, other[index]) }
        }
    }

    override fun T.minus(other: StructureND<T>): MultikTensor<T> = (-(other.asMultik().array - this)).wrap()

    override fun StructureND<T>.minus(arg: T): MultikTensor<T> =
        asMultik().array.deepCopy().apply { minusAssign(arg) }.wrap()

    override fun StructureND<T>.minus(other: StructureND<T>): MultikTensor<T> =
        asMultik().array.minus(other.asMultik().array).wrap()

    override fun Tensor<T>.minusAssign(value: T) {
        if (this is MultikTensor) {
            array.minusAssign(value)
        } else {
            mapInPlace { _, t -> elementAlgebra.run { t - value } }
        }
    }

    override fun Tensor<T>.minusAssign(other: StructureND<T>) {
        if (this is MultikTensor) {
            array.minusAssign(other.asMultik().array)
        } else {
            mapInPlace { index, t -> elementAlgebra.run { t - other[index] } }
        }
    }

    override fun T.times(arg: StructureND<T>): MultikTensor<T> =
        arg.asMultik().array.deepCopy().apply { timesAssign(this@times) }.wrap()

    override fun StructureND<T>.times(arg: T): Tensor<T> =
        asMultik().array.deepCopy().apply { timesAssign(arg) }.wrap()

    override fun StructureND<T>.times(other: StructureND<T>): MultikTensor<T> =
        asMultik().array.times(other.asMultik().array).wrap()

    override fun Tensor<T>.timesAssign(value: T) {
        if (this is MultikTensor) {
            array.timesAssign(value)
        } else {
            mapInPlace { _, t -> elementAlgebra.multiply(t, value) }
        }
    }

    override fun Tensor<T>.timesAssign(other: StructureND<T>) {
        if (this is MultikTensor) {
            array.timesAssign(other.asMultik().array)
        } else {
            mapInPlace { index, t -> elementAlgebra.multiply(t, other[index]) }
        }
    }

    override fun StructureND<T>.unaryMinus(): MultikTensor<T> =
        asMultik().array.unaryMinus().wrap()

    override fun StructureND<T>.get(i: Int): MultikTensor<T> = asMultik().array.mutableView(i).wrap()

    override fun StructureND<T>.transpose(i: Int, j: Int): MultikTensor<T> = asMultik().array.transpose(i, j).wrap()

    override fun StructureND<T>.view(shape: IntArray): MultikTensor<T> {
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
            mt
        } else {
            NDArray(mt.data, mt.offset, shape, dim = DN(shape.size), base = mt.base ?: mt)
        }.wrap()
    }

    override fun StructureND<T>.viewAs(other: StructureND<T>): MultikTensor<T> = view(other.shape)

    override fun StructureND<T>.dot(other: StructureND<T>): MultikTensor<T> =
        if (this.shape.size == 1 && other.shape.size == 1) {
            Multik.ndarrayOf(
                asMultik().array.asD1Array() dot other.asMultik().array.asD1Array()
            ).asDNArray().wrap()
        } else if (this.shape.size == 2 && other.shape.size == 2) {
            (asMultik().array.asD2Array() dot other.asMultik().array.asD2Array()).asDNArray().wrap()
        } else if (this.shape.size == 2 && other.shape.size == 1) {
            (asMultik().array.asD2Array() dot other.asMultik().array.asD1Array()).asDNArray().wrap()
        } else {
            TODO("Not implemented for broadcasting")
        }

    override fun diagonalEmbedding(diagonalEntries: Tensor<T>, offset: Int, dim1: Int, dim2: Int): MultikTensor<T> {
        TODO("Diagonal embedding not implemented")
    }

    override fun StructureND<T>.sum(): T = asMultik().array.reduceMultiIndexed { _: IntArray, acc: T, t: T ->
        elementAlgebra.add(acc, t)
    }

    override fun StructureND<T>.sum(dim: Int, keepDim: Boolean): MultikTensor<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.min(): T? = asMultik().array.min()

    override fun StructureND<T>.min(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.max(): T? = asMultik().array.max()

    override fun StructureND<T>.max(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.argMax(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }
}

public abstract class MultikDivisionTensorAlgebra<T, A : Field<T>>
    : MultikTensorAlgebra<T, A>(), TensorPartialDivisionAlgebra<T, A> where T : Number, T : Comparable<T> {

    override fun T.div(arg: StructureND<T>): MultikTensor<T> = arg.map { elementAlgebra.divide(this@div, it) }

    override fun StructureND<T>.div(arg: T): MultikTensor<T> =
        asMultik().array.deepCopy().apply { divAssign(arg) }.wrap()

    override fun StructureND<T>.div(other: StructureND<T>): MultikTensor<T> =
        asMultik().array.div(other.asMultik().array).wrap()

    override fun Tensor<T>.divAssign(value: T) {
        if (this is MultikTensor) {
            array.divAssign(value)
        } else {
            mapInPlace { _, t -> elementAlgebra.divide(t, value) }
        }
    }

    override fun Tensor<T>.divAssign(other: StructureND<T>) {
        if (this is MultikTensor) {
            array.divAssign(other.asMultik().array)
        } else {
            mapInPlace { index, t -> elementAlgebra.divide(t, other[index]) }
        }
    }
}

public object MultikDoubleAlgebra : MultikDivisionTensorAlgebra<Double, DoubleField>() {
    override val elementAlgebra: DoubleField get() = DoubleField
    override val type: DataType get() = DataType.DoubleDataType
}

public val Double.Companion.multikAlgebra: MultikTensorAlgebra<Double, DoubleField> get() = MultikDoubleAlgebra
public val DoubleField.multikAlgebra: MultikTensorAlgebra<Double, DoubleField> get() = MultikDoubleAlgebra

public object MultikFloatAlgebra : MultikDivisionTensorAlgebra<Float, FloatField>() {
    override val elementAlgebra: FloatField get() = FloatField
    override val type: DataType get() = DataType.FloatDataType
}

public val Float.Companion.multikAlgebra: MultikTensorAlgebra<Float, FloatField> get() = MultikFloatAlgebra
public val FloatField.multikAlgebra: MultikTensorAlgebra<Float, FloatField> get() = MultikFloatAlgebra

public object MultikShortAlgebra : MultikTensorAlgebra<Short, ShortRing>() {
    override val elementAlgebra: ShortRing get() = ShortRing
    override val type: DataType get() = DataType.ShortDataType
}

public val Short.Companion.multikAlgebra: MultikTensorAlgebra<Short, ShortRing> get() = MultikShortAlgebra
public val ShortRing.multikAlgebra: MultikTensorAlgebra<Short, ShortRing> get() = MultikShortAlgebra

public object MultikIntAlgebra : MultikTensorAlgebra<Int, IntRing>() {
    override val elementAlgebra: IntRing get() = IntRing
    override val type: DataType get() = DataType.IntDataType
}

public val Int.Companion.multikAlgebra: MultikTensorAlgebra<Int, IntRing> get() = MultikIntAlgebra
public val IntRing.multikAlgebra: MultikTensorAlgebra<Int, IntRing> get() = MultikIntAlgebra

public object MultikLongAlgebra : MultikTensorAlgebra<Long, LongRing>() {
    override val elementAlgebra: LongRing get() = LongRing
    override val type: DataType get() = DataType.LongDataType
}

public val Long.Companion.multikAlgebra: MultikTensorAlgebra<Long, LongRing> get() = MultikLongAlgebra
public val LongRing.multikAlgebra: MultikTensorAlgebra<Long, LongRing> get() = MultikLongAlgebra