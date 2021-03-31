package space.kscience.kmath.optimization

import space.kscience.kmath.expressions.AutoDiffProcessor
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.ExpressionAlgebra
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices

/**
 * A likelihood function optimization problem with provided derivatives
 */
public interface FunctionOptimization<T : Any> : Optimization<T> {
    /**
     * The optimization direction. If true search for function maximum, if false, search for the minimum
     */
    public var maximize: Boolean

    /**
     * Define the initial guess for the optimization problem
     */
    public fun initialGuess(map: Map<Symbol, T>)

    /**
     * Set a differentiable expression as objective function as function and gradient provider
     */
    public fun diffFunction(expression: DifferentiableExpression<T, Expression<T>>)

    public companion object {
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
    }
}

/**
 * Define a chi-squared-based objective function
 */
public fun <T: Any, I : Any, A> FunctionOptimization<T>.chiSquared(
    autoDiff: AutoDiffProcessor<T, I, A, Expression<T>>,
    x: Buffer<T>,
    y: Buffer<T>,
    yErr: Buffer<T>,
    model: A.(I) -> I,
) where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> {
    val chiSquared = FunctionOptimization.chiSquared(autoDiff, x, y, yErr, model)
    diffFunction(chiSquared)
    maximize = false
}

/**
 * Optimize differentiable expression using specific [OptimizationProblemFactory]
 */
public fun <T : Any, F : FunctionOptimization<T>> DifferentiableExpression<T, Expression<T>>.optimizeWith(
    factory: OptimizationProblemFactory<T, F>,
    vararg symbols: Symbol,
    configuration: F.() -> Unit,
): OptimizationResult<T> {
    require(symbols.isNotEmpty()) { "Must provide a list of symbols for optimization" }
    val problem = factory(symbols.toList(), configuration)
    problem.diffFunction(this)
    return problem.optimize()
}
