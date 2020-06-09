package scientifik.kmath.expressions.asm

import scientifik.kmath.expressions.asm.AsmConstantExpression
import scientifik.kmath.expressions.asm.AsmExpression

fun <T> AsmExpression<T>.optimize(): AsmExpression<T> {
    val a = tryEvaluate()
    return if (a == null) this else AsmConstantExpression(a)
}
