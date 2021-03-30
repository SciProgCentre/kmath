package space.kscience.kmath.optimization

import space.kscience.kmath.misc.Symbol

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
 * An optimization problem builder over [T] variables
 */
public interface Optimization<T : Any> {

    /**
     * Update the problem from previous optimization run
     */
    public fun update(result: OptimizationResult<T>)

    /**
     * Make an optimization run
     */
    public fun optimize(): OptimizationResult<T>
}

public fun interface OptimizationProblemFactory<T : Any, out P : Optimization<T>> {
    public fun build(symbols: List<Symbol>): P
}

public operator fun <T : Any, P : Optimization<T>> OptimizationProblemFactory<T, P>.invoke(
    symbols: List<Symbol>,
    block: P.() -> Unit,
): P = build(symbols).apply(block)
