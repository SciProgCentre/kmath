package scientifik.kmath.asm

import scientifik.kmath.asm.internal.AsmBuilder
import scientifik.kmath.asm.internal.hasSpecific
import scientifik.kmath.asm.internal.optimize
import scientifik.kmath.asm.internal.tryInvokeSpecific
import scientifik.kmath.expressions.Expression
import scientifik.kmath.expressions.ExpressionAlgebra
import scientifik.kmath.operations.*
import kotlin.reflect.KClass

/**
 * A function declaration that could be compiled to [AsmBuilder].
 *
 * @param T the type the stored function returns.
 */
sealed class AsmExpression<T : Any>: Expression<T> {
    abstract val type: KClass<out T>

    abstract val algebra: Algebra<T>

    /**
     * Tries to evaluate this function without its variables. This method is intended for optimization.
     *
     * @return `null` if the function depends on its variables, the value if the function is a constant.
     */
    internal open fun tryEvaluate(): T? = null

    /**
     * Compiles this declaration.
     *
     * @param gen the target [AsmBuilder].
     */
    internal abstract fun appendTo(gen: AsmBuilder<T>)

    /**
     * Compile and cache the expression
     */
    private val compiledExpression by lazy{
        val builder = AsmBuilder(type.java, algebra, buildName(this))
        this.appendTo(builder)
        builder.generate()
    }

    override fun invoke(arguments: Map<String, T>): T = compiledExpression.invoke(arguments)
}

internal class AsmUnaryOperation<T : Any>(
    override val type: KClass<out T>,
    override val algebra: Algebra<T>,
    private val name: String,
    expr: AsmExpression<T>
) : AsmExpression<T>() {
    private val expr: AsmExpression<T> = expr.optimize()
    override fun tryEvaluate(): T? = algebra { unaryOperation(name, expr.tryEvaluate() ?: return@algebra null) }

    override fun appendTo(gen: AsmBuilder<T>) {
        gen.visitLoadAlgebra()

        if (!hasSpecific(algebra, name, 1))
            gen.visitStringConstant(name)

        expr.appendTo(gen)

        if (gen.tryInvokeSpecific(algebra, name, 1))
            return

        gen.visitAlgebraOperation(
            owner = AsmBuilder.ALGEBRA_CLASS,
            method = "unaryOperation",
            descriptor = "(L${AsmBuilder.STRING_CLASS};" +
                    "L${AsmBuilder.OBJECT_CLASS};)" +
                    "L${AsmBuilder.OBJECT_CLASS};"
        )
    }
}

internal class AsmBinaryOperation<T : Any>(
    override val type: KClass<out T>,
    override val algebra: Algebra<T>,
    private val name: String,
    first: AsmExpression<T>,
    second: AsmExpression<T>
) : AsmExpression<T>() {
    private val first: AsmExpression<T> = first.optimize()
    private val second: AsmExpression<T> = second.optimize()

    override fun tryEvaluate(): T? = algebra {
        binaryOperation(
            name,
            first.tryEvaluate() ?: return@algebra null,
            second.tryEvaluate() ?: return@algebra null
        )
    }

    override fun appendTo(gen: AsmBuilder<T>) {
        gen.visitLoadAlgebra()

        if (!hasSpecific(algebra, name, 2))
            gen.visitStringConstant(name)

        first.appendTo(gen)
        second.appendTo(gen)

        if (gen.tryInvokeSpecific(algebra, name, 2))
            return

        gen.visitAlgebraOperation(
            owner = AsmBuilder.ALGEBRA_CLASS,
            method = "binaryOperation",
            descriptor = "(L${AsmBuilder.STRING_CLASS};" +
                    "L${AsmBuilder.OBJECT_CLASS};" +
                    "L${AsmBuilder.OBJECT_CLASS};)" +
                    "L${AsmBuilder.OBJECT_CLASS};"
        )
    }
}

internal class AsmVariableExpression<T : Any>(
    override val type: KClass<out T>,
    override val algebra: Algebra<T>,
    private val name: String,
    private val default: T? = null
) : AsmExpression<T>() {
    override fun appendTo(gen: AsmBuilder<T>): Unit = gen.visitLoadFromVariables(name, default)
}

internal class AsmConstantExpression<T : Any>(
    override val type: KClass<out T>,
    override val algebra: Algebra<T>,
    private val value: T
) : AsmExpression<T>() {
    override fun tryEvaluate(): T = value
    override fun appendTo(gen: AsmBuilder<T>): Unit = gen.visitLoadFromConstants(value)
}

internal class AsmConstProductExpression<T : Any>(
    override val type: KClass<out T>,
    override val algebra: Space<T>,
    expr: AsmExpression<T>,
    private val const: Number
) : AsmExpression<T>() {
    private val expr: AsmExpression<T> = expr.optimize()

    override fun tryEvaluate(): T? = algebra { (expr.tryEvaluate() ?: return@algebra null) * const }

    override fun appendTo(gen: AsmBuilder<T>) {
        gen.visitLoadAlgebra()
        gen.visitNumberConstant(const)
        expr.appendTo(gen)

        gen.visitAlgebraOperation(
            owner = AsmBuilder.SPACE_OPERATIONS_CLASS,
            method = "multiply",
            descriptor = "(L${AsmBuilder.OBJECT_CLASS};" +
                    "L${AsmBuilder.NUMBER_CLASS};)" +
                    "L${AsmBuilder.OBJECT_CLASS};"
        )
    }
}

