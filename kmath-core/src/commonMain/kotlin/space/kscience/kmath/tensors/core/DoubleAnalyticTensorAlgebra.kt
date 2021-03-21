package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.AnalyticTensorAlgebra
import kotlin.math.*

public class DoubleAnalyticTensorAlgebra:
    AnalyticTensorAlgebra<Double, DoubleTensor>,
    DoubleOrderedTensorAlgebra()
{
    override fun DoubleTensor.exp(): DoubleTensor = this.map(::exp)

    // todo log with other base????
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

    override fun DoubleTensor.clamp(min: Double, max: Double): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.erf(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.erfinv(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.erfc(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.lerp(end: DoubleTensor, weight: DoubleTensor): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.lgamma(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.logit(eps: Double): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.igamma(other: DoubleTensor): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.igammac(other: DoubleTensor): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.mvlgamma(dimensions: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.polygamma(order: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.pow(exponent: Double): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.round(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.sigmoid(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.sinc(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.heaviside(values: DoubleTensor): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.trapz(xValues: DoubleTensor, dim: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.quantile(q: Double, dim: Int, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.std(dim: Int, unbiased: Boolean, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.histc(bins: Int, min: Double, max: Double): DoubleTensor {
        TODO("Not yet implemented")
    }

}

public inline fun <R> DoubleAnalyticTensorAlgebra(block: DoubleAnalyticTensorAlgebra.() -> R): R =
    DoubleAnalyticTensorAlgebra().block()