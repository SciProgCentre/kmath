package kscience.kmath.expressions

/**
 * An expression that provides derivatives
 */
public interface DifferentiableExpression<T> : Expression<T> {
    public fun derivativeOrNull(symbols: List<Symbol>): Expression<T>?
}

public fun <T> DifferentiableExpression<T>.derivative(symbols: List<Symbol>): Expression<T> =
    derivativeOrNull(symbols) ?: error("Derivative by symbols $symbols not provided")

public fun <T> DifferentiableExpression<T>.derivative(vararg symbols: Symbol): Expression<T> =
    derivative(symbols.toList())

public fun <T> DifferentiableExpression<T>.derivative(name: String): Expression<T> =
    derivative(StringSymbol(name))

/**
 * A [DifferentiableExpression] that defines only first derivatives
 */
public abstract class FirstDerivativeExpression<T> : DifferentiableExpression<T> {

    public abstract fun derivativeOrNull(symbol: Symbol): Expression<T>?

    public override fun derivativeOrNull(symbols: List<Symbol>): Expression<T>? {
        val dSymbol = symbols.firstOrNull() ?: return null
        return derivativeOrNull(dSymbol)
    }
}

/**
 * A factory that converts an expression in autodiff variables to a [DifferentiableExpression]
 */
public interface AutoDiffProcessor<T : Any, I : Any, A : ExpressionAlgebra<T, I>> {
    public fun process(function: A.() -> I): DifferentiableExpression<T>
}