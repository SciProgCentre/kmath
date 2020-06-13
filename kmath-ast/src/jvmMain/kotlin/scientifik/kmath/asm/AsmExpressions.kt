package scientifik.kmath.asm

import scientifik.kmath.asm.internal.AsmGenerationContext
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
abstract class AsmNode<T> internal constructor() {
    /**
     * Tries to evaluate this function without its variables. This method is intended for optimization.
     *
     * @return `null` if the function depends on its variables, the value if the function is a constant.
     */
    internal open fun tryEvaluate(): T? = null

    /**
     * Compiles this declaration.
     *
     * @param gen the target [AsmGenerationContext].
     */
    @PublishedApi
    internal abstract fun compile(gen: AsmGenerationContext<T>)
}

internal class AsmUnaryOperation<T>(private val context: Algebra<T>, private val name: String, expr: AsmNode<T>) :
    AsmNode<T>() {
    private val expr: AsmNode<T> = expr.optimize()
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
    first: AsmNode<T>,
    second: AsmNode<T>
) : AsmNode<T>() {
    private val first: AsmNode<T> = first.optimize()
    private val second: AsmNode<T> = second.optimize()

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
    AsmNode<T>() {
    override fun compile(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromVariables(name, default)
}

internal class AsmConstantExpression<T>(private val value: T) :
    AsmNode<T>() {
    override fun tryEvaluate(): T = value
    override fun compile(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromConstants(value)
}

internal class AsmConstProductExpression<T>(
    private val context: Space<T>,
    expr: AsmNode<T>,
    private val const: Number
) : AsmNode<T>() {
    private val expr: AsmNode<T> = expr.optimize()

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
    AsmNode<T>() {
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
 * A context class for [AsmNode] construction.
 */
interface AsmExpressionAlgebra<T, A : NumericAlgebra<T>> : NumericAlgebra<AsmNode<T>>,
    ExpressionAlgebra<T, AsmNode<T>> {
    /**
     * The algebra to provide for AsmExpressions built.
     */
    val algebra: A

    /**
     * Builds an AsmExpression to wrap a number.
     */
    override fun number(value: Number): AsmNode<T> = AsmNumberExpression(algebra, value)

    /**
     * Builds an AsmExpression of constant expression which does not depend on arguments.
     */
    override fun const(value: T): AsmNode<T> = AsmConstantExpression(value)

    /**
     * Builds an AsmExpression to access a variable.
     */
    override fun variable(name: String, default: T?): AsmNode<T> = AsmVariableExpression(name, default)

    /**
     * Builds an AsmExpression of dynamic call of binary operation [operation] on [left] and [right].
     */
    override fun binaryOperation(operation: String, left: AsmNode<T>, right: AsmNode<T>): AsmNode<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    /**
     * Builds an AsmExpression of dynamic call of unary operation with name [operation] on [arg].
     */
    override fun unaryOperation(operation: String, arg: AsmNode<T>): AsmNode<T> =
        AsmUnaryOperation(algebra, operation, arg)
}

/**
 * A context class for [AsmNode] construction for [Space] algebras.
 */
open class AsmExpressionSpace<T, A>(override val algebra: A) : AsmExpressionAlgebra<T, A>,
    Space<AsmNode<T>> where  A : Space<T>, A : NumericAlgebra<T> {
    override val zero: AsmNode<T>
        get() = const(algebra.zero)

    /**
     * Builds an AsmExpression of addition of two another expressions.
     */
    override fun add(a: AsmNode<T>, b: AsmNode<T>): AsmNode<T> =
        AsmBinaryOperation(algebra, SpaceOperations.PLUS_OPERATION, a, b)

    /**
     * Builds an AsmExpression of multiplication of expression by number.
     */
    override fun multiply(a: AsmNode<T>, k: Number): AsmNode<T> = AsmConstProductExpression(algebra, a, k)

    operator fun AsmNode<T>.plus(arg: T): AsmNode<T> = this + const(arg)
    operator fun AsmNode<T>.minus(arg: T): AsmNode<T> = this - const(arg)
    operator fun T.plus(arg: AsmNode<T>): AsmNode<T> = arg + this
    operator fun T.minus(arg: AsmNode<T>): AsmNode<T> = arg - this

    override fun unaryOperation(operation: String, arg: AsmNode<T>): AsmNode<T> =
        super<AsmExpressionAlgebra>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: AsmNode<T>, right: AsmNode<T>): AsmNode<T> =
        super<AsmExpressionAlgebra>.binaryOperation(operation, left, right)
}

/**
 * A context class for [AsmNode] construction for [Ring] algebras.
 */
open class AsmExpressionRing<T, A>(override val algebra: A) : AsmExpressionSpace<T, A>(algebra),
    Ring<AsmNode<T>> where  A : Ring<T>, A : NumericAlgebra<T> {
    override val one: AsmNode<T>
        get() = const(algebra.one)

    /**
     * Builds an AsmExpression of multiplication of two expressions.
     */
    override fun multiply(a: AsmNode<T>, b: AsmNode<T>): AsmNode<T> =
        AsmBinaryOperation(algebra, RingOperations.TIMES_OPERATION, a, b)

    operator fun AsmNode<T>.times(arg: T): AsmNode<T> = this * const(arg)
    operator fun T.times(arg: AsmNode<T>): AsmNode<T> = arg * this

    override fun unaryOperation(operation: String, arg: AsmNode<T>): AsmNode<T> =
        super<AsmExpressionSpace>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: AsmNode<T>, right: AsmNode<T>): AsmNode<T> =
        super<AsmExpressionSpace>.binaryOperation(operation, left, right)

    override fun number(value: Number): AsmNode<T> = super<AsmExpressionSpace>.number(value)
}

/**
 * A context class for [AsmNode] construction for [Field] algebras.
 */
open class AsmExpressionField<T, A>(override val algebra: A) :
    AsmExpressionRing<T, A>(algebra),
    Field<AsmNode<T>> where A : Field<T>, A : NumericAlgebra<T> {
    /**
     * Builds an AsmExpression of division an expression by another one.
     */
    override fun divide(a: AsmNode<T>, b: AsmNode<T>): AsmNode<T> =
        AsmBinaryOperation(algebra, FieldOperations.DIV_OPERATION, a, b)

    operator fun AsmNode<T>.div(arg: T): AsmNode<T> = this / const(arg)
    operator fun T.div(arg: AsmNode<T>): AsmNode<T> = arg / this

    override fun unaryOperation(operation: String, arg: AsmNode<T>): AsmNode<T> =
        super<AsmExpressionRing>.unaryOperation(operation, arg)

    override fun binaryOperation(operation: String, left: AsmNode<T>, right: AsmNode<T>): AsmNode<T> =
        super<AsmExpressionRing>.binaryOperation(operation, left, right)

    override fun number(value: Number): AsmNode<T> = super<AsmExpressionRing>.number(value)
}
