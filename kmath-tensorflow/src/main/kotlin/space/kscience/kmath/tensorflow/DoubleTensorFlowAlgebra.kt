package space.kscience.kmath.tensorflow

import org.tensorflow.Graph
import org.tensorflow.Output
import org.tensorflow.ndarray.NdArray
import org.tensorflow.ndarray.Shape
import org.tensorflow.op.core.Constant
import org.tensorflow.types.TFloat64
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.tensors.api.Tensor

public class DoubleTensorFlowOutput(
    graph: Graph,
    output: Output<TFloat64>
) : TensorFlowOutput<Double, TFloat64>(graph, output) {
    override fun org.tensorflow.Tensor.actualizeTensor(): NdArray<Double> = output.asTensor()
}

public class DoubleTensorFlowAlgebra internal constructor(
    graph: Graph
) : TensorFlowAlgebra<Double, TFloat64>(graph) {

    override fun Tensor<Double>.asTensorFlow(): TensorFlowOutput<Double, TFloat64> =
        if (this is TensorFlowOutput<Double, *> && output.type() == TFloat64::class.java) {
            @Suppress("UNCHECKED_CAST")
            this as TensorFlowOutput<Double, TFloat64>
        } else {
            val res = TFloat64.tensorOf(Shape.of(*shape.toLongArray())) { array ->
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