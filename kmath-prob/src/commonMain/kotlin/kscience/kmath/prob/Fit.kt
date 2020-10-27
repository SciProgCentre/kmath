package kscience.kmath.prob

import kscience.kmath.expressions.AutoDiffProcessor
import kscience.kmath.expressions.DifferentiableExpression
import kscience.kmath.expressions.ExpressionAlgebra
import kscience.kmath.operations.ExtendedField
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.indices

public object Fit {

    /**
     * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic differentiation
     */
    public fun <T : Any, I : Any, A> chiSquared(
        autoDiff: AutoDiffProcessor<T, I, A>,
        x: Buffer<T>,
        y: Buffer<T>,
        yErr: Buffer<T>,
        model: A.(I) -> I,
    ): DifferentiableExpression<T> where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> {
        require(x.size == y.size) { "X and y buffers should be of the same size" }
        require(y.size == yErr.size) { "Y and yErr buffer should of the same size" }
        return autoDiff.process {
            var sum = zero
            x.indices.forEach {
                val xValue = const(x[it])
                val yValue = const(y[it])
                val yErrValue = const(yErr[it])
                val modelValue = model(xValue)
                sum += ((yValue - modelValue) / yErrValue).pow(2)
            }
            sum
        }
    }
}