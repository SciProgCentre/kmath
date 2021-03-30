package space.kscience.kmath.kotlingrad

import edu.umontreal.kotlingrad.api.*
import space.kscience.kmath.ast.MST
import space.kscience.kmath.ast.MstAlgebra
import space.kscience.kmath.ast.MstExtendedField
import space.kscience.kmath.ast.MstExtendedField.unaryMinus
import space.kscience.kmath.operations.*

/**
 * Maps [SVar] to [MST.Symbolic] directly.
 *
 * @receiver the variable.
 * @return a node.
 */
public fun <X : SFun<X>> SVar<X>.toMst(): MST.Symbolic = MstAlgebra.bindSymbol(name)

/**
 * Maps [SVar] to [MST.Numeric] directly.
 *
 * @receiver the constant.
 * @return a node.
 */
public fun <X : SFun<X>> SConst<X>.toMst(): MST.Numeric = MstAlgebra.number(doubleValue)

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
 * @receiver the scalar function.
 * @return a node.
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
internal fun <X : SFun<X>> MST.Symbolic.toSVar(): SVar<X> = SVar(value)

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
public fun <X : SFun<X>> MST.toSFun(): SFun<X> = when (this) {
    is MST.Numeric -> toSConst()
    is MST.Symbolic -> toSVar()

    is MST.Unary -> when (operation) {
        GroupOperations.PLUS_OPERATION -> +value.toSFun<X>()
        GroupOperations.MINUS_OPERATION -> -value.toSFun<X>()
        TrigonometricOperations.SIN_OPERATION -> sin(value.toSFun())
        TrigonometricOperations.COS_OPERATION -> cos(value.toSFun())
        TrigonometricOperations.TAN_OPERATION -> tan(value.toSFun())
        PowerOperations.SQRT_OPERATION -> sqrt(value.toSFun())
        ExponentialOperations.EXP_OPERATION -> exp(value.toSFun())
        ExponentialOperations.LN_OPERATION -> value.toSFun<X>().ln()
        else -> error("Unary operation $operation not defined in $this")
    }

    is MST.Binary -> when (operation) {
        GroupOperations.PLUS_OPERATION -> left.toSFun<X>() + right.toSFun()
        GroupOperations.MINUS_OPERATION -> left.toSFun<X>() - right.toSFun()
        RingOperations.TIMES_OPERATION -> left.toSFun<X>() * right.toSFun()
        FieldOperations.DIV_OPERATION -> left.toSFun<X>() / right.toSFun()
        PowerOperations.POW_OPERATION -> left.toSFun<X>() pow (right as MST.Numeric).toSConst()
        else -> error("Binary operation $operation not defined in $this")
    }
}
