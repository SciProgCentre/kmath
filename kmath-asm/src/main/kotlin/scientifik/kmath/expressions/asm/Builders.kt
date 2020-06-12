package scientifik.kmath.expressions.asm

import scientifik.kmath.expressions.Expression
import scientifik.kmath.expressions.ExpressionAlgebra
import scientifik.kmath.operations.*

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


inline fun <reified T, E : ExpressionAlgebra<T, AsmExpression<T>>> asm(
    i: E,
    algebra: Algebra<T>,
    block: E.() -> AsmExpression<T>
): Expression<T> {
    val expression = i.block().optimize()
    val ctx = AsmGenerationContext(T::class.java, algebra, buildName(expression))
    expression.invoke(ctx)
    return ctx.generate()
}

inline fun <reified T> buildAsmAlgebra(
    algebra: Algebra<T>,
    block: AsmExpressionAlgebra<T>.() -> AsmExpression<T>
): Expression<T> = asm(AsmExpressionAlgebra(algebra), algebra, block)

inline fun <reified T> buildAsmSpace(
    algebra: Space<T>,
    block: AsmExpressionSpace<T>.() -> AsmExpression<T>
): Expression<T> = asm(AsmExpressionSpace(algebra), algebra, block)

inline fun <reified T> buildAsmRing(
    algebra: Ring<T>,
    block: AsmExpressionRing<T>.() -> AsmExpression<T>
): Expression<T> = asm(AsmExpressionRing(algebra), algebra, block)

inline fun <reified T> buildAsmField(
    algebra: Field<T>,
    block: AsmExpressionField<T>.() -> AsmExpression<T>
): Expression<T> = asm(AsmExpressionField(algebra), algebra, block)
