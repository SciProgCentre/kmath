package scientifik.kmath.ast

import scientifik.kmath.operations.*

object MstAlgebra : NumericAlgebra<MST> {
    override fun number(value: Number): MST = MST.Numeric(value)

    override fun symbol(value: String): MST = MST.Symbolic(value)

    override fun unaryOperation(operation: String, arg: MST): MST =
        MST.Unary(operation, arg)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MST.Binary(operation, left, right)
}

object MstSpace : Space<MST>, NumericAlgebra<MST> {
    override val zero: MST = number(0.0)

    override fun number(value: Number): MST = MstAlgebra.number(value)
    override fun symbol(value: String): MST = MstAlgebra.symbol(value)

    override fun add(a: MST, b: MST): MST =
        binaryOperation(SpaceOperations.PLUS_OPERATION, a, b)

    override fun multiply(a: MST, k: Number): MST =
        binaryOperation(RingOperations.TIMES_OPERATION, a, number(k))

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MstAlgebra.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST = MstAlgebra.unaryOperation(operation, arg)
}

object MstRing : Ring<MST>, NumericAlgebra<MST> {
    override val zero: MST = number(0.0)
    override val one: MST = number(1.0)

    override fun number(value: Number): MST = MstAlgebra.number(value)
    override fun symbol(value: String): MST = MstAlgebra.symbol(value)
    override fun add(a: MST, b: MST): MST = binaryOperation(SpaceOperations.PLUS_OPERATION, a, b)

    override fun multiply(a: MST, k: Number): MST =
        binaryOperation(RingOperations.TIMES_OPERATION, a, MstSpace.number(k))

    override fun multiply(a: MST, b: MST): MST = binaryOperation(RingOperations.TIMES_OPERATION, a, b)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MstAlgebra.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST = MstAlgebra.unaryOperation(operation, arg)
}

object MstField : Field<MST> {
    override val zero: MST = number(0.0)
    override val one: MST = number(1.0)

    override fun symbol(value: String): MST = MstAlgebra.symbol(value)
    override fun number(value: Number): MST = MstAlgebra.number(value)
    override fun add(a: MST, b: MST): MST = binaryOperation(SpaceOperations.PLUS_OPERATION, a, b)

    override fun multiply(a: MST, k: Number): MST =
        binaryOperation(RingOperations.TIMES_OPERATION, a, MstSpace.number(k))

    override fun multiply(a: MST, b: MST): MST = binaryOperation(RingOperations.TIMES_OPERATION, a, b)
    override fun divide(a: MST, b: MST): MST = binaryOperation(FieldOperations.DIV_OPERATION, a, b)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MstAlgebra.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST = MstAlgebra.unaryOperation(operation, arg)
}
