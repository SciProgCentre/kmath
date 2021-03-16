package space.kscience.kmath.expressions

/**
 * Represents expression which structure can be differentiated.
 *
 * @param T the type this expression takes as argument and returns.
 * @param R the type of expression this expression can be differentiated to.
 */
public interface DifferentiableExpression<T, out R : Expression<T>> : Expression<T> {
    /**
     * Differentiates this expression by ordered collection of [symbols].
     *
     * @param symbols the symbols.
     * @return the derivative or `null`.
     */
    public fun derivativeOrNull(symbols: List<Symbol>): R?
}

public fun <T, R : Expression<T>> DifferentiableExpression<T, R>.derivative(symbols: List<Symbol>): R =
    derivativeOrNull(symbols) ?: error("Derivative by symbols $symbols not provided")

public fun <T, R : Expression<T>> DifferentiableExpression<T, R>.derivative(vararg symbols: Symbol): R =
    derivative(symbols.toList())

public fun <T, R : Expression<T>> DifferentiableExpression<T, R>.derivative(name: String): R =
    derivative(StringSymbol(name))

/**
 * A [DifferentiableExpression] that defines only first derivatives
 */
public abstract class FirstDerivativeExpression<T, R : Expression<T>> : DifferentiableExpression<T, R> {
    /**
     * Returns first derivative of this expression by given [symbol].
     */
    public abstract fun derivativeOrNull(symbol: Symbol): R?

    public final override fun derivativeOrNull(symbols: List<Symbol>): R? {
        val dSymbol = symbols.firstOrNull() ?: return null
        return derivativeOrNull(dSymbol)
    }
}

/**
 * A factory that converts an expression in autodiff variables to a [DifferentiableExpression]
 */
public fun interface AutoDiffProcessor<T : Any, I : Any, A : ExpressionAlgebra<T, I>, out R : Expression<T>> {
    public fun process(function: A.() -> I): DifferentiableExpression<T, R>
}
