package scientifik.kmath.expressions

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space

internal class VariableExpression<T>(val name: String, val default: T? = null) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T =
        arguments[name] ?: default ?: error("Parameter not found: $name")
}

internal class ConstantExpression<T>(val value: T) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = value
}

internal class SumExpression<T>(
    val context: Space<T>,
    val first: Expression<T>,
    val second: Expression<T>
) : Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.add(first.invoke(arguments), second.invoke(arguments))
}

internal class ProductExpression<T>(val context: Ring<T>, val first: Expression<T>, val second: Expression<T>) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T =
        context.multiply(first.invoke(arguments), second.invoke(arguments))
}

internal class ConstProductExpession<T>(val context: Space<T>, val expr: Expression<T>, val const: Number) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.multiply(expr.invoke(arguments), const)
}

internal class DivExpession<T>(val context: Field<T>, val expr: Expression<T>, val second: Expression<T>) :
    Expression<T> {
    override fun invoke(arguments: Map<String, T>): T = context.divide(expr.invoke(arguments), second.invoke(arguments))
}

open class FunctionalExpressionSpace<T>(
    val space: Space<T>,
    one: T
) : Space<Expression<T>>, ExpressionContext<T> {

    override val zero: Expression<T> = ConstantExpression(space.zero)

    val one: Expression<T> = ConstantExpression(one)

    override fun const(value: T): Expression<T> = ConstantExpression(value)

    override fun variable(name: String, default: T?): Expression<T> = VariableExpression(name, default)

    override fun add(a: Expression<T>, b: Expression<T>): Expression<T> = SumExpression(space, a, b)

    override fun multiply(a: Expression<T>, k: Number): Expression<T> = ConstProductExpession(space, a, k)


    operator fun Expression<T>.plus(arg: T) = this + const(arg)
    operator fun Expression<T>.minus(arg: T) = this - const(arg)

    operator fun T.plus(arg: Expression<T>) = arg + this
    operator fun T.minus(arg: Expression<T>) = arg - this

    fun const(value: Double): Expression<T> = one.times(value)

    open fun produceSingular(value: String): Expression<T> {
        val numberValue = value.toDoubleOrNull()
        return if (numberValue == null) {
            variable(value)
        } else {
            const(numberValue)
        }
    }

    open fun produceUnary(operation: String, value: Expression<T>): Expression<T> {
        return when (operation) {
            UnaryNode.PLUS_OPERATION -> value
            UnaryNode.MINUS_OPERATION -> -value
            else -> error("Unary operation $operation is not supported by $this")
        }
    }

    open fun produceBinary(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> {
        return when (operation) {
            BinaryNode.PLUS_OPERATION -> left + right
            BinaryNode.MINUS_OPERATION -> left - right
            else -> error("Binary operation $operation is not supported by $this")
        }
    }

    override fun produce(node: SyntaxTreeNode): Expression<T> {
        return when (node) {
            is SingularNode -> produceSingular(node.value)
            is UnaryNode -> produceUnary(node.operation, produce(node.value))
            is BinaryNode -> produceBinary(node.operation, produce(node.left), produce(node.right))
        }
    }
}

open class FunctionalExpressionField<T>(
    val field: Field<T>
) : Field<Expression<T>>, FunctionalExpressionSpace<T>(field, field.one) {
    override fun multiply(a: Expression<T>, b: Expression<T>): Expression<T> = ProductExpression(field, a, b)

    override fun divide(a: Expression<T>, b: Expression<T>): Expression<T> = DivExpession(field, a, b)

    operator fun Expression<T>.times(arg: T) = this * const(arg)
    operator fun Expression<T>.div(arg: T) = this / const(arg)

    operator fun T.times(arg: Expression<T>) = arg * this
    operator fun T.div(arg: Expression<T>) = arg / this

    override fun produce(node: SyntaxTreeNode): Expression<T> {
        //TODO bring together numeric and typed expressions
        return super.produce(node)
    }

    override fun produceBinary(operation: String, left: Expression<T>, right: Expression<T>): Expression<T> {
        return when (operation) {
            BinaryNode.TIMES_OPERATION -> left * right
            BinaryNode.DIV_OPERATION -> left / right
            else -> super.produceBinary(operation, left, right)
        }
    }
}