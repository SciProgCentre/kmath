/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*

/**
 * [Algebra] over [MST] nodes.
 */
public object MstNumericAlgebra : NumericAlgebra<MST> {
    override fun number(value: Number): MST.Numeric = MST.Numeric(value)
    override fun bindSymbolOrNull(value: String): Symbol = StringSymbol(value)
    override fun bindSymbol(value: String): Symbol = bindSymbolOrNull(value)

    override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        { arg -> MST.Unary(operation, arg) }

    override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        { left, right -> MST.Binary(operation, left, right) }
}

/**
 * [Group] over [MST] nodes.
 */
public object MstGroup : Group<MST>, NumericAlgebra<MST>, ScaleOperations<MST> {
    override val zero: MST.Numeric = number(0.0)

    override fun number(value: Number): MST.Numeric = MstNumericAlgebra.number(value)
    override fun bindSymbolOrNull(value: String): Symbol = MstNumericAlgebra.bindSymbolOrNull(value)
    override fun add(left: MST, right: MST): MST.Binary = binaryOperationFunction(GroupOps.PLUS_OPERATION)(left, right)
    override operator fun MST.unaryPlus(): MST.Unary =
        unaryOperationFunction(GroupOps.PLUS_OPERATION)(this)

    override operator fun MST.unaryMinus(): MST.Unary =
        unaryOperationFunction(GroupOps.MINUS_OPERATION)(this)

    override operator fun MST.minus(arg: MST): MST.Binary =
        binaryOperationFunction(GroupOps.MINUS_OPERATION)(this, arg)

    override fun scale(a: MST, value: Double): MST.Binary =
        binaryOperationFunction(RingOps.TIMES_OPERATION)(a, number(value))

    override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstNumericAlgebra.binaryOperationFunction(operation)

    override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstNumericAlgebra.unaryOperationFunction(operation)
}

/**
 * [Ring] over [MST] nodes.
 */
@Suppress("OVERRIDE_BY_INLINE")
@OptIn(UnstableKMathAPI::class)
public object MstRing : Ring<MST>, NumbersAddOps<MST>, ScaleOperations<MST> {
    override inline val zero: MST.Numeric get() = MstGroup.zero
    override val one: MST.Numeric = number(1.0)

    override fun number(value: Number): MST.Numeric = MstGroup.number(value)
    override fun bindSymbolOrNull(value: String): Symbol = MstNumericAlgebra.bindSymbolOrNull(value)
    override fun add(left: MST, right: MST): MST.Binary = MstGroup.add(left, right)

    override fun scale(a: MST, value: Double): MST.Binary =
        MstGroup.binaryOperationFunction(RingOps.TIMES_OPERATION)(a, MstGroup.number(value))

    override fun multiply(left: MST, right: MST): MST.Binary =
        binaryOperationFunction(RingOps.TIMES_OPERATION)(left, right)

    override operator fun MST.unaryPlus(): MST.Unary = MstGroup { +this@unaryPlus }
    override operator fun MST.unaryMinus(): MST.Unary = MstGroup { -this@unaryMinus }
    override operator fun MST.minus(arg: MST): MST.Binary = MstGroup { this@minus - arg }

    override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstGroup.binaryOperationFunction(operation)

    override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstNumericAlgebra.unaryOperationFunction(operation)
}

/**
 * [Field] over [MST] nodes.
 */
@Suppress("OVERRIDE_BY_INLINE")
@OptIn(UnstableKMathAPI::class)
public object MstField : Field<MST>, NumbersAddOps<MST>, ScaleOperations<MST> {
    override inline val zero: MST.Numeric get() = MstRing.zero
    override inline val one: MST.Numeric get() = MstRing.one

    override fun bindSymbolOrNull(value: String): Symbol = MstNumericAlgebra.bindSymbolOrNull(value)
    override fun number(value: Number): MST.Numeric = MstRing.number(value)
    override fun add(left: MST, right: MST): MST.Binary = MstRing.add(left, right)

