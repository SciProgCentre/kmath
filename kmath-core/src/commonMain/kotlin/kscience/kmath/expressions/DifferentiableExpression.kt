package kscience.kmath.expressions

/**
 * An expression that provides derivatives
 */
public interface DifferentiableExpression<T> : Expression<T>{
    public fun derivativeOrNull(orders: Map<Symbol, Int>): Expression<T>?
}

public fun <T> DifferentiableExpression<T>.derivative(orders: Map<Symbol, Int>): Expression<T> =
    derivativeOrNull(orders) ?: error("Derivative with orders $orders not provided")

public fun <T> DifferentiableExpression<T>.derivative(vararg orders: Pair<Symbol, Int>): Expression<T> =
    derivative(mapOf(*orders))

public fun <T> DifferentiableExpression<T>.derivative(symbol: Symbol): Expression<T> = derivative(symbol to 1)

public fun <T> DifferentiableExpression<T>.derivative(name: String): Expression<T> =
    derivative(StringSymbol(name) to 1)

/**
 * A [DifferentiableExpression] that defines only first derivatives
 */
public abstract class FirstDerivativeExpression<T> : DifferentiableExpression<T> {

    public abstract fun derivativeOrNull(symbol: Symbol): Expression<T>?

    public override fun derivativeOrNull(orders: Map<Symbol, Int>): Expression<T>? {
        val dSymbol = orders.entries.singleOrNull { it.value == 1 }?.key ?: return null
        return derivativeOrNull(dSymbol)
    }
}

/**
 * A factory that converts an expression in autodiff variables to a [DifferentiableExpression]
 */
public interface AutoDiffProcessor<T : Any, I : Any, A : ExpressionAlgebra<T, I>> {
    public fun process(function: A.() -> I): DifferentiableExpression<T>
}