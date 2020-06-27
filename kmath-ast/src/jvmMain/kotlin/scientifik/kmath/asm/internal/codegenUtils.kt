package scientifik.kmath.asm.internal

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.commons.InstructionAdapter
import scientifik.kmath.ast.MST
import scientifik.kmath.expressions.Expression
import scientifik.kmath.operations.Algebra
import kotlin.reflect.KClass

private val methodNameAdapters: Map<Pair<String, Int>, String> by lazy {
    hashMapOf(
        "+" to 2 to "add",
        "*" to 2 to "multiply",
        "/" to 2 to "divide",
        "+" to 1 to "unaryPlus",
        "-" to 1 to "unaryMinus",
        "-" to 2 to "minus"
    )
}

internal val KClass<*>.asm: Type
    get() = Type.getType(java)

/**
 * Creates an [InstructionAdapter] from this [MethodVisitor].
 */
private fun MethodVisitor.instructionAdapter(): InstructionAdapter = InstructionAdapter(this)

/**
 * Creates an [InstructionAdapter] from this [MethodVisitor] and applies [block] to it.
 */
internal fun MethodVisitor.instructionAdapter(block: InstructionAdapter.() -> Unit): InstructionAdapter =
    instructionAdapter().apply(block)

/**
 * Constructs a [Label], then applies it to this visitor.
 */
internal fun MethodVisitor.label(): Label {
    val l = Label()
    visitLabel(l)
    return l
}

/**
 * Creates a class name for [Expression] subclassed to implement [mst] provided.
 *
 * This methods helps to avoid collisions of class name to prevent loading several classes with the same name. If there
 * is a colliding class, change [collision] parameter or leave it `0` to check existing classes recursively.
 */
internal tailrec fun buildName(mst: MST, collision: Int = 0): String {
    val name = "scientifik.kmath.asm.generated.AsmCompiledExpression_${mst.hashCode()}_$collision"

    try {
        Class.forName(name)
    } catch (ignored: ClassNotFoundException) {
        return name
    }

    return buildName(mst, collision + 1)
}

@Suppress("FunctionName")
internal inline fun ClassWriter(flags: Int, block: ClassWriter.() -> Unit): ClassWriter =
    ClassWriter(flags).apply(block)

internal inline fun ClassWriter.visitField(
    access: Int,
    name: String,
    descriptor: String,
    signature: String?,
    value: Any?,
    block: FieldVisitor.() -> Unit
): FieldVisitor = visitField(access, name, descriptor, signature, value).apply(block)

/**
 * Checks if the target [context] for code generation contains a method with needed [name] and [arity], also builds
 * type expectation stack for needed arity.
 *
 * @return `true` if contains, else `false`.
 */
private fun <T> AsmBuilder<T>.buildExpectationStack(context: Algebra<T>, name: String, arity: Int): Boolean {
    val theName = methodNameAdapters[name to arity] ?: name
    val hasSpecific = context.javaClass.methods.find { it.name == theName && it.parameters.size == arity } != null
    val t = if (primitiveMode && hasSpecific) primitiveMask else tType
    repeat(arity) { expectationStack.push(t) }
    return hasSpecific
}

/**
 * Checks if the target [context] for code generation contains a method with needed [name] and [arity] and inserts
 * [AsmBuilder.invokeAlgebraOperation] of this method.
 *
 * @return `true` if contains, else `false`.
 */
private fun <T> AsmBuilder<T>.tryInvokeSpecific(context: Algebra<T>, name: String, arity: Int): Boolean {
    val theName = methodNameAdapters[name to arity] ?: name

    context.javaClass.methods.find {
        var suitableSignature = it.name == theName && it.parameters.size == arity

        if (primitiveMode && it.isBridge)
            suitableSignature = false

        suitableSignature
    } ?: return false

    val owner = context::class.asm

    invokeAlgebraOperation(
        owner = owner.internalName,
        method = theName,
        descriptor = Type.getMethodDescriptor(primitiveMaskBoxed, *Array(arity) { primitiveMask }),
        expectedArity = arity,
        opcode = INVOKEVIRTUAL
    )

    return true
}

/**
 * Builds specialized algebra call with option to fallback to generic algebra operation accepting String.
 */
internal fun <T> AsmBuilder<T>.buildAlgebraOperationCall(
    context: Algebra<T>,
    name: String,
    fallbackMethodName: String,
    arity: Int,
    parameters: AsmBuilder<T>.() -> Unit
) {
    loadAlgebra()
    if (!buildExpectationStack(context, name, arity)) loadStringConstant(name)
    parameters()

    if (!tryInvokeSpecific(context, name, arity)) invokeAlgebraOperation(
        owner = AsmBuilder.ALGEBRA_TYPE.internalName,
        method = fallbackMethodName,

        descriptor = Type.getMethodDescriptor(
            AsmBuilder.OBJECT_TYPE,
            AsmBuilder.STRING_TYPE,
            *Array(arity) { AsmBuilder.OBJECT_TYPE }
        ),

        expectedArity = arity
    )
}

