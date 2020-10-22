package kscience.kmath.expressions

/**
 * And object that could be differentiated
 */
public interface Differentiable<T> {
    public fun derivative(orders: Map<Symbol, Int>): T
}

public interface DifferentiableExpression<T> : Differentiable<Expression<T>>, Expression<T>

public fun <T> DifferentiableExpression<T>.derivative(vararg orders: Pair<Symbol, Int>): Expression<T> =
    derivative(mapOf(*orders))

public fun <T> DifferentiableExpression<T>.derivative(symbol: Symbol): Expression<T> = derivative(symbol to 1)

public fun <T> DifferentiableExpression<T>.derivative(name: String): Expression<T> = derivative(StringSymbol(name) to 1)

//public interface DifferentiableExpressionBuilder<T, E, A : ExpressionAlgebra<T, E>>: ExpressionBuilder<T,E,A> {
//    public override fun expression(block: A.() -> E): DifferentiableExpression<T>
//}
