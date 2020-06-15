package scientifik.kmath.asm

import scientifik.kmath.ast.MST
import scientifik.kmath.ast.evaluate
import scientifik.kmath.expressions.Expression
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.NumericAlgebra
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space

internal fun buildName(expression: AsmExpression<*>, collision: Int = 0): String {
    val name = "scientifik.kmath.expressions.generated.AsmCompiledExpression_${expression.hashCode()}_$collision"

    try {
        Class.forName(name)
    } catch (ignored: ClassNotFoundException) {
        return name
    }

    return buildName(expression, collision + 1)
}

inline fun <reified T : Any, A : NumericAlgebra<T>, E : AsmExpressionAlgebra<T, A>> A.asm(
    expressionAlgebra: E,
    block: E.() -> AsmExpression<T>
): Expression<T> = expressionAlgebra.block()

inline fun <reified T : Any> NumericAlgebra<T>.asm(ast: MST): Expression<T> =
    AsmExpressionAlgebra(T::class, this).evaluate(ast)

inline fun <reified T : Any, A> A.asmSpace(block: AsmExpressionSpace<T, A>.() -> AsmExpression<T>): Expression<T> where A : NumericAlgebra<T>, A : Space<T> =
    AsmExpressionSpace<T, A>(T::class, this).block()

inline fun <reified T : Any, A> A.asmSpace(ast: MST): Expression<T> where A : NumericAlgebra<T>, A : Space<T> =
    asmSpace { evaluate(ast) }

inline fun <reified T : Any, A> A.asmRing(block: AsmExpressionRing<T, A>.() -> AsmExpression<T>): Expression<T> where A : NumericAlgebra<T>, A : Ring<T> =
    AsmExpressionRing(T::class, this).block()

inline fun <reified T : Any, A> A.asmRing(ast: MST): Expression<T> where A : NumericAlgebra<T>, A : Ring<T> =
    asmRing { evaluate(ast) }

inline fun <reified T : Any, A> A.asmField(block: AsmExpressionField<T, A>.() -> AsmExpression<T>): Expression<T> where A : NumericAlgebra<T>, A : Field<T> =
    AsmExpressionField(T::class, this).block()

inline fun <reified T : Any, A> A.asmField(ast: MST): Expression<T> where A : NumericAlgebra<T>, A : Field<T> =
    asmRing { evaluate(ast) }
