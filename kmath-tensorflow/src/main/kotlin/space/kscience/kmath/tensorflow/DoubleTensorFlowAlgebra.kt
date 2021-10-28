package space.kscience.kmath.tensorflow

import org.tensorflow.Graph
import org.tensorflow.Output
import org.tensorflow.ndarray.NdArray
import org.tensorflow.op.core.Constant
import org.tensorflow.types.TFloat64
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.DoubleField

public class DoubleTensorFlowOutput(
    graph: Graph,
    output: Output<TFloat64>
) : TensorFlowOutput<Double, TFloat64>(graph, output) {
    override fun org.tensorflow.Tensor.actualizeTensor(): NdArray<Double> = output.asTensor()
}

public class DoubleTensorFlowAlgebra internal constructor(
    graph: Graph
) : TensorFlowAlgebra<Double, TFloat64, DoubleField>(graph) {

    override val elementAlgebra: DoubleField get() = DoubleField

    override fun structureND(
        shape: Shape,
        initializer: DoubleField.(IntArray) -> Double
    ): StructureND<Double> {
        val res = TFloat64.tensorOf(org.tensorflow.ndarray.Shape.of(*shape.toLongArray())) { array ->
            DefaultStrides(shape).forEach { index ->
                array.setDouble(elementAlgebra.initializer(index), *index.toLongArray())
            }
        }
        return DoubleTensorFlowOutput(graph, ops.constant(res).asOutput())
    }

    override fun StructureND<Double>.asTensorFlow(): TensorFlowOutput<Double, TFloat64> =
        if (this is TensorFlowOutput<Double, *> && output.type() == TFloat64::class.java) {
            @Suppress("UNCHECKED_CAST")
            this as TensorFlowOutput<Double, TFloat64>
        } else {
            val res = TFloat64.tensorOf(org.tensorflow.ndarray.Shape.of(*shape.toLongArray())) { array ->
                @OptIn(PerformancePitfall::class)
                elements().forEach { (index, value) ->
                    array.setDouble(value, *index.toLongArray())
                }
            }
            DoubleTensorFlowOutput(graph, ops.constant(res).asOutput())
        }

    override fun Output<TFloat64>.wrap(): TensorFlowOutput<Double, TFloat64> = DoubleTensorFlowOutput(graph, this)

    override fun const(value: Double): Constant<TFloat64> = ops.constant(value)
}