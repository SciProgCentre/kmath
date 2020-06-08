package scientifik.kmath.expressions

import scientifik.kmath.operations.*

abstract class AsmCompiledExpression<T> internal constructor(
    @JvmField private val algebra: Algebra<T>,
    @JvmField private val constants: MutableList<out Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}

interface AsmExpression<T> {
    fun tryEvaluate(): T? = null
    fun invoke(gen: AsmGenerationContext<T>)
}

internal class AsmVariableExpression<T>(val name: String, val default: T? = null) :
    AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromVariables(name, default)
}

internal class AsmConstantExpression<T>(val value: T) :
    AsmExpression<T> {
    override fun tryEvaluate(): T = value
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromConstants(value)
}

internal class AsmSumExpression<T>(
    private val algebra: SpaceOperations<T>,
    first: AsmExpression<T>,
    second: AsmExpression<T>
) : AsmExpression<T> {
    private val first: AsmExpression<T> = first.optimize()
    private val second: AsmExpression<T> = second.optimize()

    override fun tryEvaluate(): T? = algebra {
        (first.tryEvaluate() ?: return@algebra null) + (second.tryEvaluate() ?: return@algebra null)
    }

    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        first.invoke(gen)
        second.invoke(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.SPACE_OPERATIONS_CLASS,
            method = "add",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};L${AsmGenerationContext.OBJECT_CLASS};)L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmProductExpression<T>(
    private val algebra: RingOperations<T>,
    first: AsmExpression<T>,
    second: AsmExpression<T>
) : AsmExpression<T> {
    private val first: AsmExpression<T> = first.optimize()
    private val second: AsmExpression<T> = second.optimize()

    override fun tryEvaluate(): T? = algebra {
        (first.tryEvaluate() ?: return@algebra null) * (second.tryEvaluate() ?: return@algebra null)
    }

    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        first.invoke(gen)
        second.invoke(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.RING_OPERATIONS_CLASS,
            method = "multiply",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};L${AsmGenerationContext.OBJECT_CLASS};)L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmConstProductExpression<T>(
    private val algebra: SpaceOperations<T>,
    expr: AsmExpression<T>,
    private val const: Number
) : AsmExpression<T> {
    private val expr: AsmExpression<T> = expr.optimize()

    override fun tryEvaluate(): T? = algebra { (expr.tryEvaluate() ?: return@algebra null) * const }

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

internal class AsmDivExpression<T>(
    private val algebra: FieldOperations<T>,
    expr: AsmExpression<T>,
    second: AsmExpression<T>
) : AsmExpression<T> {
    private val expr: AsmExpression<T> = expr.optimize()
    private val second: AsmExpression<T> = second.optimize()

    override fun tryEvaluate(): T? = algebra {
        (expr.tryEvaluate() ?: return@algebra null) / (second.tryEvaluate() ?: return@algebra null)
    }

    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        expr.invoke(gen)
        second.invoke(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.FIELD_OPERATIONS_CLASS,
            method = "divide",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};L${AsmGenerationContext.OBJECT_CLASS};)L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}
