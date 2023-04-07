/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensorflow


import org.tensorflow.Graph
import org.tensorflow.Operand
import org.tensorflow.Output
import org.tensorflow.Session
import org.tensorflow.ndarray.NdArray
import org.tensorflow.ndarray.index.Indices
import org.tensorflow.op.Ops
import org.tensorflow.op.core.*
import org.tensorflow.types.TInt32
import org.tensorflow.types.family.TNumber
import org.tensorflow.types.family.TType
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnsafeKMathAPI
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.asArray
import space.kscience.kmath.nd.contentEquals
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra

internal fun IntArray.toLongArray() = LongArray(size) { get(it).toLong() }
internal fun LongArray.toIntArray() = IntArray(size) { get(it).toInt() }

internal val <T> NdArray<T>.scalar: T get() = getObject()


public sealed interface TensorFlowTensor<T> : Tensor<T>

/**
 * Static (eager) in-memory TensorFlow tensor
 */
@JvmInline
public value class TensorFlowArray<T>(public val tensor: NdArray<T>) : Tensor<T> {
    override val shape: ShapeND get() = ShapeND(tensor.shape().asArray().toIntArray())

    @PerformancePitfall
    override fun get(index: IntArray): T = tensor.getObject(*index.toLongArray())

    //TODO implement native element sequence

    @PerformancePitfall
    override fun set(index: IntArray, value: T) {
        tensor.setObject(value, *index.toLongArray())
    }
}

/**
 * Lazy graph-based TensorFlow tensor. The tensor is actualized on call.
 *
 * If the tensor is used for intermediate operations, actualizing it could impact performance.
 */
public abstract class TensorFlowOutput<T, TT : TType>(
    protected val graph: Graph,
    output: Output<TT>,
) : TensorFlowTensor<T> {

    public var output: Output<TT> = output
        internal set

    override val shape: ShapeND get() = ShapeND(output.shape().asArray().toIntArray())

    protected abstract fun org.tensorflow.Tensor.actualizeTensor(): NdArray<T>

    internal val actualTensor by lazy {
        Session(graph).use { session ->
            TensorFlowArray(session.runner().fetch(output).run().first().actualizeTensor())
        }
    }

    @PerformancePitfall
    override fun get(index: IntArray): T = actualTensor[index]

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = actualTensor.elements()

    @PerformancePitfall
    override fun set(index: IntArray, value: T) {
        actualTensor[index] = value
    }

}


