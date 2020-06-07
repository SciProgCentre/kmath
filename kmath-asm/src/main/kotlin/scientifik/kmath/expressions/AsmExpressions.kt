package scientifik.kmath.expressions

import scientifik.kmath.operations.Algebra

abstract class AsmCompiledExpression<T> internal constructor(
    @JvmField private val algebra: Algebra<T>,
    @JvmField private val constants: MutableList<out Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}

interface AsmExpression<T> {
    fun invoke(gen: AsmGenerationContext<T>)
}

internal class AsmVariableExpression<T>(val name: String, val default: T? = null) :
    AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromVariables(name, default)
}

internal class AsmConstantExpression<T>(val value: T) :
    AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromConstants(value)
}

internal class AsmSumExpression<T>(
    val first: AsmExpression<T>,
    val second: AsmExpression<T>
) : AsmExpression<T> {
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
    val first: AsmExpression<T>,
    val second: AsmExpression<T>
) : AsmExpression<T> {
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
    val expr: AsmExpression<T>,
    val const: Number
) : AsmExpression<T> {
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
    val expr: AsmExpression<T>,
    val second: AsmExpression<T>
) : AsmExpression<T> {
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
