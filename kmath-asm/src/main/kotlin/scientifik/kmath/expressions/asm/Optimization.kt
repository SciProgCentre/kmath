package scientifik.kmath.expressions.asm

@PublishedApi
internal fun <T> AsmExpression<T>.optimize(): AsmExpression<T> {
    val a = tryEvaluate()
    return if (a == null) this else AsmConstantExpression(a)
}
