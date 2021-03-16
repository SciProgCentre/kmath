package space.kscience.kmath.tensors

public open class DoubleOrderedTensorAlgebra:
    OrderedTensorAlgebra<Double, DoubleTensor>,
    DoubleTensorAlgebra()
{
    override fun DoubleTensor.max(dim: Int, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.cummax(dim: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.min(dim: Int, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.cummin(dim: Int): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.median(dim: Int, keepDim: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun maximum(lhs: DoubleTensor, rhs: DoubleTensor) {
        TODO("Not yet implemented")
    }

    override fun minimum(lhs: DoubleTensor, rhs: DoubleTensor) {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.sort(dim: Int, keepDim: Boolean, descending: Boolean): DoubleTensor {
        TODO("Not yet implemented")
    }
}

public inline fun <R> DoubleOrderedTensorAlgebra(block: DoubleOrderedTensorAlgebra.() -> R): R =
    DoubleOrderedTensorAlgebra().block()