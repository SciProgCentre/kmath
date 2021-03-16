package space.kscience.kmath.tensors

public class DoubleAnalyticTensorAlgebra:
    AnalyticTensorAlgebra<Double, DoubleTensor>,
    DoubleOrderedTensorAlgebra()
{
    override fun DoubleTensor.exp(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.log(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.sqrt(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.cos(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.acos(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.cosh(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.acosh(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.sin(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.asin(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.sinh(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.asinh(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.tan(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.atan(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.tanh(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.atanh(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.ceil(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.floor(): DoubleTensor {
        TODO("Not yet implemented")
    }

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