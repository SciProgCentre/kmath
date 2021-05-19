/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.kotlingrad

import edu.umontreal.kotlingrad.api.SFun
import edu.umontreal.kotlingrad.api.SVar
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.NumericAlgebra

/**
 * Represents [MST] based [DifferentiableExpression].
 *
 * The principle of this API is converting the [mst] to an [SFun], differentiating it with
 * [Kotlin∇](https://github.com/breandan/kotlingrad), then converting [SFun] back to [MST].
 *
 * @param T The type of number.
 * @param A The [NumericAlgebra] of [T].
 * @property algebra The [A] instance.
 * @property mst The [MST] node.
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
                .map(MstNumericAlgebra::bindSymbol)
                .map<Symbol, SVar<KMathNumber<T, A>>>(Symbol::toSVar)
                .fold(mst.toSFun(), SFun<KMathNumber<T, A>>::d)
                .toMst(),
        )
}

/**
 * Wraps this [MST] into [DifferentiableMstExpression].
 */
public fun <T : Number, A : NumericAlgebra<T>> MST.toDiffExpression(algebra: A): DifferentiableMstExpression<T, A> =
    DifferentiableMstExpression(algebra, this)
