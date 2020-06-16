
package scientifik.kmath.ast

import scientifik.kmath.operations.*

object MSTAlgebra : NumericAlgebra<MST> {
    override fun symbol(value: String): MST = MST.Symbolic(value)

    override fun unaryOperation(operation: String, arg: MST): MST =
        MST.Unary(operation, arg)

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MST.Binary(operation, left, right)

    override fun number(value: Number): MST = MST.Numeric(value)
}

object MSTSpace : Space<MST>, NumericAlgebra<MST> by MSTAlgebra {
    override val zero: MST = number(0.0)

    override fun add(a: MST, b: MST): MST =
        binaryOperation(SpaceOperations.PLUS_OPERATION, a, b)

    override fun multiply(a: MST, k: Number): MST =
        binaryOperation(RingOperations.TIMES_OPERATION, a, number(k))

    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MSTAlgebra.binaryOperation(operation, left, right)

    override fun unaryOperation(operation: String, arg: MST): MST = MSTAlgebra.unaryOperation(operation, arg)
}

object MSTRing : Ring<MST>, Space<MST> by MSTSpace {
    override val one: MST = number(1.0)

    override fun multiply(a: MST, b: MST): MST = binaryOperation(RingOperations.TIMES_OPERATION, a, b)
    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MSTSpace.binaryOperation(operation, left, right)
}

object MSTField : Field<MST>, Ring<MST> by MSTRing {
    override fun divide(a: MST, b: MST): MST = binaryOperation(FieldOperations.DIV_OPERATION, a, b)
    override fun binaryOperation(operation: String, left: MST, right: MST): MST =
        MSTRing.binaryOperation(operation, left, right)
}