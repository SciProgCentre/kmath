package scientifik.kmath.asm

import scientifik.kmath.operations.Algebra
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.jvmName

private val methodNameAdapters: Map<String, String> = mapOf("+" to "add", "*" to "multiply", "/" to "divide")

internal fun <T> hasSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context::class.memberFunctions.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    return true
}

internal fun <T> AsmGenerationContext<T>.tryInvokeSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context::class.memberFunctions.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    val owner = context::class.jvmName.replace('.', '/')

    val sig = buildString {
        append('(')
        repeat(arity) { append("L${AsmGenerationContext.OBJECT_CLASS};") }
        append(')')
        append("L${AsmGenerationContext.OBJECT_CLASS};")
    }

    visitAlgebraOperation(owner = owner, method = aName, descriptor = sig)

    return true
}

@PublishedApi
internal fun <T> AsmExpression<T>.optimize(): AsmExpression<T> {
    val a = tryEvaluate()
    return if (a == null) this else AsmConstantExpression(a)
}
