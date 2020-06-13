package scientifik.kmath.asm

import scientifik.kmath.asm.internal.hasSpecific
import scientifik.kmath.asm.internal.optimize
import scientifik.kmath.asm.internal.tryInvokeSpecific
import scientifik.kmath.expressions.Expression
import scientifik.kmath.expressions.ExpressionAlgebra
import scientifik.kmath.operations.*

/**
 * A function declaration that could be compiled to [AsmGenerationContext].
 *
 * @param T the type the stored function returns.
 */
interface AsmExpression<T> {
    /**
     * Tries to evaluate this function without its variables. This method is intended for optimization.
     *
     * @return `null` if the function depends on its variables, the value if the function is a constant.
     */
    fun tryEvaluate(): T? = null

    /**
     * Compiles this declaration.
     *
     * @param gen the target [AsmGenerationContext].
     */
    fun compile(gen: AsmGenerationContext<T>)
}

internal class AsmUnaryOperation<T>(private val context: Algebra<T>, private val name: String, expr: AsmExpression<T>) :
    AsmExpression<T> {
    private val expr: AsmExpression<T> = expr.optimize()
    override fun tryEvaluate(): T? = context { unaryOperation(name, expr.tryEvaluate() ?: return@context null) }

    override fun compile(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()

        if (!hasSpecific(context, name, 1))
            gen.visitStringConstant(name)

        expr.compile(gen)

        if (gen.tryInvokeSpecific(context, name, 1))
            return

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.ALGEBRA_CLASS,
            method = "unaryOperation",
            descriptor = "(L${AsmGenerationContext.STRING_CLASS};" +
                    "L${AsmGenerationContext.OBJECT_CLASS};)" +
                    "L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmBinaryOperation<T>(
    private val context: Algebra<T>,
    private val name: String,
    first: AsmExpression<T>,
    second: AsmExpression<T>
) : AsmExpression<T> {
    private val first: AsmExpression<T> = first.optimize()
    private val second: AsmExpression<T> = second.optimize()

    override fun tryEvaluate(): T? = context {
        binaryOperation(
            name,
            first.tryEvaluate() ?: return@context null,
            second.tryEvaluate() ?: return@context null
        )
    }

    override fun compile(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()

        if (!hasSpecific(context, name, 2))
            gen.visitStringConstant(name)

        first.compile(gen)
        second.compile(gen)

        if (gen.tryInvokeSpecific(context, name, 2))
            return

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.ALGEBRA_CLASS,
            method = "binaryOperation",
            descriptor = "(L${AsmGenerationContext.STRING_CLASS};" +
                    "L${AsmGenerationContext.OBJECT_CLASS};" +
                    "L${AsmGenerationContext.OBJECT_CLASS};)" +
                    "L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmVariableExpression<T>(private val name: String, private val default: T? = null) :
    AsmExpression<T> {
    override fun compile(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromVariables(name, default)
}

internal class AsmConstantExpression<T>(private val value: T) :
    AsmExpression<T> {
    override fun tryEvaluate(): T = value
    override fun compile(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromConstants(value)
}

internal class AsmConstProductExpression<T>(
    private val context: Space<T>,
    expr: AsmExpression<T>,
    private val const: Number
) :
    AsmExpression<T> {
    private val expr: AsmExpression<T> = expr.optimize()

    override fun tryEvaluate(): T? = context { (expr.tryEvaluate() ?: return@context null) * const }

    override fun compile(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        gen.visitNumberConstant(const)
        expr.compile(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.SPACE_OPERATIONS_CLASS,
            method = "multiply",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};" +
                    "L${AsmGenerationContext.NUMBER_CLASS};)" +
                    "L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmNumberExpression<T>(private val context: NumericAlgebra<T>, private val value: Number) :
    AsmExpression<T> {
    override fun tryEvaluate(): T? = context.number(value)

    override fun compile(gen: AsmGenerationContext<T>): Unit = gen.visitNumberConstant(value)
}

internal abstract class FunctionalCompiledExpression<T> internal constructor(
    @JvmField protected val algebra: Algebra<T>,
    @JvmField protected val constants: Array<Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}

/**
 * A context class for [AsmExpression] construction.
 */
interface AsmExpressionAlgebra<T, A : NumericAlgebra<T>> : NumericAlgebra<AsmExpression<T>>,
    ExpressionAlgebra<T, AsmExpression<T>> {
    /**
     * The algebra to provide for AsmExpressions built.
     */
    val algebra: A

    /**
     * Builds an AsmExpression to wrap a number.
     */
    override fun number(value: Number): AsmExpression<T> = AsmNumberExpression(algebra, value)

    /**
     * Builds an AsmExpression of constant expression which does not depend on arguments.
     */
    override fun const(value: T): AsmExpression<T> = AsmConstantExpression(value)

    /**
     * Builds an AsmExpression to access a variable.
     */
    override fun variable(name: String, default: T?): AsmExpression<T> = AsmVariableExpression(name, default)

    /**
     * Builds an AsmExpression of dynamic call of binary operation [operation] on [left] and [right].
     */
    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    /**
     * Builds an AsmExpression of dynamic call of unary operation with name [operation] on [arg].
     */
    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)
}

/**
 * A context class for [AsmExpression] construction for [Space] algebras.
 */
open class AsmExpressionSpace<T, A>(override val algebra: A) : AsmExpressionAlgebra<T, A>,
    Space<AsmExpression<T>> where  A : Space<T>, A : NumericAlgebra<T> {
    override val zero: AsmExpression<T>
        get() = const(algebra.zero)

    /**
     * Builds an AsmExpression of addition of two another expressions.
     */
    override fun add(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, SpaceOperations.PLUS_OPERATION, a, b)

    /**
     * Builds an AsmExpression of multiplication of expression by number.
     */
    override fun multiply(a: AsmExpression<T>, k: Number): AsmExpression<T> = AsmConstProductExpression(algebra, a, k)

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
open class AsmExpressionRing<T, A>(override val algebra: A) : AsmExpressionSpace<T, A>(algebra),
    Ring<AsmExpression<T>> where  A : Ring<T>, A : NumericAlgebra<T> {
    override val one: AsmExpression<T>
        get() = const(algebra.one)

    /**
     * Builds an AsmExpression of multiplication of two expressions.
     */
    override fun multiply(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, RingOperations.TIMES_OPERATION, a, b)

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
open class AsmExpressionField<T, A>(override val algebra: A) :
    AsmExpressionRing<T, A>(algebra),
    Field<AsmExpression<T>> where A : Field<T>, A : NumericAlgebra<T> {
    /**
     * Builds an AsmExpression of division an expression by another one.
     */
    override fun divide(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, FieldOperations.DIV_OPERATION, a, b)

    operator fun AsmExpression<T>.div(arg: T): AsmExpression<T> = this / const(arg)
    operator fun T.div(arg: AsmExpression<T>): AsmExpression<T> = arg / this

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        super<AsmExpressionRing>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        super<AsmExpressionRing>.binaryOperation(operation, left, right)

    override fun number(value: Number): AsmExpression<T> = super<AsmExpressionRing>.number(value)
}
