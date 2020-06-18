package scientifik.kmath.asm.internal

import org.objectweb.asm.Opcodes
import scientifik.kmath.operations.Algebra

private val methodNameAdapters: Map<String, String> by lazy {
    hashMapOf("+" to "add", "*" to "multiply", "/" to "divide")
}

internal fun <T> hasSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context.javaClass.methods.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    return true
}

internal fun <T> AsmBuilder<T>.tryInvokeSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context.javaClass.methods.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    val owner = context::class.java.name.replace('.', '/')

    val sig = buildString {
        append('(')
        repeat(arity) { append("L${AsmBuilder.OBJECT_CLASS};") }
        append(')')
        append("L${AsmBuilder.OBJECT_CLASS};")
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