    override fun scale(a: MST, value: Double): MST.Binary =
        MstGroup.binaryOperationFunction(RingOps.TIMES_OPERATION)(a, MstGroup.number(value))

    override fun multiply(left: MST, right: MST): MST.Binary = MstRing.multiply(left, right)
    override fun divide(left: MST, right: MST): MST.Binary =
        binaryOperationFunction(FieldOps.DIV_OPERATION)(left, right)

    override operator fun MST.unaryPlus(): MST.Unary = MstRing { +this@unaryPlus }
    override operator fun MST.unaryMinus(): MST.Unary = MstRing { -this@unaryMinus }
    override operator fun MST.minus(arg: MST): MST.Binary = MstRing { this@minus - arg }

    override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstRing.binaryOperationFunction(operation)

    override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstRing.unaryOperationFunction(operation)
}

/**
 * [ExtendedField] over [MST] nodes.
 */
@Suppress("OVERRIDE_BY_INLINE")
public object MstExtendedField : ExtendedField<MST>, NumericAlgebra<MST> {
    override inline val zero: MST.Numeric get() = MstField.zero
    override inline val one: MST.Numeric get() = MstField.one

    override fun bindSymbolOrNull(value: String): Symbol = MstNumericAlgebra.bindSymbolOrNull(value)
    override fun number(value: Number): MST.Numeric = MstRing.number(value)
    override fun sin(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.SIN_OPERATION)(arg)
    override fun cos(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.COS_OPERATION)(arg)
    override fun tan(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.TAN_OPERATION)(arg)
    override fun asin(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ASIN_OPERATION)(arg)
    override fun acos(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ACOS_OPERATION)(arg)
    override fun atan(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ATAN_OPERATION)(arg)
    override fun sinh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.SINH_OPERATION)(arg)
    override fun cosh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.COSH_OPERATION)(arg)
    override fun tanh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.TANH_OPERATION)(arg)
    override fun asinh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.ASINH_OPERATION)(arg)
    override fun acosh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.ACOSH_OPERATION)(arg)
    override fun atanh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.ATANH_OPERATION)(arg)
    override fun add(left: MST, right: MST): MST.Binary = MstField.add(left, right)
    override fun sqrt(arg: MST): MST = unaryOperationFunction(PowerOperations.SQRT_OPERATION)(arg)

    override fun scale(a: MST, value: Double): MST =
        binaryOperation(GroupOps.PLUS_OPERATION, a, number(value))

    override fun multiply(left: MST, right: MST): MST.Binary = MstField.multiply(left, right)
    override fun divide(left: MST, right: MST): MST.Binary = MstField.divide(left, right)
    override operator fun MST.unaryPlus(): MST.Unary = MstField { +this@unaryPlus }
    override operator fun MST.unaryMinus(): MST.Unary = MstField { -this@unaryMinus }
    override operator fun MST.minus(arg: MST): MST.Binary = MstField { this@minus - arg }

    override fun power(arg: MST, pow: Number): MST.Binary =
        binaryOperationFunction(PowerOperations.POW_OPERATION)(arg, number(pow))

    override fun exp(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.EXP_OPERATION)(arg)
    override fun ln(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.LN_OPERATION)(arg)

    override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstField.binaryOperationFunction(operation)

    override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstField.unaryOperationFunction(operation)
}

/**
 * Logic algebra for [MST]
 */
@UnstableKMathAPI
public object MstLogicAlgebra : LogicAlgebra<MST> {
    override fun bindSymbolOrNull(value: String): MST = super.bindSymbolOrNull(value) ?: StringSymbol(value)

    override fun const(boolean: Boolean): Symbol = if (boolean) {
        LogicAlgebra.TRUE
    } else {
        LogicAlgebra.FALSE
    }

    override fun MST.not(): MST = MST.Unary(Boolean::not.name, this)

    override fun MST.and(other: MST): MST = MST.Binary(Boolean::and.name, this, other)

    override fun MST.or(other: MST): MST  = MST.Binary(Boolean::or.name, this, other)

    override fun MST.xor(other: MST): MST  = MST.Binary(Boolean::xor.name, this, other)
}
