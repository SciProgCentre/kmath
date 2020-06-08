package scientifik.kmath.expressions

import scientifik.kmath.operations.Algebra
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


inline fun <reified T, I> asm(i: I, algebra: Algebra<T>, block: I.() -> AsmExpression<T>): Expression<T> {
    val expression = i.block().optimize()
    val ctx = AsmGenerationContext(T::class.java, algebra, buildName(expression))
    expression.invoke(ctx)
    return ctx.generate()
}

inline fun <reified T> asmSpace(
    algebra: Space<T>,
    block: AsmExpressionSpace<T>.() -> AsmExpression<T>
): Expression<T> = asm(AsmExpressionSpace(algebra), algebra, block)

inline fun <reified T> asmField(
    algebra: Field<T>,
    block: AsmExpressionField<T>.() -> AsmExpression<T>
): Expression<T> = asm(AsmExpressionField(algebra), algebra, block)
