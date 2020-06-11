package scientifik.kmath.expressions.asm

import scientifik.kmath.expressions.ExpressionContext
import scientifik.kmath.operations.*

open class AsmExpressionAlgebra<T>(val algebra: Algebra<T>) :
    Algebra<AsmExpression<T>>,
    ExpressionContext<T, AsmExpression<T>> {
    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    override fun const(value: T): AsmExpression<T> = AsmConstantExpression(value)
    override fun variable(name: String, default: T?): AsmExpression<T> = AsmVariableExpression(name, default)
}

open class AsmExpressionSpace<T>(
    val space: Space<T>
) : AsmExpressionAlgebra<T>(space), Space<AsmExpression<T>> {
    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    override val zero: AsmExpression<T> = AsmConstantExpression(space.zero)

    override fun add(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(space, SpaceOperations.PLUS_OPERATION, a, b)

    override fun multiply(a: AsmExpression<T>, k: Number): AsmExpression<T> = AsmConstProductExpression(space, a, k)
    operator fun AsmExpression<T>.plus(arg: T): AsmExpression<T> = this + const(arg)
    operator fun AsmExpression<T>.minus(arg: T): AsmExpression<T> = this - const(arg)
    operator fun T.plus(arg: AsmExpression<T>): AsmExpression<T> = arg + this
    operator fun T.minus(arg: AsmExpression<T>): AsmExpression<T> = arg - this
}

open class AsmExpressionRing<T>(private val ring: Ring<T>) : AsmExpressionSpace<T>(ring), Ring<AsmExpression<T>> {
    override val one: AsmExpression<T>
        get() = const(this.ring.one)

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    override fun number(value: Number): AsmExpression<T> = const(ring { one * value })

    override fun multiply(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(space, RingOperations.TIMES_OPERATION, a, b)

    operator fun AsmExpression<T>.times(arg: T): AsmExpression<T> = this * const(arg)
    operator fun T.times(arg: AsmExpression<T>): AsmExpression<T> = arg * this
}

open class AsmExpressionField<T>(private val field: Field<T>) :
    AsmExpressionRing<T>(field),
    Field<AsmExpression<T>> {

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    override fun divide(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(field, FieldOperations.DIV_OPERATION, a, b)

    operator fun AsmExpression<T>.div(arg: T): AsmExpression<T> = this / const(arg)
    operator fun T.div(arg: AsmExpression<T>): AsmExpression<T> = arg / this
}
