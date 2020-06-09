package scientifik.kmath.expressions.asm

import scientifik.kmath.expressions.Expression
import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.invoke

interface AsmExpression<T> {
    fun tryEvaluate(): T? = null
    fun invoke(gen: AsmGenerationContext<T>)
}

internal class AsmUnaryOperation<T>(private val context: Algebra<T>, private val name: String, expr: AsmExpression<T>) :
    AsmExpression<T> {
    private val expr: AsmExpression<T> = expr.optimize()

    override fun tryEvaluate(): T? = context {
        unaryOperation(
            name,
            expr.tryEvaluate() ?: return@context null
        )
    }

    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        gen.visitStringConstant(name)
        expr.invoke(gen)

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
        gen.visitStringConstant(name)
        first.invoke(gen)
        second.invoke(gen)

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

internal class AsmVariableExpression<T>(private val name: String, private val default: T? = null) : AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromVariables(name, default)
}

internal class AsmConstantExpression<T>(private val value: T) : AsmExpression<T> {
    override fun tryEvaluate(): T = value
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromConstants(value)
}

internal class AsmConstProductExpression<T>(private val context: Space<T>, expr: AsmExpression<T>, private val const: Number) :
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

internal abstract class FunctionalCompiledExpression<T> internal constructor(
    @JvmField protected val algebra: Algebra<T>,
    @JvmField protected val constants: MutableList<out Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}