internal class AsmNumberExpression<T : Any>(
    override val type: KClass<out T>,
    override val algebra: NumericAlgebra<T>,
    private val value: Number
) : AsmExpression<T>() {
    override fun tryEvaluate(): T? = algebra.number(value)

    override fun appendTo(gen: AsmBuilder<T>): Unit = gen.visitNumberConstant(value)
}

internal abstract class FunctionalCompiledExpression<T> internal constructor(
    @JvmField protected val algebra: Algebra<T>,
    @JvmField protected val constants: Array<Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}

/**
 * A context class for [AsmExpression] construction.
 *
 * @param  algebra The algebra to provide for AsmExpressions built.
 */
open class AsmExpressionAlgebra<T : Any, A : NumericAlgebra<T>>(val type: KClass<out T>, val algebra: A) :
    NumericAlgebra<AsmExpression<T>>, ExpressionAlgebra<T, AsmExpression<T>> {

    /**
     * Builds an AsmExpression to wrap a number.
     */
    override fun number(value: Number): AsmExpression<T> = AsmNumberExpression(type, algebra, value)

    /**
     * Builds an AsmExpression of constant expression which does not depend on arguments.
     */
    override fun const(value: T): AsmExpression<T> = AsmConstantExpression(type, algebra, value)

    /**
     * Builds an AsmExpression to access a variable.
     */
    override fun variable(name: String, default: T?): AsmExpression<T> = AsmVariableExpression(type, algebra, name, default)

    /**
     * Builds an AsmExpression of dynamic call of binary operation [operation] on [left] and [right].
     */
    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(type, algebra, operation, left, right)

    /**
     * Builds an AsmExpression of dynamic call of unary operation with name [operation] on [arg].
     */
    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(type, algebra, operation, arg)
}

/**
 * A context class for [AsmExpression] construction for [Space] algebras.
 */
open class AsmExpressionSpace<T : Any, A>(type: KClass<out T>, algebra: A) : AsmExpressionAlgebra<T, A>(type, algebra),
    Space<AsmExpression<T>> where  A : Space<T>, A : NumericAlgebra<T> {
    override val zero: AsmExpression<T> get() = const(algebra.zero)

    /**
     * Builds an AsmExpression of addition of two another expressions.
     */
    override fun add(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(type, algebra, SpaceOperations.PLUS_OPERATION, a, b)

    /**
     * Builds an AsmExpression of multiplication of expression by number.
     */
    override fun multiply(a: AsmExpression<T>, k: Number): AsmExpression<T> = AsmConstProductExpression(type, algebra, a, k)

    operator fun AsmExpression<T>.plus(arg: T): AsmExpression<T> = this + const(arg)
    operator fun AsmExpression<T>.minus(arg: T): AsmExpression<T> = this - const(arg)
    operator fun T.plus(arg: AsmExpression<T>): AsmExpression<T> = arg + this
    operator fun T.minus(arg: AsmExpression<T>): AsmExpression<T> = arg - this

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        super<AsmExpressionAlgebra>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        super<AsmExpressionAlgebra>.binaryOperation(operation, left, right)
}

/**
 * A context class for [AsmExpression] construction for [Ring] algebras.
 */
open class AsmExpressionRing<T : Any, A>(type: KClass<out T>, algebra: A) : AsmExpressionSpace<T, A>(type, algebra),
    Ring<AsmExpression<T>> where  A : Ring<T>, A : NumericAlgebra<T> {
    override val one: AsmExpression<T> get() = const(algebra.one)

    /**
     * Builds an AsmExpression of multiplication of two expressions.
     */
    override fun multiply(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(type, algebra, RingOperations.TIMES_OPERATION, a, b)

    operator fun AsmExpression<T>.times(arg: T): AsmExpression<T> = this * const(arg)
    operator fun T.times(arg: AsmExpression<T>): AsmExpression<T> = arg * this

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        super<AsmExpressionSpace>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        super<AsmExpressionSpace>.binaryOperation(operation, left, right)

    override fun number(value: Number): AsmExpression<T> = super<AsmExpressionSpace>.number(value)
}

/**
 * A context class for [AsmExpression] construction for [Field] algebras.
 */
open class AsmExpressionField<T : Any, A>(type: KClass<out T>, algebra: A) :
    AsmExpressionRing<T, A>(type, algebra),
    Field<AsmExpression<T>> where A : Field<T>, A : NumericAlgebra<T> {
    /**
     * Builds an AsmExpression of division an expression by another one.
     */
    override fun divide(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(type, algebra, FieldOperations.DIV_OPERATION, a, b)

    operator fun AsmExpression<T>.div(arg: T): AsmExpression<T> = this / const(arg)
    operator fun T.div(arg: AsmExpression<T>): AsmExpression<T> = arg / this

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        super<AsmExpressionRing>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        super<AsmExpressionRing>.binaryOperation(operation, left, right)

    override fun number(value: Number): AsmExpression<T> = super<AsmExpressionRing>.number(value)
}
