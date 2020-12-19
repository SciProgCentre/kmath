package kscience.kmath.ast

import kscience.kmath.operations.*

/**
 * [Algebra] over [MST] nodes.
 */
public object MstAlgebra : NumericAlgebra<MST> {
    public override fun number(value: Number): MST.Numeric = MST.Numeric(value)
    public override fun symbol(value: String): MST.Symbolic = MST.Symbolic(value)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST.Unary =
        { arg -> MST.Unary(operation, arg) }

    public override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST.Binary =
        { left, right -> MST.Binary(operation, left, right) }
}

/**
 * [Space] over [MST] nodes.
 */
public object MstSpace : Space<MST>, NumericAlgebra<MST> {
    public override val zero: MST.Numeric by lazy { number(0.0) }

    public override fun number(value: Number): MST.Numeric = MstAlgebra.number(value)
    public override fun symbol(value: String): MST.Symbolic = MstAlgebra.symbol(value)
    public override fun add(a: MST, b: MST): MST.Binary = binaryOperation(SpaceOperations.PLUS_OPERATION)(a, b)
    public override fun MST.unaryMinus(): MST = unaryOperation(SpaceOperations.MINUS_OPERATION)(this)

    public override fun multiply(a: MST, k: Number): MST.Binary =
        binaryOperation(RingOperations.TIMES_OPERATION)(a, number(k))

    public override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstAlgebra.binaryOperation(operation)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST.Unary =
        MstAlgebra.unaryOperation(operation)
}

/**
 * [Ring] over [MST] nodes.
 */
public object MstRing : Ring<MST>, NumericAlgebra<MST> {
    override val zero: MST.Numeric
        get() = MstSpace.zero

    override val one: MST.Numeric by lazy { number(1.0) }

    public override fun number(value: Number): MST.Numeric = MstSpace.number(value)
    public override fun symbol(value: String): MST.Symbolic = MstSpace.symbol(value)
    public override fun add(a: MST, b: MST): MST.Binary = MstSpace.add(a, b)
    public override fun multiply(a: MST, k: Number): MST.Binary = MstSpace.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST.Binary = binaryOperation(RingOperations.TIMES_OPERATION)(a, b)
    public override fun MST.unaryMinus(): MST = MstSpace.unaryOperation(SpaceOperations.MINUS_OPERATION)(this)

    public override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstSpace.binaryOperation(operation)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST.Unary =
        MstAlgebra.unaryOperation(operation)
}

/**
 * [Field] over [MST] nodes.
 */
public object MstField : Field<MST> {
    public override val zero: MST.Numeric
        get() = MstRing.zero

    public override val one: MST.Numeric
        get() = MstRing.one

    public override fun symbol(value: String): MST.Symbolic = MstRing.symbol(value)
    public override fun number(value: Number): MST.Numeric = MstRing.number(value)
    public override fun add(a: MST, b: MST): MST.Binary = MstRing.add(a, b)
    public override fun multiply(a: MST, k: Number): MST.Binary = MstRing.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST.Binary = MstRing.multiply(a, b)
    public override fun divide(a: MST, b: MST): MST.Binary = binaryOperation(FieldOperations.DIV_OPERATION)(a, b)
    public override fun MST.unaryMinus(): MST = MstSpace.unaryOperation(SpaceOperations.MINUS_OPERATION)(this)

    public override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstRing.binaryOperation(operation)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST.Unary = MstRing.unaryOperation(operation)
}

/**
 * [ExtendedField] over [MST] nodes.
 */
public object MstExtendedField : ExtendedField<MST> {
    public override val zero: MST.Numeric
        get() = MstField.zero

    public override val one: MST.Numeric
        get() = MstField.one

    public override fun symbol(value: String): MST = MstField.symbol(value)
    public override fun sin(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.SIN_OPERATION)(arg)
    public override fun cos(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.COS_OPERATION)(arg)
    public override fun tan(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.TAN_OPERATION)(arg)
    public override fun asin(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.ASIN_OPERATION)(arg)
    public override fun acos(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.ACOS_OPERATION)(arg)
    public override fun atan(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.ATAN_OPERATION)(arg)
    public override fun sinh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.SINH_OPERATION)(arg)
    public override fun cosh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.COSH_OPERATION)(arg)
    public override fun tanh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.TANH_OPERATION)(arg)
    public override fun asinh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.ASINH_OPERATION)(arg)
    public override fun acosh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.ACOSH_OPERATION)(arg)
    public override fun atanh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.ATANH_OPERATION)(arg)
    public override fun add(a: MST, b: MST): MST.Binary = MstField.add(a, b)
    public override fun multiply(a: MST, k: Number): MST.Binary = MstField.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST.Binary = MstField.multiply(a, b)
    public override fun divide(a: MST, b: MST): MST.Binary = MstField.divide(a, b)
    public override fun MST.unaryMinus(): MST = MstSpace.unaryOperation(SpaceOperations.MINUS_OPERATION)(this)

    public override fun power(arg: MST, pow: Number): MST.Binary =
        binaryOperation(PowerOperations.POW_OPERATION)(arg, number(pow))

    public override fun exp(arg: MST): MST.Unary = unaryOperation(ExponentialOperations.EXP_OPERATION)(arg)
    public override fun ln(arg: MST): MST.Unary = unaryOperation(ExponentialOperations.LN_OPERATION)(arg)

    public override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstField.binaryOperation(operation)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST.Unary = MstField.unaryOperation(operation)
}
