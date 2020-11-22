package kscience.kmath.ast

import kscience.kmath.operations.*

/**
 * [Algebra] over [MST] nodes.
 */
public object MstAlgebra : NumericAlgebra<MST> {
    override fun number(value: Number): MST.Numeric = MST.Numeric(value)

    override fun symbol(value: String): MST.Symbolic = MST.Symbolic(value)

    override fun unaryOperation(operation: String, arg: MST): MST.Unary =
        MST.Unary(operation, arg)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST.Binary =
        MST.Binary(operation, left, right)
}

/**
 * [Space] over [MST] nodes.
 */
public object MstSpace : Space<MST>, NumericAlgebra<MST> {
    override val zero: MST.Numeric by lazy { number(0.0) }

    override fun number(value: Number): MST.Numeric = MstAlgebra.number(value)
    override fun symbol(value: String): MST.Symbolic = MstAlgebra.symbol(value)
    override fun add(a: MST, b: MST): MST.Binary = binaryOperation(SpaceOperations.PLUS_OPERATION, a, b)
    override fun multiply(a: MST, k: Number): MST.Binary = binaryOperation(RingOperations.TIMES_OPERATION, a, number(k))

    override fun binaryOperation(operation: String, left: MST, right: MST): MST.Binary =
        MstAlgebra.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST.Unary = MstAlgebra.unaryOperation(operation, arg)
}

/**
 * [Ring] over [MST] nodes.
 */
public object MstRing : Ring<MST>, NumericAlgebra<MST> {
    override val zero: MST.Numeric
        get() = MstSpace.zero

    override val one: MST.Numeric by lazy { number(1.0) }

    override fun number(value: Number): MST.Numeric = MstSpace.number(value)
    override fun symbol(value: String): MST.Symbolic = MstSpace.symbol(value)
    override fun add(a: MST, b: MST): MST.Binary = MstSpace.add(a, b)
    override fun multiply(a: MST, k: Number): MST.Binary = MstSpace.multiply(a, k)
    override fun multiply(a: MST, b: MST): MST.Binary = binaryOperation(RingOperations.TIMES_OPERATION, a, b)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST.Binary =
        MstSpace.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST.Unary = MstSpace.unaryOperation(operation, arg)
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
    public override fun divide(a: MST, b: MST): MST.Binary = binaryOperation(FieldOperations.DIV_OPERATION, a, b)

    public override fun binaryOperation(operation: String, left: MST, right: MST): MST.Binary =
        MstRing.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST.Unary = MstRing.unaryOperation(operation, arg)
}

/**
 * [ExtendedField] over [MST] nodes.
 */
public object MstExtendedField : ExtendedField<MST> {
    override val zero: MST.Numeric
        get() = MstField.zero

    override val one: MST.Numeric
        get() = MstField.one

    override fun symbol(value: String): MST.Symbolic = MstField.symbol(value)
    override fun number(value: Number): MST.Numeric = MstField.number(value)
    override fun sin(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.SIN_OPERATION, arg)
    override fun cos(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.COS_OPERATION, arg)
    override fun tan(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.TAN_OPERATION, arg)
    override fun asin(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.ASIN_OPERATION, arg)
    override fun acos(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.ACOS_OPERATION, arg)
    override fun atan(arg: MST): MST.Unary = unaryOperation(TrigonometricOperations.ATAN_OPERATION, arg)
    override fun sinh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.SINH_OPERATION, arg)
    override fun cosh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.COSH_OPERATION, arg)
    override fun tanh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.TANH_OPERATION, arg)
    override fun asinh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.ASINH_OPERATION, arg)
    override fun acosh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.ACOSH_OPERATION, arg)
    override fun atanh(arg: MST): MST.Unary = unaryOperation(HyperbolicOperations.ATANH_OPERATION, arg)
    override fun add(a: MST, b: MST): MST.Binary = MstField.add(a, b)
    override fun multiply(a: MST, k: Number): MST.Binary = MstField.multiply(a, k)
    override fun multiply(a: MST, b: MST): MST.Binary = MstField.multiply(a, b)
    override fun divide(a: MST, b: MST): MST.Binary = MstField.divide(a, b)

    override fun power(arg: MST, pow: Number): MST.Binary =
        binaryOperation(PowerOperations.POW_OPERATION, arg, number(pow))

    override fun exp(arg: MST): MST.Unary = unaryOperation(ExponentialOperations.EXP_OPERATION, arg)
    override fun ln(arg: MST): MST.Unary = unaryOperation(ExponentialOperations.LN_OPERATION, arg)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST.Binary =
        MstField.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST.Unary = MstField.unaryOperation(operation, arg)
}
