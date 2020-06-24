package scientifik.kmath.asm.internal

import org.objectweb.asm.Opcodes
import scientifik.kmath.operations.Algebra

private val methodNameAdapters: Map<String, String> by lazy {
    hashMapOf(
        "+" to "add",
        "*" to "multiply",
        "/" to "divide"
    )
}

/**
 * Checks if the target [context] for code generation contains a method with needed [name] and [arity].
 *
 * @return `true` if contains, else `false`.
 */
internal fun <T> hasSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context.javaClass.methods.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    return true
}

/**
 * Checks if the target [context] for code generation contains a method with needed [name] and [arity] and inserts
 * [AsmBuilder.invokeAlgebraOperation] of this method.
 *
 * @return `true` if contains, else `false`.
 */
internal fun <T> AsmBuilder<T>.tryInvokeSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    context.javaClass.methods.find { it.name == aName && it.parameters.size == arity }
        ?: return false

    val owner = context::class.java.name.replace('.', '/')

    val sig = buildString {
        append('(')
        repeat(arity) { append(primitiveTypeSig) }
        append(')')
        append(primitiveTypeReturnSig)
    }

    invokeAlgebraOperation(
        owner = owner,
        method = aName,
        descriptor = sig,
        opcode = Opcodes.INVOKEVIRTUAL
    )

    return true
}
