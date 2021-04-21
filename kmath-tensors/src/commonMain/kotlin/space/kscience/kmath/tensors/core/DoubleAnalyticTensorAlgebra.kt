package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.TensorStructure
import kotlin.math.*

public class DoubleAnalyticTensorAlgebra:
    AnalyticTensorAlgebra<Double>,
        DoubleTensorAlgebra()
{
    override fun TensorStructure<Double>.exp(): DoubleTensor = tensor.map(::exp)

    override fun TensorStructure<Double>.log(): DoubleTensor = tensor.map(::ln)

    override fun TensorStructure<Double>.sqrt(): DoubleTensor = tensor.map(::sqrt)

    override fun TensorStructure<Double>.cos(): DoubleTensor = tensor.map(::cos)

    override fun TensorStructure<Double>.acos(): DoubleTensor = tensor.map(::acos)

    override fun TensorStructure<Double>.cosh(): DoubleTensor = tensor.map(::cosh)

    override fun TensorStructure<Double>.acosh(): DoubleTensor = tensor.map(::acosh)

    override fun TensorStructure<Double>.sin(): DoubleTensor = tensor.map(::sin)

    override fun TensorStructure<Double>.asin(): DoubleTensor = tensor.map(::asin)

    override fun TensorStructure<Double>.sinh(): DoubleTensor = tensor.map(::sinh)

    override fun TensorStructure<Double>.asinh(): DoubleTensor = tensor.map(::asinh)

    override fun TensorStructure<Double>.tan(): DoubleTensor = tensor.map(::tan)

    override fun TensorStructure<Double>.atan(): DoubleTensor = tensor.map(::atan)

    override fun TensorStructure<Double>.tanh(): DoubleTensor = tensor.map(::tanh)

    override fun TensorStructure<Double>.atanh(): DoubleTensor = tensor.map(::atanh)

    override fun TensorStructure<Double>.ceil(): DoubleTensor = tensor.map(::ceil)

    override fun TensorStructure<Double>.floor(): DoubleTensor = tensor.map(::floor)

}

public inline fun <R> DoubleAnalyticTensorAlgebra(block: DoubleAnalyticTensorAlgebra.() -> R): R =
    DoubleAnalyticTensorAlgebra().block()