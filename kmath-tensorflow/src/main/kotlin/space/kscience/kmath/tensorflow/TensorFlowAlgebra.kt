package space.kscience.kmath.tensorflow


import org.tensorflow.Graph
import org.tensorflow.Operand
import org.tensorflow.Output
import org.tensorflow.Session
import org.tensorflow.ndarray.NdArray
import org.tensorflow.op.Ops
import org.tensorflow.op.core.Constant
import org.tensorflow.types.family.TType
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra

private fun IntArray.toLongArray() = LongArray(size) { get(it).toLong() }
private fun LongArray.toIntArray() = IntArray(size) { get(it).toInt() }

private val <T> NdArray<T>.scalar: T
    get() = getObject()


public sealed interface TensorFlowTensor<T> : Tensor<T>

@JvmInline
public value class TensorFlowArray<T>(public val tensor: NdArray<T>) : Tensor<T> {
    override val shape: Shape get() = tensor.shape().asArray().toIntArray()

    override fun get(index: IntArray): T = tensor.getObject(*index.toLongArray())

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = sequence {
        tensor.scalars().forEachIndexed { index: LongArray, ndArray: NdArray<T> ->
            //yield(index.toIntArray() to ndArray.scalar)
            TODO()
        }
    }

    override fun set(index: IntArray, value: T) {
        tensor.setObject(value, *index.toLongArray())
    }
}

public abstract class TensorFlowOutput<T, TT : TType>(
    private val graph: Graph,
    output: Output<TT>
) : TensorFlowTensor<T> {

    public var output: Output<TT> = output
        internal set

    override val shape: Shape get() = output.shape().asArray().toIntArray()

    protected abstract fun org.tensorflow.Tensor.actualizeTensor(): NdArray<T>

    private val actualTensor by lazy {
        val session = Session(graph)
        TensorFlowArray(session.runner().fetch(output).run().first().actualizeTensor())
    }

    override fun get(index: IntArray): T = actualTensor[index]

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = actualTensor.elements()

    override fun set(index: IntArray, value: T) {
        actualTensor[index] = value
    }

}


public abstract class TensorFlowAlgebra<T, TT : TType> internal constructor(
    private val graph: Graph
) : TensorAlgebra<T> {

    private val ops by lazy { Ops.create(graph) }

    protected fun Tensor<T>.asTensorFlow(): TensorFlowOutput<T, TT> = if (this is TensorFlowOutput<T, TT>) this else {
        TODO()
    }

    protected abstract fun Output<TT>.wrap(): TensorFlowOutput<T, TT>

    protected abstract fun const(value: T): Constant<TT>

    override fun Tensor<T>.valueOrNull(): T? = if (shape contentEquals intArrayOf(1))
        get(Shape(0)) else null

    private inline fun Tensor<T>.biOp(
        other: Tensor<T>,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>
    ): TensorFlowOutput<T, TT> {
        val left = asTensorFlow().output
        val right = other.asTensorFlow().output
        return operation(left, right).asOutput().wrap()
    }

    private inline fun T.biOp(
        other: Tensor<T>,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>
    ): TensorFlowOutput<T, TT> {
        val left = const(this)
        val right = other.asTensorFlow().output
        return operation(left, right).asOutput().wrap()
    }

    private inline fun Tensor<T>.biOp(
        value: T,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>
    ): TensorFlowOutput<T, TT> {
        val left = asTensorFlow().output
        val right = const(value)
        return operation(left, right).asOutput().wrap()
    }

    private inline fun Tensor<T>.inPlaceOp(
        other: Tensor<T>,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>
    ): Unit {
        val origin = asTensorFlow()
        val left = origin.output
        val right = other.asTensorFlow().output
        origin.output = operation(left, right).asOutput()
    }

    private inline fun Tensor<T>.inPlaceOp(
        value: T,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>
    ): Unit {
        val origin = asTensorFlow()
        val left = origin.output
        val right = const(value)
        origin.output = operation(left, right).asOutput()
    }

    private inline fun unOp(value: Tensor<T>, operation: (Operand<TT>) -> Operand<TT>): TensorFlowOutput<T, TT> =
        operation(value.asTensorFlow().output).asOutput().wrap()

    override fun T.plus(other: Tensor<T>) = biOp(other, ops.math::add)

    override fun Tensor<T>.plus(value: T) = biOp(value, ops.math::add)

    override fun Tensor<T>.plus(other: Tensor<T>) = biOp(other, ops.math::add)

    override fun Tensor<T>.plusAssign(value: T): Unit = inPlaceOp(value, ops.math::add)

    override fun Tensor<T>.plusAssign(other: Tensor<T>): Unit = inPlaceOp(other, ops.math::add)

    override fun Tensor<T>.minus(value: T) = biOp(value, ops.math::sub)

    override fun Tensor<T>.minus(other: Tensor<T>) = biOp(other, ops.math::sub)

    override fun Tensor<T>.minusAssign(value: T): Unit = inPlaceOp(value, ops.math::sub)

    override fun Tensor<T>.minusAssign(other: Tensor<T>): Unit = inPlaceOp(other, ops.math::sub)

    override fun T.times(other: Tensor<T>) = biOp(other, ops.math::mul)

    override fun Tensor<T>.times(value: T) = biOp(value, ops.math::mul)

    override fun Tensor<T>.times(other: Tensor<T>): TensorFlowOutput<T, TT> = biOp(other, ops.math::mul)

    override fun Tensor<T>.timesAssign(value: T): Unit = inPlaceOp(value, ops.math::mul)

    override fun Tensor<T>.timesAssign(other: Tensor<T>): Unit = inPlaceOp(other, ops.math::mul)

    override fun Tensor<T>.unaryMinus() = unOp(this, ops.math::neg)

    override fun Tensor<T>.get(i: Int): Tensor<T>{
        ops.
    }

    override fun Tensor<T>.transpose(i: Int, j: Int): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.view(shape: IntArray): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.viewAs(other: Tensor<T>): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.dot(other: Tensor<T>) = biOp(other, ops.math.)

    override fun diagonalEmbedding(diagonalEntries: Tensor<T>, offset: Int, dim1: Int, dim2: Int): Tensor<T> = ops.run {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.sum(): T {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.sum(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.min(): T {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.min(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.max(): T {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.max(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.argMax(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }
}