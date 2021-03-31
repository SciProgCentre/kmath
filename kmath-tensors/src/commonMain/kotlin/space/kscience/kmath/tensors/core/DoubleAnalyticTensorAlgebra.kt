package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.AnalyticTensorAlgebra
import kotlin.math.*

public class DoubleAnalyticTensorAlgebra:
    AnalyticTensorAlgebra<Double, DoubleTensor>,
        DoubleTensorAlgebra()
{
    override fun DoubleTensor.exp(): DoubleTensor = this.map(::exp)

    override fun DoubleTensor.log(): DoubleTensor = this.map(::ln)

    override fun DoubleTensor.sqrt(): DoubleTensor = this.map(::sqrt)

    override fun DoubleTensor.cos(): DoubleTensor = this.map(::cos)

    override fun DoubleTensor.acos(): DoubleTensor = this.map(::acos)

    override fun DoubleTensor.cosh(): DoubleTensor = this.map(::cosh)

    override fun DoubleTensor.acosh(): DoubleTensor = this.map(::acosh)

    override fun DoubleTensor.sin(): DoubleTensor = this.map(::sin)

    override fun DoubleTensor.asin(): DoubleTensor = this.map(::asin)

    override fun DoubleTensor.sinh(): DoubleTensor = this.map(::sinh)

    override fun DoubleTensor.asinh(): DoubleTensor = this.map(::asinh)

    override fun DoubleTensor.tan(): DoubleTensor = this.map(::tan)

    override fun DoubleTensor.atan(): DoubleTensor = this.map(::atan)

    override fun DoubleTensor.tanh(): DoubleTensor = this.map(::tanh)

    override fun DoubleTensor.atanh(): DoubleTensor = this.map(::atanh)

    override fun DoubleTensor.ceil(): DoubleTensor = this.map(::ceil)

    override fun DoubleTensor.floor(): DoubleTensor = this.map(::floor)

}

public inline fun <R> DoubleAnalyticTensorAlgebra(block: DoubleAnalyticTensorAlgebra.() -> R): R =
    DoubleAnalyticTensorAlgebra().block()