public abstract class TensorFlowAlgebra<T, TT : TNumber, A : Ring<T>> internal constructor(
    protected val graph: Graph,
) : TensorAlgebra<T, A> {

    public val ops: Ops by lazy { Ops.create(graph) }

    protected abstract fun StructureND<T>.asTensorFlow(): TensorFlowOutput<T, TT>

    protected abstract fun Output<TT>.wrap(): TensorFlowOutput<T, TT>

    protected abstract fun const(value: T): Constant<TT>

    @OptIn(PerformancePitfall::class)
    override fun StructureND<T>.valueOrNull(): T? = if (shape contentEquals ShapeND(1))
        get(intArrayOf(0)) else null

    /**
     * Perform binary lazy operation on tensor. Both arguments are implicitly converted
     */
    public fun StructureND<T>.operate(
        other: StructureND<T>,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>,
    ): TensorFlowOutput<T, TT> {
        val left = asTensorFlow().output
        val right = other.asTensorFlow().output
        return operation(left, right).asOutput().wrap()
    }

    public fun T.operate(
        other: StructureND<T>,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>,
    ): TensorFlowOutput<T, TT> {
        val left = const(this)
        val right = other.asTensorFlow().output
        return operation(left, right).asOutput().wrap()
    }

    public fun StructureND<T>.operate(
        value: T,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>,
    ): TensorFlowOutput<T, TT> {
        val left = asTensorFlow().output
        val right = const(value)
        return operation(left, right).asOutput().wrap()
    }

    public fun Tensor<T>.operateInPlace(
        other: StructureND<T>,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>,
    ): Unit {
        val origin = asTensorFlow()
        val left = origin.output
        val right = other.asTensorFlow().output
        origin.output = operation(left, right).asOutput()
    }

    public fun Tensor<T>.operateInPlace(
        value: T,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>,
    ): Unit {
        val origin = asTensorFlow()
        val left = origin.output
        val right = const(value)
        origin.output = operation(left, right).asOutput()
    }

    public fun StructureND<T>.operate(operation: (Operand<TT>) -> Operand<TT>): TensorFlowOutput<T, TT> =
        operation(asTensorFlow().output).asOutput().wrap()

    override fun T.plus(arg: StructureND<T>): TensorFlowOutput<T, TT> = operate(arg, ops.math::add)

    override fun StructureND<T>.plus(arg: T): TensorFlowOutput<T, TT> = operate(arg, ops.math::add)

    override fun StructureND<T>.plus(arg: StructureND<T>): TensorFlowOutput<T, TT> = operate(arg, ops.math::add)

    override fun Tensor<T>.plusAssign(value: T): Unit = operateInPlace(value, ops.math::add)

    override fun Tensor<T>.plusAssign(arg: StructureND<T>): Unit = operateInPlace(arg, ops.math::add)

    override fun StructureND<T>.minus(arg: T): TensorFlowOutput<T, TT> = operate(arg, ops.math::sub)

    override fun StructureND<T>.minus(arg: StructureND<T>): TensorFlowOutput<T, TT> = operate(arg, ops.math::sub)

    override fun T.minus(arg: StructureND<T>): Tensor<T> = operate(arg, ops.math::sub)

    override fun Tensor<T>.minusAssign(value: T): Unit = operateInPlace(value, ops.math::sub)

    override fun Tensor<T>.minusAssign(arg: StructureND<T>): Unit = operateInPlace(arg, ops.math::sub)

    override fun T.times(arg: StructureND<T>): TensorFlowOutput<T, TT> = operate(arg, ops.math::mul)

    override fun StructureND<T>.times(arg: T): TensorFlowOutput<T, TT> = operate(arg, ops.math::mul)

    override fun StructureND<T>.times(arg: StructureND<T>): TensorFlowOutput<T, TT> = operate(arg, ops.math::mul)

    override fun Tensor<T>.timesAssign(value: T): Unit = operateInPlace(value, ops.math::mul)

    override fun Tensor<T>.timesAssign(arg: StructureND<T>): Unit = operateInPlace(arg, ops.math::mul)

    override fun StructureND<T>.unaryMinus(): TensorFlowOutput<T, TT> = operate(ops.math::neg)

    override fun Tensor<T>.getTensor(i: Int): Tensor<T> = operate {
        StridedSliceHelper.stridedSlice(ops.scope(), it, Indices.at(i.toLong()))
    }

    override fun StructureND<T>.transposed(i: Int, j: Int): Tensor<T> = operate {
        ops.linalg.transpose(it, ops.constant(intArrayOf(i, j)))
    }

    override fun Tensor<T>.view(shape: ShapeND): Tensor<T> = operate {
        @OptIn(UnsafeKMathAPI::class)
        ops.reshape(it, ops.constant(shape.asArray()))
    }

    override fun Tensor<T>.viewAs(other: StructureND<T>): Tensor<T> = operate(other) { l, r ->
        ops.reshape(l, ops.shape(r))
    }

    override fun StructureND<T>.dot(other: StructureND<T>): TensorFlowOutput<T, TT> = operate(other) { l, r ->
        ops.linalg.matMul(
            if (l.shape().numDimensions() == 1) ops.expandDims(l, ops.constant(0)) else l,
            if (r.shape().numDimensions() == 1) ops.expandDims(r, ops.constant(-1)) else r
        )
    }

    override fun diagonalEmbedding(
        diagonalEntries: StructureND<T>,
        offset: Int,
        dim1: Int,
        dim2: Int,
    ): TensorFlowOutput<T, TT> = diagonalEntries.operate {
        ops.linalg.matrixDiagV3(
            /* diagonal = */ it,
            /* k = */ ops.constant(offset),
            /* numRows = */ ops.constant(dim1),
            /* numCols = */ ops.constant(dim2),
            /* paddingValue = */ const(elementAlgebra.zero)
        )
    }

    override fun StructureND<T>.sum(): T = operate {
        ops.sum(it, ops.constant(intArrayOf()))
    }.value()

    override fun StructureND<T>.sum(dim: Int, keepDim: Boolean): TensorFlowOutput<T, TT> = operate {
        ops.sum(it, ops.constant(dim), Sum.keepDims(keepDim))
    }

    override fun StructureND<T>.min(): T = operate {
        ops.min(it, ops.constant(intArrayOf()))
    }.value()

    override fun StructureND<T>.min(dim: Int, keepDim: Boolean): Tensor<T> = operate {
        ops.min(it, ops.constant(dim), Min.keepDims(keepDim))
    }

    override fun StructureND<T>.argMin(dim: Int, keepDim: Boolean): Tensor<Int> = IntTensorFlowOutput(
        graph,
        ops.math.argMin(asTensorFlow().output, ops.constant(dim), TInt32::class.java).output()
    ).actualTensor

    override fun StructureND<T>.max(): T = operate {
        ops.max(it, ops.constant(intArrayOf()))
    }.value()

    override fun StructureND<T>.max(dim: Int, keepDim: Boolean): Tensor<T> = operate {
        ops.max(it, ops.constant(dim), Max.keepDims(keepDim))
    }

    override fun StructureND<T>.argMax(dim: Int, keepDim: Boolean): Tensor<Int> = IntTensorFlowOutput(
        graph,
        ops.math.argMax(asTensorFlow().output, ops.constant(dim), TInt32::class.java).output()
    ).actualTensor

//    private val symbolCache = HashMap<String, TensorFlowOutput<T, TT>>()
//
//    override fun bindSymbolOrNull(value: String): TensorFlowOutput<T, TT>? {
//        return symbolCache.getOrPut(value){ops.var}
//    }
//
//    public fun StructureND<T>.grad(
//
//    )= operate { ops.gradients() }

    @OptIn(UnstableKMathAPI::class)
    override fun export(arg: StructureND<T>): StructureND<T> =
        if (arg is TensorFlowOutput<T, *>) arg.actualTensor else arg
}

//TODO add TensorFlow expressions