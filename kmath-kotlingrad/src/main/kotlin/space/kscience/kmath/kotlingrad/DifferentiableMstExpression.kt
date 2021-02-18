package space.kscience.kmath.kotlingrad

import edu.umontreal.kotlingrad.api.SFun
import space.kscience.kmath.ast.MST
import space.kscience.kmath.ast.MstAlgebra
import space.kscience.kmath.ast.MstExpression
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.NumericAlgebra

/**
 * Represents wrapper of [MstExpression] implementing [DifferentiableExpression].
 *
 * The principle of this API is converting the [mst] to an [SFun], differentiating it with Kotlin∇, then converting
 * [SFun] back to [MST].
 *
 * @param T the type of number.
 * @param A the [NumericAlgebra] of [T].
 * @property expr the underlying [MstExpression].
 */
public inline class DifferentiableMstExpression<T, A>(public val expr: MstExpression<T, A>) :
    DifferentiableExpression<T, MstExpression<T, A>> where A : NumericAlgebra<T>, T : Number {
    public constructor(algebra: A, mst: MST) : this(MstExpression(algebra, mst))

    /**
     * The [MstExpression.algebra] of [expr].
     */
    public val algebra: A
        get() = expr.algebra

    /**
     * The [MstExpression.mst] of [expr].
     */
    public val mst: MST
        get() = expr.mst

    public override fun invoke(arguments: Map<Symbol, T>): T = expr(arguments)

    public override fun derivativeOrNull(symbols: List<Symbol>): MstExpression<T, A> = MstExpression(
        algebra,
        symbols.map(Symbol::identity)
            .map(MstAlgebra::bindSymbol)
            .map { it.toSVar<KMathNumber<T, A>>() }
            .fold(mst.toSFun(), SFun<KMathNumber<T, A>>::d)
            .toMst(),
    )
}

/**
 * Wraps this [MstExpression] into [DifferentiableMstExpression].
 */
public fun <T : Number, A : NumericAlgebra<T>> MstExpression<T, A>.differentiable(): DifferentiableMstExpression<T, A> =
    DifferentiableMstExpression(this)
