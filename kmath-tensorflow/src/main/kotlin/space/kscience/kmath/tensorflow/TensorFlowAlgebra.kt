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
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.api.TensorAlgebra

internal fun IntArray.toLongArray() = LongArray(size) { get(it).toLong() }
internal fun LongArray.toIntArray() = IntArray(size) { get(it).toInt() }

internal val <T> NdArray<T>.scalar: T get() = getObject()


public sealed interface TensorFlowTensor<T> : Tensor<T>

@JvmInline
public value class TensorFlowArray<T>(public val tensor: NdArray<T>) : Tensor<T> {
    override val shape: Shape get() = tensor.shape().asArray().toIntArray()

    override fun get(index: IntArray): T = tensor.getObject(*index.toLongArray())

    //TODO implement native element sequence

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


public abstract class TensorFlowAlgebra<T, TT : TType, A: Ring<T>> internal constructor(
    protected val graph: Graph
) : TensorAlgebra<T,A> {

    protected val ops: Ops by lazy { Ops.create(graph) }

    protected abstract fun StructureND<T>.asTensorFlow(): TensorFlowOutput<T, TT>

    protected abstract fun Output<TT>.wrap(): TensorFlowOutput<T, TT>

    protected abstract fun const(value: T): Constant<TT>

    override fun StructureND<T>.valueOrNull(): T? = if (shape contentEquals intArrayOf(1))
        get(Shape(0)) else null

    private inline fun StructureND<T>.biOp(
        other: StructureND<T>,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>
    ): TensorFlowOutput<T, TT> {
        val left = asTensorFlow().output
        val right = other.asTensorFlow().output
        return operation(left, right).asOutput().wrap()
    }

    private inline fun T.biOp(
        other: StructureND<T>,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>
    ): TensorFlowOutput<T, TT> {
        val left = const(this)
        val right = other.asTensorFlow().output
        return operation(left, right).asOutput().wrap()
    }

    private inline fun StructureND<T>.biOp(
        value: T,
        operation: (left: Operand<TT>, right: Operand<TT>) -> Operand<TT>
    ): TensorFlowOutput<T, TT> {
        val left = asTensorFlow().output
        val right = const(value)
        return operation(left, right).asOutput().wrap()
    }

    private inline fun Tensor<T>.inPlaceOp(
        other: StructureND<T>,
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

    private inline fun unOp(value: StructureND<T>, operation: (Operand<TT>) -> Operand<TT>): TensorFlowOutput<T, TT> =
        operation(value.asTensorFlow().output).asOutput().wrap()

    override fun T.plus(arg: StructureND<T>): TensorFlowOutput<T, TT> = biOp(arg, ops.math::add)

    override fun StructureND<T>.plus(arg: T): TensorFlowOutput<T, TT> = biOp(arg, ops.math::add)

    override fun StructureND<T>.plus(arg: StructureND<T>): TensorFlowOutput<T, TT> = biOp(arg, ops.math::add)

    override fun Tensor<T>.plusAssign(value: T): Unit = inPlaceOp(value, ops.math::add)

    override fun Tensor<T>.plusAssign(arg: StructureND<T>): Unit = inPlaceOp(arg, ops.math::add)

    override fun StructureND<T>.minus(arg: T): TensorFlowOutput<T, TT> = biOp(arg, ops.math::sub)

    override fun StructureND<T>.minus(arg: StructureND<T>): TensorFlowOutput<T, TT> = biOp(arg, ops.math::sub)

    override fun T.minus(arg: StructureND<T>): Tensor<T> = biOp(arg, ops.math::sub)

    override fun Tensor<T>.minusAssign(value: T): Unit = inPlaceOp(value, ops.math::sub)

    override fun Tensor<T>.minusAssign(other: StructureND<T>): Unit = inPlaceOp(other, ops.math::sub)

    override fun T.times(arg: StructureND<T>): TensorFlowOutput<T, TT> = biOp(arg, ops.math::mul)

    override fun StructureND<T>.times(arg: T): TensorFlowOutput<T, TT> = biOp(arg, ops.math::mul)

    override fun StructureND<T>.times(other: StructureND<T>): TensorFlowOutput<T, TT> = biOp(other, ops.math::mul)

    override fun Tensor<T>.timesAssign(value: T): Unit = inPlaceOp(value, ops.math::mul)

    override fun Tensor<T>.timesAssign(arg: StructureND<T>): Unit = inPlaceOp(arg, ops.math::mul)

    override fun StructureND<T>.unaryMinus(): TensorFlowOutput<T, TT> = unOp(this, ops.math::neg)

    override fun StructureND<T>.get(i: Int): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.transpose(i: Int, j: Int): Tensor<T> = unOp(this) {
        ops.linalg.transpose(it, ops.constant(intArrayOf(i, j)))
    }

    override fun Tensor<T>.view(shape: IntArray): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun Tensor<T>.viewAs(other: StructureND<T>): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.dot(other: StructureND<T>): TensorFlowOutput<T, TT> = biOp(other) { l, r ->
        ops.linalg.matMul(
            if (l.asTensor().shape().numDimensions() == 1) ops.expandDims(l,ops.constant(0)) else l,
            if (r.asTensor().shape().numDimensions() == 1) ops.expandDims(r,ops.constant(-1)) else r)
    }

    override fun diagonalEmbedding(diagonalEntries: Tensor<T>, offset: Int, dim1: Int, dim2: Int): Tensor<T> = ops.run {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.sum(): T = TODO("Not yet implemented")

    override fun StructureND<T>.sum(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.min(): T {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.min(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.max(): T {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.max(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }

    override fun StructureND<T>.argMax(dim: Int, keepDim: Boolean): Tensor<T> {
        TODO("Not yet implemented")
    }
}