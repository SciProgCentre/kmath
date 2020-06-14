package scientifik.kmath.asm.internal

import org.objectweb.asm.Opcodes
import scientifik.kmath.asm.AsmConstantExpression
import scientifik.kmath.asm.AsmNode
import scientifik.kmath.operations.Algebra

private val methodNameAdapters: Map<String, String> = mapOf("+" to "add", "*" to "multiply", "/" to "divide")

internal fun <T> hasSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context::class.java.methods.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    return true
}

internal fun <T> AsmGenerator<T>.tryInvokeSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context::class.java.methods.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    val owner = context::class.java.name.replace('.', '/')

    val sig = buildString {
        append('(')
        repeat(arity) { append("L${AsmGenerator.OBJECT_CLASS};") }
        append(')')
        append("L${AsmGenerator.OBJECT_CLASS};")
    }

    invokeAlgebraOperation(
        owner = owner,
        method = aName,
        descriptor = sig,
        opcode = Opcodes.INVOKEVIRTUAL,
        isInterface = false
    )

    return true
}

@PublishedApi
internal fun <T> AsmNode<T>.optimize(): AsmNode<T> {
    val a = tryEvaluate()
    return if (a == null) this else AsmConstantExpression(a)
}
