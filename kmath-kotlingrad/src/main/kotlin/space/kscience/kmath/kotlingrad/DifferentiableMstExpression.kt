package space.kscience.kmath.kotlingrad

import edu.umontreal.kotlingrad.api.SFun
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.MstAlgebra
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.misc.Symbol
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
public class DifferentiableMstExpression<T : Number, A : NumericAlgebra<T>>(
    public val algebra: A,
    public val mst: MST,
) : DifferentiableExpression<T, DifferentiableMstExpression<T, A>> {

    public override fun invoke(arguments: Map<Symbol, T>): T = mst.interpret(algebra, arguments)

    public override fun derivativeOrNull(symbols: List<Symbol>): DifferentiableMstExpression<T, A> =
        DifferentiableMstExpression(
            algebra,
            symbols.map(Symbol::identity)
                .map(MstAlgebra::bindSymbol)
                .map { it.toSVar<KMathNumber<T, A>>() }
                .fold(mst.toSFun(), SFun<KMathNumber<T, A>>::d)
                .toMst(),
        )
}

/**
 * Wraps this [MST] into [DifferentiableMstExpression].
 */
public fun <T : Number, A : NumericAlgebra<T>> MST.toDiffExpression(algebra: A): DifferentiableMstExpression<T, A> =
    DifferentiableMstExpression(algebra, this)
