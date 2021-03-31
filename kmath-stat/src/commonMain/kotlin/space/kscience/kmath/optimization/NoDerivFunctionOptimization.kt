package space.kscience.kmath.optimization

import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices
import kotlin.math.pow

/**
 * A likelihood function optimization problem
 */
public interface NoDerivFunctionOptimization<T : Any> : Optimization<T> {
    /**
     * The optimization direction. If true search for function maximum, if false, search for the minimum
     */
    public var maximize: Boolean

    /**
     * Define the initial guess for the optimization problem
     */
    public fun initialGuess(map: Map<Symbol, T>)

    /**
     * Set an objective function expression
     */
    public fun function(expression: Expression<T>)

    public companion object {
        /**
         * Generate a chi squared expression from given x-y-sigma model represented by an expression. Does not provide derivatives
         */
        public fun chiSquared(
            x: Buffer<Double>,
            y: Buffer<Double>,
            yErr: Buffer<Double>,
            model: Expression<Double>,
            xSymbol: Symbol = Symbol.x,
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
}


/**
 * Optimize expression without derivatives using specific [OptimizationProblemFactory]
 */
public fun <T : Any, F : NoDerivFunctionOptimization<T>> Expression<T>.noDerivOptimizeWith(
    factory: OptimizationProblemFactory<T, F>,
    vararg symbols: Symbol,
    configuration: F.() -> Unit,
): OptimizationResult<T> {
    require(symbols.isNotEmpty()) { "Must provide a list of symbols for optimization" }
    val problem = factory(symbols.toList(), configuration)
    problem.function(this)
    return problem.optimize()
}
