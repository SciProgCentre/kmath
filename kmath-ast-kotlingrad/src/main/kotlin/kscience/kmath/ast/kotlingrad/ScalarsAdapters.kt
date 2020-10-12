package kscience.kmath.ast.kotlingrad

import edu.umontreal.kotlingrad.experimental.*
import kscience.kmath.ast.MST
import kscience.kmath.ast.MstExtendedField
import kscience.kmath.ast.MstExtendedField.unaryMinus
import kscience.kmath.operations.*

/**
 * Maps [SFun] objects to [MST]. Some unsupported operations like [Derivative] are bound and converted then.
 * [Power] operation is limited to constant right-hand side arguments.
 *
 * Detailed mapping is:
 *
 * - [SVar] -> [MstExtendedField.symbol];
 * - [SConst] -> [MstExtendedField.number];
 * - [Sum] -> [MstExtendedField.add];
 * - [Prod] -> [MstExtendedField.multiply];
 * - [Power] -> [MstExtendedField.power] (limited);
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
 * @receiver the scalar function.
 * @return a node.
 */
public fun <X : SFun<X>> SFun<X>.mst(): MST = MstExtendedField {
    when (this@mst) {
        is SVar -> symbol(name)
        is SConst -> number(doubleValue)
        is Sum -> left.mst() + right.mst()
        is Prod -> left.mst() * right.mst()
        is Power -> power(left.mst(), (right as SConst<*>).doubleValue)
        is Negative -> -input.mst()
        is Log -> ln(left.mst()) / ln(right.mst())
        is Sine -> sin(input.mst())
        is Cosine -> cos(input.mst())
        is Tangent -> tan(input.mst())
        is DProd -> this@mst().mst()
        is SComposition -> this@mst().mst()
        is VSumAll<X, *> -> this@mst().mst()
        is Derivative -> this@mst().mst()
    }
}

/**
 * Maps [MST.Numeric] to [SConst] directly.
 *
 * @receiver the node.
 * @return a new constant.
 */
public fun <X : SFun<X>> MST.Numeric.sConst(): SConst<X> = SConst(value)

/**
 * Maps [MST.Symbolic] to [SVar] directly.
 *
 * @receiver the node.
 * @param proto the prototype instance.
 * @return a new variable.
 */
public fun <X : SFun<X>> MST.Symbolic.sVar(proto: X): SVar<X> = SVar(proto, value)

/**
 * Maps [MST] objects to [SFun]. Unsupported operations throw [IllegalStateException].
 *
 * Detailed mapping is:
 *
 * - [MST.Numeric] -> [SConst];
 * - [MST.Symbolic] -> [SVar];
 * - [MST.Unary] -> [Negative], [Sine], [Cosine], [Tangent], [Power], [Log];
 * - [MST.Binary] -> [Sum], [Prod], [Power].
 *
 * @receiver the node.
 * @param proto the prototype instance.
 * @return a scalar function.
 */
public fun <X : SFun<X>> MST.sFun(proto: X): SFun<X> = when (this) {
    is MST.Numeric -> sConst()
    is MST.Symbolic -> sVar(proto)

    is MST.Unary -> when (operation) {
        SpaceOperations.PLUS_OPERATION -> value.sFun(proto)
        SpaceOperations.MINUS_OPERATION -> Negative(value.sFun(proto))
        TrigonometricOperations.SIN_OPERATION -> Sine(value.sFun(proto))
        TrigonometricOperations.COS_OPERATION -> Cosine(value.sFun(proto))
        TrigonometricOperations.TAN_OPERATION -> Tangent(value.sFun(proto))
        PowerOperations.SQRT_OPERATION -> Power(value.sFun(proto), SConst(0.5))
        ExponentialOperations.EXP_OPERATION -> Power(value.sFun(proto), E())
        ExponentialOperations.LN_OPERATION -> Log(value.sFun(proto))
        else -> error("Unary operation $operation not defined in $this")
    }

    is MST.Binary -> when (operation) {
        SpaceOperations.PLUS_OPERATION -> Sum(left.sFun(proto), right.sFun(proto))
        SpaceOperations.MINUS_OPERATION -> Sum(left.sFun(proto), Negative(right.sFun(proto)))
        RingOperations.TIMES_OPERATION -> Prod(left.sFun(proto), right.sFun(proto))
        FieldOperations.DIV_OPERATION -> Prod(left.sFun(proto), Power(right.sFun(proto), Negative(One())))
        PowerOperations.POW_OPERATION -> Power(left.sFun(proto), SConst((right as MST.Numeric).value))
        else -> error("Binary operation $operation not defined in $this")
    }
}
