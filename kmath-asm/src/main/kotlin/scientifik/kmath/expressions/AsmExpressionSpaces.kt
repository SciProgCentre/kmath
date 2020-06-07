package scientifik.kmath.expressions

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space

open class AsmExpressionSpace<T>(space: Space<T>) : Space<AsmExpression<T>>,
    ExpressionSpace<T, AsmExpression<T>> {
    override val zero: AsmExpression<T> = AsmConstantExpression(space.zero)
    override fun const(value: T): AsmExpression<T> = AsmConstantExpression(value)
    override fun variable(name: String, default: T?): AsmExpression<T> = AsmVariableExpression(name, default)
    override fun add(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> = AsmSumExpression(a, b)
    override fun multiply(a: AsmExpression<T>, k: Number): AsmExpression<T> = AsmConstProductExpression(a, k)
    operator fun AsmExpression<T>.plus(arg: T): AsmExpression<T> = this + const(arg)
    operator fun AsmExpression<T>.minus(arg: T): AsmExpression<T> = this - const(arg)
    operator fun T.plus(arg: AsmExpression<T>): AsmExpression<T> = arg + this
    operator fun T.minus(arg: AsmExpression<T>): AsmExpression<T> = arg - this
}

class AsmExpressionField<T>(private val field: Field<T>) : ExpressionField<T, AsmExpression<T>>,
    AsmExpressionSpace<T>(field) {
    override val one: AsmExpression<T>
        get() = const(this.field.one)

    override fun number(value: Number): AsmExpression<T> = const(field.run { one * value })
    override fun multiply(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> = AsmProductExpression(a, b)
    override fun divide(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> = AsmDivExpression(a, b)
    operator fun AsmExpression<T>.times(arg: T): AsmExpression<T> = this * const(arg)
    operator fun AsmExpression<T>.div(arg: T): AsmExpression<T> = this / const(arg)
    operator fun T.times(arg: AsmExpression<T>): AsmExpression<T> = arg * this
    operator fun T.div(arg: AsmExpression<T>): AsmExpression<T> = arg / this
}
