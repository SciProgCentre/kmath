package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.ReduceOpsTensorAlgebra

public class DoubleReduceOpsTensorAlgebra:
    DoubleTensorAlgebra(),
    ReduceOpsTensorAlgebra<Double, DoubleTensor> {

    override fun DoubleTensor.value(): Double {
        check(this.shape contentEquals intArrayOf(1)) {
            "Inconsistent value for tensor of shape ${shape.toList()}"
        }
        return this.buffer.array()[this.bufferStart]
    }
}

public inline fun <R> DoubleReduceOpsTensorAlgebra(block: DoubleReduceOpsTensorAlgebra.() -> R): R =
    DoubleReduceOpsTensorAlgebra().block()