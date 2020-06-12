package scientifik.kmath.ast

import scientifik.kmath.expressions.Expression
import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.NumericAlgebra

//TODO stubs for asm generation

interface AsmExpression<T>

interface AsmExpressionAlgebra<T, A : Algebra<T>> : NumericAlgebra<AsmExpression<T>> {
    val algebra: A
}

fun <T> AsmExpression<T>.compile(): Expression<T> = TODO()

//TODO add converter for functional expressions

inline fun <reified T : Any, A : Algebra<T>> A.asm(
    block: AsmExpressionAlgebra<T, A>.() -> AsmExpression<T>
): Expression<T> = TODO()

inline fun <reified T : Any, A : Algebra<T>> A.asm(ast: MathSyntaxTree): Expression<T> = asm { compile(ast) }