package space.kscience.kmath.stat

import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.Symbol

public interface OptimizationFeature

public class OptimizationResult<T>(
    public val point: Map<Symbol, T>,
    public val value: T,
    public val features: Set<OptimizationFeature> = emptySet(),
) {
    override fun toString(): String {
        return "OptimizationResult(point=$point, value=$value)"
    }
}

public operator fun <T> OptimizationResult<T>.plus(
    feature: OptimizationFeature,
): OptimizationResult<T> = OptimizationResult(point, value, features + feature)

/**
 * A configuration builder for optimization problem
 */
public interface OptimizationProblem<T : Any> {
    /**
     * Define the initial guess for the optimization problem
     */
    public fun initialGuess(map: Map<Symbol, T>)

    /**
     * Set an objective function expression
     */
    public fun expression(expression: Expression<T>)

    /**
     * Set a differentiable expression as objective function as function and gradient provider
     */
    public fun diffExpression(expression: DifferentiableExpression<T, Expression<T>>)

    /**
     * Update the problem from previous optimization run
     */
    public fun update(result: OptimizationResult<T>)

    /**
     * Make an optimization run
     */
    public fun optimize(): OptimizationResult<T>
}

public fun interface OptimizationProblemFactory<T : Any, out P : OptimizationProblem<T>> {
    public fun build(symbols: List<Symbol>): P
}

public operator fun <T : Any, P : OptimizationProblem<T>> OptimizationProblemFactory<T, P>.invoke(
    symbols: List<Symbol>,
    block: P.() -> Unit,
): P = build(symbols).apply(block)

/**
 * Optimize expression without derivatives using specific [OptimizationProblemFactory]
 */
public fun <T : Any, F : OptimizationProblem<T>> Expression<T>.optimizeWith(
    factory: OptimizationProblemFactory<T, F>,
    vararg symbols: Symbol,
    configuration: F.() -> Unit,
): OptimizationResult<T> {
    require(symbols.isNotEmpty()) { "Must provide a list of symbols for optimization" }
    val problem = factory(symbols.toList(), configuration)
    problem.expression(this)
    return problem.optimize()
}

/**
 * Optimize differentiable expression using specific [OptimizationProblemFactory]
 */
public fun <T : Any, F : OptimizationProblem<T>> DifferentiableExpression<T, Expression<T>>.optimizeWith(
    factory: OptimizationProblemFactory<T, F>,
    vararg symbols: Symbol,
    configuration: F.() -> Unit,
): OptimizationResult<T> {
    require(symbols.isNotEmpty()) { "Must provide a list of symbols for optimization" }
    val problem = factory(symbols.toList(), configuration)
    problem.diffExpression(this)
    return problem.optimize()
}
