/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.kotlingrad

import ai.hypergraph.kotlingrad.api.*
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.MstExtendedField
import space.kscience.kmath.expressions.MstExtendedField.unaryMinus
import space.kscience.kmath.expressions.MstNumericAlgebra
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.*

/**
 * Maps [SVar] to [Symbol] directly.
 *
 * @receiver The variable.
 * @returnAa node.
 */
public fun <X : SFun<X>> SVar<X>.toMst(): Symbol = MstNumericAlgebra.bindSymbol(name)

/**
 * Maps [SVar] to [MST.Numeric] directly.
 *
 * @receiver The constant.
 * @return A node.
 */
public fun <X : SFun<X>> SConst<X>.toMst(): MST.Numeric = MstNumericAlgebra.number(doubleValue)

/**
 * Maps [SFun] objects to [MST]. Some unsupported operations like [Derivative] are bound and converted then.
 * [Power] operation is limited to constant right-hand side arguments.
 *
 * Detailed mapping is:
 *
 * - [SVar] -> [MstExtendedField.bindSymbol];
 * - [SConst] -> [MstExtendedField.number];
 * - [Sum] -> [MstExtendedField.add];
 * - [Prod] -> [MstExtendedField.multiply];
 * - [Power] -> [MstExtendedField.power] (limited to constant exponents only);
 * - [Negative] -> [MstExtendedField.unaryMinus];
 * - [Log] -> [MstExtendedField.ln] (left) / [MstExtendedField.ln] (right);
 * - [Sine] -> [MstExtendedField.sin];
 * - [Cosine] -> [MstExtendedField.cos];
 * - [Tangent] -> [MstExtendedField.tan];
 * - [DProd] is vector operation, and it is requested to be evaluated;
 * - [SComposition] is also requested to be evaluated eagerly;
 * - [VSumAll] is requested to be evaluated;
 * - [Derivative] is requested to be evaluated.
 *
 * @receiver The scalar function.
 * @return A node.
 */
public fun <X : SFun<X>> SFun<X>.toMst(): MST = MstExtendedField {
    when (this@toMst) {
        is SVar -> toMst()
        is SConst -> toMst()
        is Sum -> left.toMst() + right.toMst()
        is Prod -> left.toMst() * right.toMst()
        is Power -> left.toMst() pow ((right as? SConst<*>)?.doubleValue ?: (right() as SConst<*>).doubleValue)
        is Negative -> -input.toMst()
        is Log -> ln(left.toMst()) / ln(right.toMst())
        is Sine -> sin(input.toMst())
        is Cosine -> cos(input.toMst())
        is Tangent -> tan(input.toMst())
        is DProd -> this@toMst().toMst()
        is SComposition -> this@toMst().toMst()
        is VSumAll<X, *> -> this@toMst().toMst()
        is Derivative -> this@toMst().toMst()
    }
}

/**
 * Maps [MST.Numeric] to [SConst] directly.
 *
 * @receiver The node.
 * @return A new constant.
 */
public fun <X : SFun<X>> MST.Numeric.toSConst(): SConst<X> = SConst(value)

/**
 * Maps [Symbol] to [SVar] directly.
 *
 * @receiver The node.
 * @return A new variable.
 */
internal fun <X : SFun<X>> Symbol.toSVar(): SVar<X> = SVar(identity)

/**
 * Maps [MST] objects to [SFun]. Unsupported operations throw [IllegalStateException].
 *
 * Detailed mapping is:
 *
 * - [MST.Numeric] -> [SConst];
 * - [Symbol] -> [SVar];
 * - [MST.Unary] -> [Negative], [Sine], [Cosine], [Tangent], [Power], [Log];
 * - [MST.Binary] -> [Sum], [Prod], [Power].
 *
 * @receiver The node.
 * @return A scalar function.
 */
public fun <X : SFun<X>> MST.toSFun(): SFun<X> = when (this) {
    is MST.Numeric -> toSConst()
    is Symbol -> toSVar()

    is MST.Unary -> when (operation) {
        GroupOps.PLUS_OPERATION -> +value.toSFun<X>()
        GroupOps.MINUS_OPERATION -> -value.toSFun<X>()
        TrigonometricOperations.SIN_OPERATION -> sin(value.toSFun())
        TrigonometricOperations.COS_OPERATION -> cos(value.toSFun())
        TrigonometricOperations.TAN_OPERATION -> tan(value.toSFun())
        PowerOperations.SQRT_OPERATION -> sqrt(value.toSFun())
        ExponentialOperations.EXP_OPERATION -> exp(value.toSFun())
        ExponentialOperations.LN_OPERATION -> value.toSFun<X>().ln()
        ExponentialOperations.SINH_OPERATION -> MstExtendedField { (exp(value) - exp(-value)) / 2.0 }.toSFun()
        ExponentialOperations.COSH_OPERATION -> MstExtendedField { (exp(value) + exp(-value)) / 2.0 }.toSFun()
        ExponentialOperations.TANH_OPERATION -> MstExtendedField { (exp(value) - exp(-value)) / (exp(-value) + exp(value)) }.toSFun()
        ExponentialOperations.ASINH_OPERATION -> MstExtendedField { ln(sqrt(value * value + one) + value) }.toSFun()
        ExponentialOperations.ACOSH_OPERATION -> MstExtendedField { ln(value + sqrt((value - one) * (value + one))) }.toSFun()
        ExponentialOperations.ATANH_OPERATION -> MstExtendedField { (ln(value + one) - ln(one - value)) / 2.0 }.toSFun()
        else -> error("Unary operation $operation not defined in $this")
    }

    is MST.Binary -> when (operation) {
        GroupOps.PLUS_OPERATION -> left.toSFun<X>() + right.toSFun()
        GroupOps.MINUS_OPERATION -> left.toSFun<X>() - right.toSFun()
        RingOps.TIMES_OPERATION -> left.toSFun<X>() * right.toSFun()
        FieldOps.DIV_OPERATION -> left.toSFun<X>() / right.toSFun()
        PowerOperations.POW_OPERATION -> left.toSFun<X>() pow (right as MST.Numeric).toSConst()
        else -> error("Binary operation $operation not defined in $this")
    }
}
