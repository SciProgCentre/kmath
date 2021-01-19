package kscience.kmath.ast

import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.*

/**
 * [Algebra] over [MST] nodes.
 */
public object MstAlgebra : NumericAlgebra<MST> {
    public override fun number(value: Number): MST.Numeric = MST.Numeric(value)
    public override fun symbol(value: String): MST.Symbolic = MST.Symbolic(value)

    public override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        { arg -> MST.Unary(operation, arg) }

    public override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        { left, right -> MST.Binary(operation, left, right) }
}

/**
 * [Space] over [MST] nodes.
 */
public object MstSpace : Space<MST>, NumericAlgebra<MST> {
    public override val zero: MST.Numeric by lazy { number(0.0) }

    public override fun number(value: Number): MST.Numeric = MstAlgebra.number(value)
    public override fun symbol(value: String): MST.Symbolic = MstAlgebra.symbol(value)
    public override fun add(a: MST, b: MST): MST.Binary = binaryOperationFunction(SpaceOperations.PLUS_OPERATION)(a, b)
    public override operator fun MST.unaryPlus(): MST.Unary =
        unaryOperationFunction(SpaceOperations.PLUS_OPERATION)(this)

    public override operator fun MST.unaryMinus(): MST.Unary =
        unaryOperationFunction(SpaceOperations.MINUS_OPERATION)(this)

    public override operator fun MST.minus(b: MST): MST.Binary =
        binaryOperationFunction(SpaceOperations.MINUS_OPERATION)(this, b)

    public override fun multiply(a: MST, k: Number): MST.Binary =
        binaryOperationFunction(RingOperations.TIMES_OPERATION)(a, number(k))

    public override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstAlgebra.binaryOperationFunction(operation)

    public override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstAlgebra.unaryOperationFunction(operation)
}

/**
 * [Ring] over [MST] nodes.
 */
@OptIn(UnstableKMathAPI::class)
public object MstRing : Ring<MST>, RingWithNumbers<MST> {
    public override val zero: MST.Numeric
        get() = MstSpace.zero

    public override val one: MST.Numeric by lazy { number(1.0) }

    public override fun number(value: Number): MST.Numeric = MstSpace.number(value)
    public override fun symbol(value: String): MST.Symbolic = MstSpace.symbol(value)
    public override fun add(a: MST, b: MST): MST.Binary = MstSpace.add(a, b)
    public override fun multiply(a: MST, k: Number): MST.Binary = MstSpace.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST.Binary =
        binaryOperationFunction(RingOperations.TIMES_OPERATION)(a, b)

    public override operator fun MST.unaryPlus(): MST.Unary = MstSpace { +this@unaryPlus }
    public override operator fun MST.unaryMinus(): MST.Unary = MstSpace { -this@unaryMinus }
    public override operator fun MST.minus(b: MST): MST.Binary = MstSpace { this@minus - b }

    public override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstSpace.binaryOperationFunction(operation)

    public override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstAlgebra.unaryOperationFunction(operation)
}

/**
 * [Field] over [MST] nodes.
 */
@OptIn(UnstableKMathAPI::class)
public object MstField : Field<MST>, RingWithNumbers<MST> {
    public override val zero: MST.Numeric
        get() = MstRing.zero

    public override val one: MST.Numeric
        get() = MstRing.one

    public override fun symbol(value: String): MST.Symbolic = MstRing.symbol(value)
    public override fun number(value: Number): MST.Numeric = MstRing.number(value)
    public override fun add(a: MST, b: MST): MST.Binary = MstRing.add(a, b)
    public override fun multiply(a: MST, k: Number): MST.Binary = MstRing.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST.Binary = MstRing.multiply(a, b)
    public override fun divide(a: MST, b: MST): MST.Binary =
        binaryOperationFunction(FieldOperations.DIV_OPERATION)(a, b)

    public override operator fun MST.unaryPlus(): MST.Unary = MstRing { +this@unaryPlus }
    public override operator fun MST.unaryMinus(): MST.Unary = MstRing { -this@unaryMinus }
    public override operator fun MST.minus(b: MST): MST.Binary = MstRing { this@minus - b }

    public override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstRing.binaryOperationFunction(operation)

    public override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstRing.unaryOperationFunction(operation)
}

/**
 * [ExtendedField] over [MST] nodes.
 */
public object MstExtendedField : ExtendedField<MST>, NumericAlgebra<MST> {
    public override val zero: MST.Numeric
        get() = MstField.zero

    public override val one: MST.Numeric
        get() = MstField.one

    public override fun symbol(value: String): MST.Symbolic = MstField.symbol(value)
    public override fun number(value: Number): MST.Numeric = MstRing.number(value)
    public override fun sin(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.SIN_OPERATION)(arg)
    public override fun cos(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.COS_OPERATION)(arg)
    public override fun tan(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.TAN_OPERATION)(arg)
    public override fun asin(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ASIN_OPERATION)(arg)
    public override fun acos(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ACOS_OPERATION)(arg)
    public override fun atan(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ATAN_OPERATION)(arg)
    public override fun sinh(arg: MST): MST.Unary = unaryOperationFunction(HyperbolicOperations.SINH_OPERATION)(arg)
    public override fun cosh(arg: MST): MST.Unary = unaryOperationFunction(HyperbolicOperations.COSH_OPERATION)(arg)
    public override fun tanh(arg: MST): MST.Unary = unaryOperationFunction(HyperbolicOperations.TANH_OPERATION)(arg)
    public override fun asinh(arg: MST): MST.Unary = unaryOperationFunction(HyperbolicOperations.ASINH_OPERATION)(arg)
    public override fun acosh(arg: MST): MST.Unary = unaryOperationFunction(HyperbolicOperations.ACOSH_OPERATION)(arg)
    public override fun atanh(arg: MST): MST.Unary = unaryOperationFunction(HyperbolicOperations.ATANH_OPERATION)(arg)
    public override fun add(a: MST, b: MST): MST.Binary = MstField.add(a, b)
    public override fun multiply(a: MST, k: Number): MST.Binary = MstField.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST.Binary = MstField.multiply(a, b)
    public override fun divide(a: MST, b: MST): MST.Binary = MstField.divide(a, b)
    public override operator fun MST.unaryPlus(): MST.Unary = MstField { +this@unaryPlus }
    public override operator fun MST.unaryMinus(): MST.Unary = MstField { -this@unaryMinus }
    public override operator fun MST.minus(b: MST): MST.Binary = MstField { this@minus - b }

    public override fun power(arg: MST, pow: Number): MST.Binary =
        binaryOperationFunction(PowerOperations.POW_OPERATION)(arg, number(pow))

    public override fun exp(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.EXP_OPERATION)(arg)
    public override fun ln(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.LN_OPERATION)(arg)

    public override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstField.binaryOperationFunction(operation)

    public override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstField.unaryOperationFunction(operation)
}
