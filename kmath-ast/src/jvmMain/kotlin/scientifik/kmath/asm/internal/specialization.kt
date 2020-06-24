package scientifik.kmath.asm.internal

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import scientifik.kmath.operations.Algebra

private val methodNameAdapters: Map<String, String> by lazy {
    hashMapOf(
        "+" to "add",
        "*" to "multiply",
        "/" to "divide"
    )
}

/**
 * Checks if the target [context] for code generation contains a method with needed [name] and [arity], also builds
 * type expectation stack for needed arity.
 *
 * @return `true` if contains, else `false`.
 */
internal fun <T> AsmBuilder<T>.buildExpectationStack(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    val hasSpecific = context.javaClass.methods.find { it.name == aName && it.parameters.size == arity } != null
    val t = if (primitiveMode && hasSpecific) PRIMITIVE_MASK else T_TYPE
    repeat(arity) { expectationStack.push(t) }

    return hasSpecific
}

/**
 * Checks if the target [context] for code generation contains a method with needed [name] and [arity] and inserts
 * [AsmBuilder.invokeAlgebraOperation] of this method.
 *
 * @return `true` if contains, else `false`.
 */
internal fun <T> AsmBuilder<T>.tryInvokeSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val aName = methodNameAdapters[name] ?: name

    val method =
        context.javaClass.methods.find {
            var suitableSignature = it.name == aName && it.parameters.size == arity

            if (primitiveMode && it.isBridge)
                suitableSignature = false

            suitableSignature
        } ?: return false

    val owner = context::class.java.name.replace('.', '/')

    invokeAlgebraOperation(
        owner = owner,
        method = aName,
        descriptor = Type.getMethodDescriptor(PRIMITIVE_MASK_BOXED, *Array(arity) { PRIMITIVE_MASK }),
        tArity = arity,
        opcode = Opcodes.INVOKEVIRTUAL
    )

    return true
}
