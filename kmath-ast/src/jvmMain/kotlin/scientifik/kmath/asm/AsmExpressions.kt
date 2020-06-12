package scientifik.kmath.asm

import scientifik.kmath.expressions.Expression
import scientifik.kmath.expressions.ExpressionAlgebra
import scientifik.kmath.operations.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.jvmName

interface AsmExpression<T> {
    fun tryEvaluate(): T? = null
    fun invoke(gen: AsmGenerationContext<T>)
}

internal val methodNameAdapters = mapOf("+" to "add", "*" to "multiply", "/" to "divide")

internal fun <T> hasSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context::class.memberFunctions.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    return true
}

internal fun <T> AsmGenerationContext<T>.tryInvokeSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context::class.memberFunctions.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    val owner = context::class.jvmName.replace('.', '/')

    val sig = buildString {
        append('(')
        repeat(arity) { append("L${AsmGenerationContext.OBJECT_CLASS};") }
        append(')')
        append("L${AsmGenerationContext.OBJECT_CLASS};")
    }

    visitAlgebraOperation(owner = owner, method = aName, descriptor = sig)

    return true
}

internal class AsmUnaryOperation<T>(private val context: Algebra<T>, private val name: String, expr: AsmExpression<T>) :
    AsmExpression<T> {
    private val expr: AsmExpression<T> = expr.optimize()
    override fun tryEvaluate(): T? = context { unaryOperation(name, expr.tryEvaluate() ?: return@context null) }

    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()

        if (!hasSpecific(context, name, 1))
            gen.visitStringConstant(name)

        expr.invoke(gen)

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

    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()

        if (!hasSpecific(context, name, 1))
            gen.visitStringConstant(name)

        first.invoke(gen)
        second.invoke(gen)

        if (gen.tryInvokeSpecific(context, name, 1))
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
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromVariables(name, default)
}

internal class AsmConstantExpression<T>(private val value: T) :
    AsmExpression<T> {
    override fun tryEvaluate(): T = value
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromConstants(value)
}

internal class AsmConstProductExpression<T>(
    private val context: Space<T>,
    expr: AsmExpression<T>,
    private val const: Number
) :
    AsmExpression<T> {
    private val expr: AsmExpression<T> = expr.optimize()

    override fun tryEvaluate(): T? = context { (expr.tryEvaluate() ?: return@context null) * const }

    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        gen.visitNumberConstant(const)
        expr.invoke(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.SPACE_OPERATIONS_CLASS,
            method = "multiply",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};L${AsmGenerationContext.NUMBER_CLASS};)L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmNumberExpression<T>(private val context: NumericAlgebra<T>, private val value: Number) : AsmExpression<T> {
    override fun tryEvaluate(): T? = context.number(value)

    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitNumberConstant(value)
}

internal abstract class FunctionalCompiledExpression<T> internal constructor(
    @JvmField protected val algebra: Algebra<T>,
    @JvmField protected val constants: Array<Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}

interface AsmExpressionAlgebra<T, A : NumericAlgebra<T>> : NumericAlgebra<AsmExpression<T>>,
    ExpressionAlgebra<T, AsmExpression<T>> {
    val algebra: A
    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)

    override fun number(value: Number): AsmExpression<T> = AsmNumberExpression(algebra, value)
    override fun const(value: T): AsmExpression<T> = AsmConstantExpression(value)
    override fun variable(name: String, default: T?): AsmExpression<T> = AsmVariableExpression(name, default)
}

open class AsmExpressionSpace<T, A>(override val algebra: A) : AsmExpressionAlgebra<T, A>,
    Space<AsmExpression<T>> where  A : Space<T>, A : NumericAlgebra<T> {
    override val zero: AsmExpression<T>
        get() = const(algebra.zero)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)

    override fun add(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, SpaceOperations.PLUS_OPERATION, a, b)

    override fun multiply(a: AsmExpression<T>, k: Number): AsmExpression<T> = AsmConstProductExpression(algebra, a, k)
    operator fun AsmExpression<T>.plus(arg: T): AsmExpression<T> = this + const(arg)
    operator fun AsmExpression<T>.minus(arg: T): AsmExpression<T> = this - const(arg)
    operator fun T.plus(arg: AsmExpression<T>): AsmExpression<T> = arg + this
    operator fun T.minus(arg: AsmExpression<T>): AsmExpression<T> = arg - this
}

open class AsmExpressionRing<T, A>(override val algebra: A) : AsmExpressionSpace<T, A>(algebra),
    Ring<AsmExpression<T>> where  A : Ring<T>, A : NumericAlgebra<T> {
    override val one: AsmExpression<T>
        get() = const(algebra.one)

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    override fun number(value: Number): AsmExpression<T> = const(algebra { one * value })

    override fun multiply(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, RingOperations.TIMES_OPERATION, a, b)

    operator fun AsmExpression<T>.times(arg: T): AsmExpression<T> = this * const(arg)
    operator fun T.times(arg: AsmExpression<T>): AsmExpression<T> = arg * this
}

open class AsmExpressionField<T, A>(override val algebra: A) :
    AsmExpressionRing<T, A>(algebra),
    Field<AsmExpression<T>> where A : Field<T>, A : NumericAlgebra<T> {

    override fun unaryOperation(operation: String, arg: AsmExpression<T>): AsmExpression<T> =
        AsmUnaryOperation(algebra, operation, arg)

    override fun binaryOperation(operation: String, left: AsmExpression<T>, right: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, operation, left, right)

    override fun divide(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmBinaryOperation(algebra, FieldOperations.DIV_OPERATION, a, b)

    operator fun AsmExpression<T>.div(arg: T): AsmExpression<T> = this / const(arg)
    operator fun T.div(arg: AsmExpression<T>): AsmExpression<T> = arg / this
}

