/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.*
import org.jetbrains.kotlinx.multik.api.linalg.LinAlg
import org.jetbrains.kotlinx.multik.api.math.Math
import org.jetbrains.kotlinx.multik.api.stat.Statistics
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnsafeKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra
import space.kscience.kmath.tensors.api.TensorPartialDivisionAlgebra

public abstract class MultikTensorAlgebra<T, A : Ring<T>>(
    private val multikEngine: Engine,
) : TensorAlgebra<T, A> where T : Number, T : Comparable<T> {

    public abstract val type: DataType

    protected val multikMath: Math = multikEngine.getMath()
    protected val multikLinAl: LinAlg = multikEngine.getLinAlg()
    protected val multikStat: Statistics = multikEngine.getStatistics()

    @OptIn(UnsafeKMathAPI::class)
    override fun structureND(shape: ShapeND, initializer: A.(IntArray) -> T): MultikTensor<T> {
        val strides = ColumnStrides(shape)
        val memoryView = initMemoryView<T>(strides.linearSize, type)
        strides.asSequence().forEachIndexed { linearIndex, tensorIndex ->
            memoryView[linearIndex] = elementAlgebra.initializer(tensorIndex)
        }
        return MultikTensor(NDArray(memoryView, shape = shape.asArray(), dim = DN(shape.size)))
    }

    @OptIn(PerformancePitfall::class, UnsafeKMathAPI::class)
    override fun StructureND<T>.map(transform: A.(T) -> T): MultikTensor<T> = if (this is MultikTensor) {
        val data = initMemoryView<T>(array.size, type)
        var count = 0
        for (el in array) data[count++] = elementAlgebra.transform(el)
        NDArray(data, shape = shape.asArray(), dim = array.dim).wrap()
    } else {
        structureND(shape) { index ->
            transform(get(index))
        }
    }

    @OptIn(PerformancePitfall::class)
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

    /**
     * Transform a structure element-by element in place.
     */
    @OptIn(PerformancePitfall::class)
    public inline fun <T> MutableStructureND<T>.mapIndexedInPlace(operation: (index: IntArray, t: T) -> T): Unit {
        if (this is MultikTensor) {
            array.multiIndices.iterator().forEach {
                set(it, operation(it, get(it)))
            }
        } else {
            indices.forEach { set(it, operation(it, get(it))) }
        }
    }


    @OptIn(PerformancePitfall::class)
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
    @OptIn(UnsafeKMathAPI::class, PerformancePitfall::class)
    public fun StructureND<T>.asMultik(): MultikTensor<T> = if (this is MultikTensor) {
        this
    } else {
        val res = mk.zeros<T, DN>(shape.asArray(), type).asDNArray()
        for (index in res.multiIndices) {
            res[index] = this[index]
        }
        res.wrap()
    }

    public fun MutableMultiArray<T, *>.wrap(): MultikTensor<T> = MultikTensor(this.asDNArray())

    @OptIn(PerformancePitfall::class)
    override fun StructureND<T>.valueOrNull(): T? = if (shape contentEquals ShapeND(1)) {
        get(intArrayOf(0))
    } else null

    override fun T.plus(arg: StructureND<T>): MultikTensor<T> =
        arg.plus(this)

    override fun StructureND<T>.plus(arg: T): MultikTensor<T> =
        asMultik().array.deepCopy().apply { plusAssign(arg) }.wrap()

    override fun StructureND<T>.plus(arg: StructureND<T>): MultikTensor<T> =
        asMultik().array.plus(arg.asMultik().array).wrap()

    override fun Tensor<T>.plusAssign(value: T) {
        if (this is MultikTensor) {
            array.plusAssign(value)
        } else {
            mapIndexedInPlace { _, t -> elementAlgebra.add(t, value) }
        }
    }

    @OptIn(PerformancePitfall::class)
    override fun Tensor<T>.plusAssign(arg: StructureND<T>) {
        if (this is MultikTensor) {
            array.plusAssign(arg.asMultik().array)
        } else {
            mapIndexedInPlace { index, t -> elementAlgebra.add(t, arg[index]) }
        }
    }

    override fun T.minus(arg: StructureND<T>): MultikTensor<T> = (-(arg.asMultik().array - this)).wrap()

    override fun StructureND<T>.minus(arg: T): MultikTensor<T> =
        asMultik().array.deepCopy().apply { minusAssign(arg) }.wrap()

    override fun StructureND<T>.minus(arg: StructureND<T>): MultikTensor<T> =
        asMultik().array.minus(arg.asMultik().array).wrap()

    override fun Tensor<T>.minusAssign(value: T) {
        if (this is MultikTensor) {
            array.minusAssign(value)
        } else {
            mapIndexedInPlace { _, t -> elementAlgebra.run { t - value } }
        }
    }

    @OptIn(PerformancePitfall::class)
    override fun Tensor<T>.minusAssign(arg: StructureND<T>) {
        if (this is MultikTensor) {
            array.minusAssign(arg.asMultik().array)
        } else {
            mapIndexedInPlace { index, t -> elementAlgebra.run { t - arg[index] } }
        }
    }

    override fun T.times(arg: StructureND<T>): MultikTensor<T> =
        arg.asMultik().array.deepCopy().apply { timesAssign(this@times) }.wrap()

    override fun StructureND<T>.times(arg: T): Tensor<T> =
        asMultik().array.deepCopy().apply { timesAssign(arg) }.wrap()

    override fun StructureND<T>.times(arg: StructureND<T>): MultikTensor<T> =
        asMultik().array.times(arg.asMultik().array).wrap()

    override fun Tensor<T>.timesAssign(value: T) {
        if (this is MultikTensor) {
            array.timesAssign(value)
        } else {
            mapIndexedInPlace { _, t -> elementAlgebra.multiply(t, value) }
        }
    }

    @OptIn(PerformancePitfall::class)
    override fun Tensor<T>.timesAssign(arg: StructureND<T>) {
        if (this is MultikTensor) {
            array.timesAssign(arg.asMultik().array)
        } else {
            mapIndexedInPlace { index, t -> elementAlgebra.multiply(t, arg[index]) }
        }
    }

    override fun StructureND<T>.unaryMinus(): MultikTensor<T> =
        asMultik().array.unaryMinus().wrap()

    override fun Tensor<T>.getTensor(i: Int): MultikTensor<T> = asMultik().array.mutableView(i).wrap()

    override fun StructureND<T>.transposed(i: Int, j: Int): MultikTensor<T> = asMultik().array.transpose(i, j).wrap()

    override fun Tensor<T>.view(shape: ShapeND): MultikTensor<T> {
        require(shape.asList().all { it > 0 })
        require(shape.linearSize == this.shape.size) {
            "Cannot reshape array of size ${this.shape.size} into a new shape ${
                shape.asList().joinToString(
                    prefix = "(",
                    postfix = ")"
                )
            }"
        }

        val mt = asMultik().array
        return if (ShapeND(mt.shape).contentEquals(shape)) {
            mt
        } else {
            @OptIn(UnsafeKMathAPI::class)
            NDArray(mt.data, mt.offset, shape.asArray(), dim = DN(shape.size), base = mt.base ?: mt)
        }.wrap()
    }

    override fun Tensor<T>.viewAs(other: StructureND<T>): MultikTensor<T> = view(other.shape)

    public abstract fun scalar(value: T): MultikTensor<T>

    override fun StructureND<T>.dot(other: StructureND<T>): MultikTensor<T> =
        if (this.shape.size == 1 && other.shape.size == 1) {
            scalar(
                multikLinAl.linAlgEx.dotVV(
                    asMultik().array.asD1Array(), other.asMultik().array.asD1Array()
                )
            )
        } else if (this.shape.size == 2 && other.shape.size == 2) {
            multikLinAl.linAlgEx.dotMM(asMultik().array.asD2Array(), other.asMultik().array.asD2Array()).wrap()
        } else if (this.shape.size == 2 && other.shape.size == 1) {
            multikLinAl.linAlgEx.dotMV(asMultik().array.asD2Array(), other.asMultik().array.asD1Array()).wrap()
        } else {
            TODO("Not implemented for broadcasting")
        }

    override fun diagonalEmbedding(diagonalEntries: StructureND<T>, offset: Int, dim1: Int, dim2: Int): MultikTensor<T> {

        TODO("Diagonal embedding not implemented")
    }

    override fun StructureND<T>.sum(): T = multikMath.sum(asMultik().array)

    override fun StructureND<T>.sum(dim: Int, keepDim: Boolean): MultikTensor<T> {
        if (keepDim) TODO("keepDim not implemented")
        return multikMath.sumDN(asMultik().array, dim).wrap()
    }

    override fun StructureND<T>.min(): T? = asMultik().array.min()

    override fun StructureND<T>.min(dim: Int, keepDim: Boolean): Tensor<T> {
        if (keepDim) TODO("keepDim not implemented")
        return multikMath.minDN(asMultik().array, dim).wrap()
    }

    override fun StructureND<T>.argMin(dim: Int, keepDim: Boolean): Tensor<Int> {
        if (keepDim) TODO("keepDim not implemented")
        val res = multikMath.argMinDN(asMultik().array, dim)
        return with(MultikIntAlgebra(multikEngine)) { res.wrap() }
    }

    override fun StructureND<T>.max(): T? = asMultik().array.max()

    override fun StructureND<T>.max(dim: Int, keepDim: Boolean): Tensor<T> {
        if (keepDim) TODO("keepDim not implemented")
        return multikMath.maxDN(asMultik().array, dim).wrap()
    }

    override fun StructureND<T>.argMax(dim: Int, keepDim: Boolean): Tensor<Int> {
        if (keepDim) TODO("keepDim not implemented")
        val res = multikMath.argMaxDN(asMultik().array, dim)
        return with(MultikIntAlgebra(multikEngine)) { res.wrap() }
    }
}

