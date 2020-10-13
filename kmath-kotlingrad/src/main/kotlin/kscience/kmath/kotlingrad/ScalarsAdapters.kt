package kscience.kmath.kotlingrad

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
 * @receiver the scalar function.
 * @return a node.
 */
public fun <X : SFun<X>> SFun<X>.toMst(): MST = MstExtendedField {
    when (this@toMst) {
        is SVar -> symbol(name)
        is SConst -> number(doubleValue)
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
 * @receiver the node.
 * @return a new constant.
 */
public fun <X : SFun<X>> MST.Numeric.toSConst(): SConst<X> = SConst(value)

/**
 * Maps [MST.Symbolic] to [SVar] directly.
 *
 * @receiver the node.
 * @param proto the prototype instance.
 * @return a new variable.
 */
public fun <X : SFun<X>> MST.Symbolic.toSVar(proto: X): SVar<X> = SVar(proto, value)

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
public fun <X : SFun<X>> MST.tSFun(proto: X): SFun<X> = when (this) {
    is MST.Numeric -> toSConst()
    is MST.Symbolic -> toSVar(proto)

    is MST.Unary -> when (operation) {
        SpaceOperations.PLUS_OPERATION -> value.tSFun(proto)
        SpaceOperations.MINUS_OPERATION -> -value.tSFun(proto)
        TrigonometricOperations.SIN_OPERATION -> sin(value.tSFun(proto))
        TrigonometricOperations.COS_OPERATION -> cos(value.tSFun(proto))
        TrigonometricOperations.TAN_OPERATION -> tan(value.tSFun(proto))
        PowerOperations.SQRT_OPERATION -> value.tSFun(proto).sqrt()
        ExponentialOperations.EXP_OPERATION -> E<X>() pow value.tSFun(proto)
        ExponentialOperations.LN_OPERATION -> value.tSFun(proto).ln()
        else -> error("Unary operation $operation not defined in $this")
    }

    is MST.Binary -> when (operation) {
        SpaceOperations.PLUS_OPERATION -> left.tSFun(proto) + right.tSFun(proto)
        SpaceOperations.MINUS_OPERATION -> left.tSFun(proto) - right.tSFun(proto)
        RingOperations.TIMES_OPERATION -> left.tSFun(proto) * right.tSFun(proto)
        FieldOperations.DIV_OPERATION -> left.tSFun(proto) / right.tSFun(proto)
        PowerOperations.POW_OPERATION -> left.tSFun(proto) pow (right as MST.Numeric).toSConst()
        else -> error("Binary operation $operation not defined in $this")
    }
}
