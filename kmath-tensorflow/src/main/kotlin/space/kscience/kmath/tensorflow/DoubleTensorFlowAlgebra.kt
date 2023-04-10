/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensorflow

import org.tensorflow.Graph
import org.tensorflow.Output
import org.tensorflow.ndarray.NdArray
import org.tensorflow.op.core.Constant
import org.tensorflow.types.TFloat64
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.nd.ColumnStrides
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.PowerOperations

public class DoubleTensorFlowOutput(
    graph: Graph,
    output: Output<TFloat64>,
) : TensorFlowOutput<Double, TFloat64>(graph, output) {

    override fun org.tensorflow.Tensor.actualizeTensor(): NdArray<Double> = this as TFloat64

}

internal fun ShapeND.toLongArray(): LongArray = LongArray(size) { get(it).toLong() }

public class DoubleTensorFlowAlgebra internal constructor(
    graph: Graph,
) : TensorFlowAlgebra<Double, TFloat64, DoubleField>(graph), PowerOperations<StructureND<Double>> {

    override val elementAlgebra: DoubleField get() = DoubleField

    override fun structureND(
        shape: ShapeND,
        initializer: DoubleField.(IntArray) -> Double,
    ): StructureND<Double> {
        val res = TFloat64.tensorOf(org.tensorflow.ndarray.Shape.of(*shape.toLongArray())) { array ->
            ColumnStrides(shape).forEach { index ->
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

    override fun divide(
        left: StructureND<Double>,
        right: StructureND<Double>,
    ): TensorFlowOutput<Double, TFloat64> = left.operate(right) { l, r ->
        ops.math.div(l, r)
    }

    override fun power(arg: StructureND<Double>, pow: Number): TensorFlowOutput<Double, TFloat64> =
        arg.operate { ops.math.pow(it, const(pow.toDouble())) }
}

/**
 * Compute a tensor with TensorFlow in a single run.
 *
 * The resulting tensor is available outside of scope
 */
@UnstableKMathAPI
public fun DoubleField.produceWithTF(
    block: DoubleTensorFlowAlgebra.() -> StructureND<Double>,
): StructureND<Double> = Graph().use { graph ->
    val scope = DoubleTensorFlowAlgebra(graph)
    scope.export(scope.block())
}

/**
 * Compute several outputs with TensorFlow in a single run.
 *
 * The resulting tensors are available outside of scope
 */
@OptIn(UnstableKMathAPI::class)
public fun DoubleField.produceMapWithTF(
    block: DoubleTensorFlowAlgebra.() -> Map<Symbol, StructureND<Double>>,
): Map<Symbol, StructureND<Double>> = Graph().use { graph ->
    val scope = DoubleTensorFlowAlgebra(graph)
    scope.block().mapValues { scope.export(it.value) }
}