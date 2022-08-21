/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.kotlingrad

import ai.hypergraph.kotlingrad.api.SFun
import ai.hypergraph.kotlingrad.api.SVar
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.NumericAlgebra

/**
 * Represents [MST] based [DifferentiableExpression] relying on [Kotlin∇](https://github.com/breandan/kotlingrad).
 *
 * The principle of this API is converting the [mst] to an [SFun], differentiating it with Kotlin∇, then converting
 * [SFun] back to [MST].
 *
 * @param T The type of number.
 * @param A The [NumericAlgebra] of [T].
 * @property algebra The [A] instance.
 * @property mst The [MST] node.
 */
public class KotlingradExpression<T : Number, A : NumericAlgebra<T>>(
    public val algebra: A,
    public val mst: MST,
) : SpecialDifferentiableExpression<T, KotlingradExpression<T, A>> {
    override fun invoke(arguments: Map<Symbol, T>): T = mst.interpret(algebra, arguments)

    override fun derivativeOrNull(
        symbols: List<Symbol>,
    ): KotlingradExpression<T, A> = KotlingradExpression(
        algebra,
        symbols.map(Symbol::identity)
            .map(MstNumericAlgebra::bindSymbol)
            .map<Symbol, SVar<KMathNumber<T, A>>>(Symbol::toSVar)
            .fold(mst.toSFun(), SFun<KMathNumber<T, A>>::d)
            .toMst(),
    )
}

/**
 * A diff processor using [MST] to Kotlingrad converter
 */
public class KotlingradProcessor<T : Number, A : NumericAlgebra<T>>(
    public val algebra: A,
) : AutoDiffProcessor<T, MST, MstExtendedField> {
    override fun differentiate(function: MstExtendedField.() -> MST): DifferentiableExpression<T> =
        MstExtendedField.function().toKotlingradExpression(algebra)
}

/**
 * Wraps this [MST] into [KotlingradExpression] in the context of [algebra].
 */
public fun <T : Number, A : NumericAlgebra<T>> MST.toKotlingradExpression(algebra: A): KotlingradExpression<T, A> =
    KotlingradExpression(algebra, this)
