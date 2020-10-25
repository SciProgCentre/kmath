package kscience.kmath.commons.optimization

import kscience.kmath.expressions.Symbol
import kotlin.reflect.KClass

public typealias ParameterSpacePoint<T> = Map<Symbol, T>

public class OptimizationResult<T>(
    public val point: ParameterSpacePoint<T>,
    public val value: T,
    public val extra: Map<KClass<*>, Any> = emptyMap()
)

public interface OptimizationProblem<T : Any> {
    public fun optimize(): OptimizationResult<T>
}

