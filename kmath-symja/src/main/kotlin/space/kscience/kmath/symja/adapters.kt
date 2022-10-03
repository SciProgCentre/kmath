/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.symja

import org.matheclipse.core.eval.ExprEvaluator
import org.matheclipse.core.expression.ComplexNum
import org.matheclipse.core.expression.F
import org.matheclipse.core.interfaces.IExpr
import org.matheclipse.core.interfaces.ISymbol
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.MstExtendedField
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.*

internal val DEFAULT_EVALUATOR = ExprEvaluator(false, 100)

/**
 * Matches the given [IExpr] instance to appropriate [MST] node or evaluates it with [evaluator].
 */
public fun IExpr.toMst(evaluator: ExprEvaluator = DEFAULT_EVALUATOR): MST = MstExtendedField {
    when {
        isPlus -> first().toMst(evaluator) + second().toMst(evaluator)
        isSin -> sin(first().toMst(evaluator))
        isSinh -> sinh(first().toMst(evaluator))
        isCos -> cos(first().toMst(evaluator))
        isCosh -> cosh(first().toMst(evaluator))
        isTan -> tan(first().toMst(evaluator))
        isTanh -> tanh(first().toMst(evaluator))
        isArcSin -> asin(first().toMst(evaluator))
        isArcCos -> acos(first().toMst(evaluator))
        isArcTan -> atan(first().toMst(evaluator))
        isArcTanh -> atanh(first().toMst(evaluator))
        isE -> bindSymbol("e")
        isPi -> bindSymbol("pi")
        isTimes -> first().toMst(evaluator) * second().toMst(evaluator)
        isOne -> one
        isZero -> zero
        isImaginaryUnit -> bindSymbol("i")
        isMinusOne -> -one
        this@toMst is ISymbol -> bindSymbol(symbolName)
        isPower -> power(first().toMst(evaluator), evaluator.evalf(second()))
        isExp -> exp(first().toMst(evaluator))
        isNumber -> number(evaluator.evalf(this@toMst))
        this@toMst === F.NIL -> error("NIL cannot be converted to MST")
        else -> evaluator.eval(this@toMst.toString()).toMst(evaluator)
    }
}

/**
 * Matches the given [MST] instance to appropriate [IExpr] node, only standard operations and symbols (which are
 * present in, say, [MstExtendedField]) are supported.
 */
public fun MST.toIExpr(): IExpr = when (this) {
    is MST.Numeric -> F.symjify(value)

    is Symbol -> when (identity) {
        "e" -> F.E
        "pi" -> F.Pi
        "i" -> ComplexNum.I
        else -> F.Dummy(identity)
    }

    is MST.Unary -> when (operation) {
        GroupOps.PLUS_OPERATION -> value.toIExpr()
        GroupOps.MINUS_OPERATION -> F.Negate(value.toIExpr())
        TrigonometricOperations.SIN_OPERATION -> F.Sin(value.toIExpr())
        TrigonometricOperations.COS_OPERATION -> F.Cos(value.toIExpr())
        TrigonometricOperations.TAN_OPERATION -> F.Tan(value.toIExpr())
        TrigonometricOperations.ASIN_OPERATION -> F.ArcSin(value.toIExpr())
        TrigonometricOperations.ACOS_OPERATION -> F.ArcCos(value.toIExpr())
        TrigonometricOperations.ATAN_OPERATION -> F.ArcTan(value.toIExpr())
        ExponentialOperations.SINH_OPERATION -> F.Sinh(value.toIExpr())
        ExponentialOperations.COSH_OPERATION -> F.Cosh(value.toIExpr())
        ExponentialOperations.TANH_OPERATION -> F.Tanh(value.toIExpr())
        ExponentialOperations.ASINH_OPERATION -> F.ArcSinh(value.toIExpr())
        ExponentialOperations.ACOSH_OPERATION -> F.ArcCosh(value.toIExpr())
        ExponentialOperations.ATANH_OPERATION -> F.ArcTanh(value.toIExpr())
        PowerOperations.SQRT_OPERATION -> F.Sqrt(value.toIExpr())
        ExponentialOperations.EXP_OPERATION -> F.Exp(value.toIExpr())
        ExponentialOperations.LN_OPERATION -> F.Log(value.toIExpr())
        else -> error("Unary operation $operation not defined in $this")
    }

    is MST.Binary -> when (operation) {
        GroupOps.PLUS_OPERATION -> left.toIExpr() + right.toIExpr()
        GroupOps.MINUS_OPERATION -> left.toIExpr() - right.toIExpr()
        RingOps.TIMES_OPERATION -> left.toIExpr() * right.toIExpr()
        FieldOps.DIV_OPERATION -> F.Divide(left.toIExpr(), right.toIExpr())
        PowerOperations.POW_OPERATION -> F.Power(left.toIExpr(), F.symjify((right as MST.Numeric).value))
        else -> error("Binary operation $operation not defined in $this")
    }
}
