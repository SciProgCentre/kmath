package space.kscience.kmath.expressions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*

/**
 * [Algebra] over [MST] nodes.
 */
public object MstAlgebra : NumericAlgebra<MST> {
    public override fun number(value: Number): MST.Numeric = MST.Numeric(value)
    public override fun bindSymbolOrNull(value: String): MST.Symbolic = MST.Symbolic(value)
    override fun bindSymbol(value: String): MST.Symbolic = bindSymbolOrNull(value)

    public override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        { arg -> MST.Unary(operation, arg) }

    public override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        { left, right -> MST.Binary(operation, left, right) }
}

/**
 * [Ring] over [MST] nodes.
 */
public object MstGroup : Group<MST>, NumericAlgebra<MST>, ScaleOperations<MST> {
    public override val zero: MST.Numeric = number(0.0)

    public override fun number(value: Number): MST.Numeric = MstAlgebra.number(value)
    public override fun bindSymbolOrNull(value: String): MST.Symbolic = MstAlgebra.bindSymbolOrNull(value)
    public override fun add(a: MST, b: MST): MST.Binary = binaryOperationFunction(GroupOperations.PLUS_OPERATION)(a, b)
    public override operator fun MST.unaryPlus(): MST.Unary =
        unaryOperationFunction(GroupOperations.PLUS_OPERATION)(this)

    public override operator fun MST.unaryMinus(): MST.Unary =
        unaryOperationFunction(GroupOperations.MINUS_OPERATION)(this)

    public override operator fun MST.minus(b: MST): MST.Binary =
        binaryOperationFunction(GroupOperations.MINUS_OPERATION)(this, b)

    public override fun scale(a: MST, value: Double): MST.Binary =
        binaryOperationFunction(RingOperations.TIMES_OPERATION)(a, number(value))

    public override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstAlgebra.binaryOperationFunction(operation)

    public override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstAlgebra.unaryOperationFunction(operation)
}

/**
 * [Ring] over [MST] nodes.
 */
@Suppress("OVERRIDE_BY_INLINE")
@OptIn(UnstableKMathAPI::class)
public object MstRing : Ring<MST>, NumbersAddOperations<MST>, ScaleOperations<MST> {
    public override inline val zero: MST.Numeric get() = MstGroup.zero
    public override val one: MST.Numeric = number(1.0)

    public override fun number(value: Number): MST.Numeric = MstGroup.number(value)
    public override fun bindSymbolOrNull(value: String): MST.Symbolic = MstAlgebra.bindSymbolOrNull(value)
    public override fun add(a: MST, b: MST): MST.Binary = MstGroup.add(a, b)

    public override fun scale(a: MST, value: Double): MST.Binary =
        MstGroup.binaryOperationFunction(RingOperations.TIMES_OPERATION)(a, MstGroup.number(value))

    public override fun multiply(a: MST, b: MST): MST.Binary =
        binaryOperationFunction(RingOperations.TIMES_OPERATION)(a, b)

    public override operator fun MST.unaryPlus(): MST.Unary = MstGroup { +this@unaryPlus }
    public override operator fun MST.unaryMinus(): MST.Unary = MstGroup { -this@unaryMinus }
    public override operator fun MST.minus(b: MST): MST.Binary = MstGroup { this@minus - b }

    public override fun binaryOperationFunction(operation: String): (left: MST, right: MST) -> MST.Binary =
        MstGroup.binaryOperationFunction(operation)

    public override fun unaryOperationFunction(operation: String): (arg: MST) -> MST.Unary =
        MstAlgebra.unaryOperationFunction(operation)
}

/**
 * [Field] over [MST] nodes.
 */
@Suppress("OVERRIDE_BY_INLINE")
@OptIn(UnstableKMathAPI::class)
public object MstField : Field<MST>, NumbersAddOperations<MST>, ScaleOperations<MST> {
    public override inline val zero: MST.Numeric get() = MstRing.zero
    public override inline val one: MST.Numeric get() = MstRing.one

    public override fun bindSymbolOrNull(value: String): MST.Symbolic = MstAlgebra.bindSymbolOrNull(value)
    public override fun number(value: Number): MST.Numeric = MstRing.number(value)
    public override fun add(a: MST, b: MST): MST.Binary = MstRing.add(a, b)

    public override fun scale(a: MST, value: Double): MST.Binary =
        MstGroup.binaryOperationFunction(RingOperations.TIMES_OPERATION)(a, MstGroup.number(value))

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
@Suppress("OVERRIDE_BY_INLINE")
public object MstExtendedField : ExtendedField<MST>, NumericAlgebra<MST> {
    public override inline val zero: MST.Numeric get() = MstField.zero
    public override inline val one: MST.Numeric get() = MstField.one

    public override fun bindSymbolOrNull(value: String): MST.Symbolic = MstAlgebra.bindSymbolOrNull(value)
    public override fun number(value: Number): MST.Numeric = MstRing.number(value)
    public override fun sin(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.SIN_OPERATION)(arg)
    public override fun cos(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.COS_OPERATION)(arg)
    public override fun tan(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.TAN_OPERATION)(arg)
    public override fun asin(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ASIN_OPERATION)(arg)
    public override fun acos(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ACOS_OPERATION)(arg)
    public override fun atan(arg: MST): MST.Unary = unaryOperationFunction(TrigonometricOperations.ATAN_OPERATION)(arg)
    public override fun sinh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.SINH_OPERATION)(arg)
    public override fun cosh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.COSH_OPERATION)(arg)
    public override fun tanh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.TANH_OPERATION)(arg)
    public override fun asinh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.ASINH_OPERATION)(arg)
    public override fun acosh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.ACOSH_OPERATION)(arg)
    public override fun atanh(arg: MST): MST.Unary = unaryOperationFunction(ExponentialOperations.ATANH_OPERATION)(arg)
    public override fun add(a: MST, b: MST): MST.Binary = MstField.add(a, b)

    public override fun scale(a: MST, value: Double): MST =
        binaryOperation(GroupOperations.PLUS_OPERATION, a, number(value))

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
