package kscience.kmath.ast.kotlingrad

import edu.umontreal.kotlingrad.experimental.*
import kscience.kmath.ast.MST
import kscience.kmath.ast.MstExtendedField
import kscience.kmath.operations.*

/**
 * Maps [SFun] objects to [MST]. Some unsupported operations like [Derivative] are bound and converted then.
 *
 * @receiver a scalar function.
 * @return the [MST].
 */
public fun <X : SFun<X>> SFun<X>.mst(): MST = MstExtendedField {
    when (this@mst) {
        is SVar -> symbol(name)
        is SConst -> number(doubleValue)
        is Sum -> left.mst() + right.mst()
        is Prod -> left.mst() * right.mst()
        is Power -> power(left.mst(), (right() as SConst<*>).doubleValue)
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
 * Maps [MST] objects to [SFun]. Unsupported operations throw [IllegalStateException].
 *
 * @receiver an [MST].
 * @return the scalar function.
 */
public fun <X : SFun<X>> MST.sfun(proto: X): SFun<X> {
    return when (this) {
        is MST.Numeric -> SConst(value)
        is MST.Symbolic -> SVar(proto, value)

        is MST.Unary -> when (operation) {
            SpaceOperations.PLUS_OPERATION -> value.sfun(proto)
            SpaceOperations.MINUS_OPERATION -> Negative(value.sfun(proto))
            TrigonometricOperations.SIN_OPERATION -> Sine(value.sfun(proto))
            TrigonometricOperations.COS_OPERATION -> Cosine(value.sfun(proto))
            TrigonometricOperations.TAN_OPERATION -> Tangent(value.sfun(proto))
            PowerOperations.SQRT_OPERATION -> Power(value.sfun(proto), SConst(0.5))
            ExponentialOperations.EXP_OPERATION -> Power(value.sfun(proto), E())
            ExponentialOperations.LN_OPERATION -> Log(value.sfun(proto))
            else -> error("Unary operation $operation not defined in $this")
        }

        is MST.Binary -> when (operation) {
            SpaceOperations.PLUS_OPERATION -> Sum(left.sfun(proto), right.sfun(proto))
            SpaceOperations.MINUS_OPERATION -> Sum(left.sfun(proto), Negative(right.sfun(proto)))
            RingOperations.TIMES_OPERATION -> Prod(left.sfun(proto), right.sfun(proto))
            FieldOperations.DIV_OPERATION -> Prod(left.sfun(proto), Power(right.sfun(proto), Negative(One())))
            PowerOperations.POW_OPERATION -> Power(left.sfun(proto), SConst((right as MST.Numeric).value))
            else -> error("Binary operation $operation not defined in $this")
        }
    }
}
