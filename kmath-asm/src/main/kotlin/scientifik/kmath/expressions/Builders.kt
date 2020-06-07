package scientifik.kmath.expressions

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space

@PublishedApi
internal fun buildName(expression: AsmExpression<*>, collision: Int = 0): String {
    val name = "scientifik.kmath.expressions.generated.AsmCompiledExpression_${expression.hashCode()}_$collision"

    try {
        Class.forName(name)
    } catch (ignored: ClassNotFoundException) {
        return name
    }

    return buildName(expression, collision + 1)
}


inline fun <reified T> asmSpace(
    algebra: Space<T>,
    block: AsmExpressionSpace<T>.() -> AsmExpression<T>
): Expression<T> {
    val expression = AsmExpressionSpace(algebra).block()
    val ctx = AsmGenerationContext(T::class.java, algebra, buildName(expression))
    expression.invoke(ctx)
    return ctx.generate()
}

inline fun <reified T> asmField(
    algebra: Field<T>,
    block: AsmExpressionField<T>.() -> AsmExpression<T>
): Expression<T> {
    val expression = AsmExpressionField(algebra).block()
    val ctx = AsmGenerationContext(T::class.java, algebra, buildName(expression))
    expression.invoke(ctx)
    return ctx.generate()
}
