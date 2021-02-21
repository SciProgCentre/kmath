package space.kscience.kmath.stat

import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices
import kotlin.math.pow

public object Fitting {

    /**
     * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic differentiation
     */
    public fun <T : Any, I : Any, A> chiSquared(
        autoDiff: AutoDiffProcessor<T, I, A, Expression<T>>,
        x: Buffer<T>,
        y: Buffer<T>,
        yErr: Buffer<T>,
        model: A.(I) -> I,
    ): DifferentiableExpression<T, Expression<T>> where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> {
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

    /**
     * Generate a chi squared expression from given x-y-sigma model represented by an expression. Does not provide derivatives
     */
    public fun chiSquared(
        x: Buffer<Double>,
        y: Buffer<Double>,
        yErr: Buffer<Double>,
        model: Expression<Double>,
        xSymbol: Symbol = StringSymbol("x"),
    ): Expression<Double> {
        require(x.size == y.size) { "X and y buffers should be of the same size" }
        require(y.size == yErr.size) { "Y and yErr buffer should of the same size" }

        return Expression { arguments ->
            x.indices.sumByDouble {
                val xValue = x[it]
                val yValue = y[it]
                val yErrValue = yErr[it]
                val modifiedArgs = arguments + (xSymbol to xValue)
                val modelValue = model(modifiedArgs)
                ((yValue - modelValue) / yErrValue).pow(2)
            }
        }
    }
}
