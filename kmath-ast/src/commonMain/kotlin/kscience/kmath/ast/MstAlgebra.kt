package kscience.kmath.ast

import kscience.kmath.operations.*

/**
 * [Algebra] over [MST] nodes.
 */
public object MstAlgebra : NumericAlgebra<MST> {
    public override fun number(value: Number): MST.Numeric = MST.Numeric(value)

    public override fun symbol(value: String): MST.Symbolic = MST.Symbolic(value)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST.Unary = { arg -> MST.Unary(operation, arg) }

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
    override fun add(a: MST, b: MST): MST.Binary = binaryOperation(SpaceOperations.PLUS_OPERATION)(a, b)
    override fun multiply(a: MST, k: Number): MST = binaryOperation(RingOperations.TIMES_OPERATION)(a, number(k))

    override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST =
        MstAlgebra.binaryOperation(operation)

    override fun unaryOperation(operation: String): (arg: MST) -> MST = MstAlgebra.unaryOperation(operation)
}

/**
 * [Ring] over [MST] nodes.
 */
public object MstRing : Ring<MST>, NumericAlgebra<MST> {
    override val zero: MST
        get() = MstSpace.zero
    override val one: MST = number(1.0)

    public override fun number(value: Number): MST = MstSpace.number(value)
    public override fun symbol(value: String): MST = MstSpace.symbol(value)
    public override fun add(a: MST, b: MST): MST = MstSpace.add(a, b)
    public override fun multiply(a: MST, k: Number): MST = MstSpace.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST = binaryOperation(RingOperations.TIMES_OPERATION)(a, b)

    public override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST =
        MstSpace.binaryOperation(operation)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST = MstAlgebra.unaryOperation(operation)
}

/**
 * [Field] over [MST] nodes.
 */
public object MstField : Field<MST> {
    public override val zero: MST
        get() = MstRing.zero

    public override val one: MST
        get() = MstRing.one

    public override fun symbol(value: String): MST = MstRing.symbol(value)
    public override fun number(value: Number): MST = MstRing.number(value)
    public override fun add(a: MST, b: MST): MST = MstRing.add(a, b)
    public override fun multiply(a: MST, k: Number): MST = MstRing.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST = MstRing.multiply(a, b)
    public override fun divide(a: MST, b: MST): MST = binaryOperation(FieldOperations.DIV_OPERATION)(a, b)

    public override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST =
        MstRing.binaryOperation(operation)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST = MstRing.unaryOperation(operation)
}

/**
 * [ExtendedField] over [MST] nodes.
 */
public object MstExtendedField : ExtendedField<MST> {
    public override val zero: MST
        get() = MstField.zero

    public override val one: MST
        get() = MstField.one

    public override fun symbol(value: String): MST = MstField.symbol(value)
    public override fun sin(arg: MST): MST = unaryOperation(TrigonometricOperations.SIN_OPERATION)(arg)
    public override fun cos(arg: MST): MST = unaryOperation(TrigonometricOperations.COS_OPERATION)(arg)
    public override fun tan(arg: MST): MST = unaryOperation(TrigonometricOperations.TAN_OPERATION)(arg)
    public override fun asin(arg: MST): MST = unaryOperation(TrigonometricOperations.ASIN_OPERATION)(arg)
    public override fun acos(arg: MST): MST = unaryOperation(TrigonometricOperations.ACOS_OPERATION)(arg)
    public override fun atan(arg: MST): MST = unaryOperation(TrigonometricOperations.ATAN_OPERATION)(arg)
    public override fun sinh(arg: MST): MST = unaryOperation(HyperbolicOperations.SINH_OPERATION)(arg)
    public override fun cosh(arg: MST): MST = unaryOperation(HyperbolicOperations.COSH_OPERATION)(arg)
    public override fun tanh(arg: MST): MST = unaryOperation(HyperbolicOperations.TANH_OPERATION)(arg)
    public override fun asinh(arg: MST): MST = unaryOperation(HyperbolicOperations.ASINH_OPERATION)(arg)
    public override fun acosh(arg: MST): MST = unaryOperation(HyperbolicOperations.ACOSH_OPERATION)(arg)
    public override fun atanh(arg: MST): MST = unaryOperation(HyperbolicOperations.ATANH_OPERATION)(arg)
    public override fun add(a: MST, b: MST): MST = MstField.add(a, b)
    public override fun multiply(a: MST, k: Number): MST = MstField.multiply(a, k)
    public override fun multiply(a: MST, b: MST): MST = MstField.multiply(a, b)
    public override fun divide(a: MST, b: MST): MST = MstField.divide(a, b)
    public override fun power(arg: MST, pow: Number): MST = binaryOperation(PowerOperations.POW_OPERATION)(arg, number(pow))
    public override fun exp(arg: MST): MST = unaryOperation(ExponentialOperations.EXP_OPERATION)(arg)
    public override fun ln(arg: MST): MST = unaryOperation(ExponentialOperations.LN_OPERATION)(arg)

    public override fun binaryOperation(operation: String): (left: MST, right: MST) -> MST =
        MstField.binaryOperation(operation)

    public override fun unaryOperation(operation: String): (arg: MST) -> MST = MstField.unaryOperation(operation)
    override fun unaryOperation(operation: String, arg: MST): MST.Unary =
        MST.Unary(operation, arg)
}
