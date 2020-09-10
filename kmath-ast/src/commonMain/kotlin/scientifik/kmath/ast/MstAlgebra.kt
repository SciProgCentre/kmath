package scientifik.kmath.ast

import scientifik.kmath.operations.*

/**
 * [Algebra] over [MST] nodes.
 */
object MstAlgebra : NumericAlgebra<MST> {
    override fun number(value: Number): MST = MST.Numeric(value)

    override fun symbol(value: String): MST = MST.Symbolic(value)

    override fun unaryOperation(operation: String, arg: MST): MST =
        MST.Unary(operation, arg)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MST.Binary(operation, left, right)
}

/**
 * [Space] over [MST] nodes.
 */
object MstSpace : Space<MST>, NumericAlgebra<MST> {
    override val zero: MST = number(0.0)

    override fun number(value: Number): MST = MstAlgebra.number(value)
    override fun symbol(value: String): MST = MstAlgebra.symbol(value)
    override fun add(a: MST, b: MST): MST = binaryOperation(SpaceOperations.PLUS_OPERATION, a, b)
    override fun multiply(a: MST, k: Number): MST = binaryOperation(RingOperations.TIMES_OPERATION, a, number(k))

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MstAlgebra.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST = MstAlgebra.unaryOperation(operation, arg)
}

/**
 * [Ring] over [MST] nodes.
 */
object MstRing : Ring<MST>, NumericAlgebra<MST> {
    override val zero: MST = number(0.0)
    override val one: MST = number(1.0)

    override fun number(value: Number): MST = MstSpace.number(value)
    override fun symbol(value: String): MST = MstSpace.symbol(value)
    override fun add(a: MST, b: MST): MST = MstSpace.add(a, b)

    override fun multiply(a: MST, k: Number): MST = MstSpace.multiply(a, k)

    override fun multiply(a: MST, b: MST): MST = binaryOperation(RingOperations.TIMES_OPERATION, a, b)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MstSpace.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST = MstAlgebra.unaryOperation(operation, arg)
}

/**
 * [Field] over [MST] nodes.
 */
object MstField : Field<MST> {
    override val zero: MST = number(0.0)
    override val one: MST = number(1.0)

    override fun symbol(value: String): MST = MstRing.symbol(value)
    override fun number(value: Number): MST = MstRing.number(value)
    override fun add(a: MST, b: MST): MST = MstRing.add(a, b)
    override fun multiply(a: MST, k: Number): MST = MstRing.multiply(a, k)
    override fun multiply(a: MST, b: MST): MST = MstRing.multiply(a, b)
    override fun divide(a: MST, b: MST): MST = binaryOperation(FieldOperations.DIV_OPERATION, a, b)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MstRing.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST = MstRing.unaryOperation(operation, arg)
}

/**
 * [ExtendedField] over [MST] nodes.
 */
object MstExtendedField : ExtendedField<MST> {
    override val zero: MST = number(0.0)
    override val one: MST = number(1.0)

    override fun sin(arg: MST): MST = unaryOperation(TrigonometricOperations.SIN_OPERATION, arg)
    override fun cos(arg: MST): MST = unaryOperation(TrigonometricOperations.COS_OPERATION, arg)
    override fun asin(arg: MST): MST = unaryOperation(TrigonometricOperations.ASIN_OPERATION, arg)
    override fun acos(arg: MST): MST = unaryOperation(TrigonometricOperations.ACOS_OPERATION, arg)
    override fun atan(arg: MST): MST = unaryOperation(TrigonometricOperations.ATAN_OPERATION, arg)
    override fun add(a: MST, b: MST): MST = MstField.add(a, b)
    override fun multiply(a: MST, k: Number): MST = MstField.multiply(a, k)
    override fun multiply(a: MST, b: MST): MST = MstField.multiply(a, b)
    override fun divide(a: MST, b: MST): MST = MstField.divide(a, b)
    override fun power(arg: MST, pow: Number): MST = binaryOperation(PowerOperations.POW_OPERATION, arg, number(pow))
    override fun exp(arg: MST): MST = unaryOperation(ExponentialOperations.EXP_OPERATION, arg)
    override fun ln(arg: MST): MST = unaryOperation(ExponentialOperations.LN_OPERATION, arg)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MstField.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST = MstField.unaryOperation(operation, arg)
}