public abstract class MultikDivisionTensorAlgebra<T, A : Field<T>>(
    multikEngine: Engine,
) : MultikTensorAlgebra<T, A>(multikEngine), TensorPartialDivisionAlgebra<T, A> where T : Number, T : Comparable<T> {

    @OptIn(UnsafeKMathAPI::class)
    override fun T.div(arg: StructureND<T>): MultikTensor<T> =
        Multik.ones<T, DN>(arg.shape.asArray(), type).apply { divAssign(arg.asMultik().array) }.wrap()

    override fun StructureND<T>.div(arg: T): MultikTensor<T> =
        asMultik().array.div(arg).wrap()

    override fun StructureND<T>.div(arg: StructureND<T>): MultikTensor<T> =
        asMultik().array.div(arg.asMultik().array).wrap()

    override fun Tensor<T>.divAssign(value: T) {
        if (this is MultikTensor) {
            array.divAssign(value)
        } else {
            mapIndexedInPlace { _, t -> elementAlgebra.divide(t, value) }
        }
    }

    @OptIn(PerformancePitfall::class)
    override fun Tensor<T>.divAssign(arg: StructureND<T>) {
        if (this is MultikTensor) {
            array.divAssign(arg.asMultik().array)
        } else {
            mapIndexedInPlace { index, t -> elementAlgebra.divide(t, arg[index]) }
        }
    }
}