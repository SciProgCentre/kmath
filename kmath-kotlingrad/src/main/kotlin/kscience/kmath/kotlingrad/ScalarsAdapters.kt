package kscience.kmath.kotlingrad

import edu.umontreal.kotlingrad.experimental.*
import kscience.kmath.ast.MST
import kscience.kmath.ast.MstAlgebra
import kscience.kmath.ast.MstExpression
import kscience.kmath.ast.MstExtendedField
import kscience.kmath.ast.MstExtendedField.unaryMinus
import kscience.kmath.expressions.DifferentiableExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.Symbol
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
public fun <X : SFun<X>> MST.toSFun(proto: X): SFun<X> = when (this) {
    is MST.Numeric -> toSConst()
    is MST.Symbolic -> toSVar(proto)

    is MST.Unary -> when (operation) {
        SpaceOperations.PLUS_OPERATION -> value.toSFun(proto)
        SpaceOperations.MINUS_OPERATION -> -value.toSFun(proto)
        TrigonometricOperations.SIN_OPERATION -> sin(value.toSFun(proto))
        TrigonometricOperations.COS_OPERATION -> cos(value.toSFun(proto))
        TrigonometricOperations.TAN_OPERATION -> tan(value.toSFun(proto))
        PowerOperations.SQRT_OPERATION -> value.toSFun(proto).sqrt()
        ExponentialOperations.EXP_OPERATION -> E<X>() pow value.toSFun(proto)
        ExponentialOperations.LN_OPERATION -> value.toSFun(proto).ln()
        else -> error("Unary operation $operation not defined in $this")
    }

    is MST.Binary -> when (operation) {
        SpaceOperations.PLUS_OPERATION -> left.toSFun(proto) + right.toSFun(proto)
        SpaceOperations.MINUS_OPERATION -> left.toSFun(proto) - right.toSFun(proto)
        RingOperations.TIMES_OPERATION -> left.toSFun(proto) * right.toSFun(proto)
        FieldOperations.DIV_OPERATION -> left.toSFun(proto) / right.toSFun(proto)
        PowerOperations.POW_OPERATION -> left.toSFun(proto) pow (right as MST.Numeric).toSConst()
        else -> error("Binary operation $operation not defined in $this")
    }
}

public class KMathNumber<T, A>(public val algebra: A, value: T) :
    RealNumber<KMathNumber<T, A>, T>(value) where T : Number, A : NumericAlgebra<T> {
    public override fun wrap(number: Number): SConst<KMathNumber<T, A>> = SConst(algebra.number(number))
    override val proto: KMathNumber<T, A> by lazy { KMathNumber(algebra, algebra.number(Double.NaN)) }
}

public class KMathProtocol<T, A>(algebra: A) :
    Protocol<KMathNumber<T, A>>(KMathNumber(algebra, algebra.number(Double.NaN)))
        where T : Number, A : NumericAlgebra<T>

public class DifferentiableMstExpression<T, A>(public val algebra: A, public val mst: MST) :
    DifferentiableExpression<T> where A : NumericAlgebra<T>, T : Number {
    public val proto by lazy { KMathProtocol(algebra).prototype }
    public val expr by lazy { MstExpression(algebra, mst) }

    public override fun invoke(arguments: Map<Symbol, T>): T = expr(arguments)  

    public override fun derivativeOrNull(orders: Map<Symbol, Int>): Expression<T> {
        val sfun = mst.toSFun(proto)
        val orders2 = orders.mapKeys { (k, _) -> MstAlgebra.symbol(k.identity).toSVar(proto) }
        TODO()
    }
}
