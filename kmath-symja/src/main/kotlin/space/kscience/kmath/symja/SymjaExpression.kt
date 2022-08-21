/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.symja

import org.matheclipse.core.eval.ExprEvaluator
import org.matheclipse.core.expression.F
import space.kscience.kmath.expressions.SpecialDifferentiableExpression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.NumericAlgebra

/**
 * Represents [MST] based [space.kscience.kmath.expressions.DifferentiableExpression] relying on
 * [Symja](https://github.com/axkr/symja_android_library).
 *
 * The principle of this API is converting the [mst] to an [org.matheclipse.core.interfaces.IExpr], differentiating it
 * with Symja's [F.D], then converting [org.matheclipse.core.interfaces.IExpr] back to [MST].
 *
 * @param T The type of number.
 * @param A The [NumericAlgebra] of [T].
 * @property algebra The [A] instance.
 * @property mst The [MST] node.
 */
public class SymjaExpression<T : Number, A : NumericAlgebra<T>>(
    public val algebra: A,
    public val mst: MST,
    public val evaluator: ExprEvaluator = DEFAULT_EVALUATOR,
) : SpecialDifferentiableExpression<T, SymjaExpression<T, A>> {
    override fun invoke(arguments: Map<Symbol, T>): T = mst.interpret(algebra, arguments)

    override fun derivativeOrNull(symbols: List<Symbol>): SymjaExpression<T, A> = SymjaExpression(
        algebra,
        symbols.map(Symbol::toIExpr).fold(mst.toIExpr(), F::D).toMst(evaluator),
        evaluator,
    )
}

/**
 * Wraps this [MST] into [SymjaExpression] in the context of [algebra].
 */
public fun <T : Number, A : NumericAlgebra<T>> MST.toSymjaExpression(algebra: A): SymjaExpression<T, A> =
    SymjaExpression(algebra, this)
