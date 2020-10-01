package kscience.kmath.asm.internal

import kscience.kmath.ast.MST
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.Algebra
import kscience.kmath.operations.FieldOperations
import kscience.kmath.operations.RingOperations
import kscience.kmath.operations.SpaceOperations
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.commons.InstructionAdapter
import java.lang.reflect.Method
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private val methodNameAdapters: Map<Pair<String, Int>, String> by lazy {
    hashMapOf(
        SpaceOperations.PLUS_OPERATION to 2 to "add",
        RingOperations.TIMES_OPERATION to 2 to "multiply",
        FieldOperations.DIV_OPERATION to 2 to "divide",
        SpaceOperations.PLUS_OPERATION to 1 to "unaryPlus",
        SpaceOperations.MINUS_OPERATION to 1 to "unaryMinus",
        SpaceOperations.MINUS_OPERATION to 2 to "minus"
    )
}

/**
 * Returns ASM [Type] for given [Class].
 *
 * @author Iaroslav Postovalov
 */
internal inline val Class<*>.asm: Type
    get() = Type.getType(this)

/**
 * Returns singleton array with this value if the [predicate] is true, returns empty array otherwise.
 *
 * @author Iaroslav Postovalov
 */
internal inline fun <reified T> T.wrapToArrayIf(predicate: (T) -> Boolean): Array<T> {
    contract { callsInPlace(predicate, InvocationKind.EXACTLY_ONCE) }
    return if (predicate(this)) arrayOf(this) else emptyArray()
}

/**
 * Creates an [InstructionAdapter] from this [MethodVisitor].
 *
 * @author Iaroslav Postovalov
 */
private fun MethodVisitor.instructionAdapter(): InstructionAdapter = InstructionAdapter(this)

/**
 * Creates an [InstructionAdapter] from this [MethodVisitor] and applies [block] to it.
 *
 * @author Iaroslav Postovalov
 */
internal inline fun MethodVisitor.instructionAdapter(block: InstructionAdapter.() -> Unit): InstructionAdapter {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return instructionAdapter().apply(block)
}

/**
 * Constructs a [Label], then applies it to this visitor.
 *
 * @author Iaroslav Postovalov
 */
internal fun MethodVisitor.label(): Label = Label().also { visitLabel(it) }

/**
 * Creates a class name for [Expression] subclassed to implement [mst] provided.
 *
 * This methods helps to avoid collisions of class name to prevent loading several classes with the same name. If there
 * is a colliding class, change [collision] parameter or leave it `0` to check existing classes recursively.
 *
 * @author Iaroslav Postovalov
 */
internal tailrec fun buildName(mst: MST, collision: Int = 0): String {
    val name = "kscience.kmath.asm.generated.AsmCompiledExpression_${mst.hashCode()}_$collision"

    try {
        Class.forName(name)
    } catch (ignored: ClassNotFoundException) {
        return name
    }

    return buildName(mst, collision + 1)
}

@Suppress("FunctionName")
internal inline fun ClassWriter(flags: Int, block: ClassWriter.() -> Unit): ClassWriter {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return ClassWriter(flags).apply(block)
}

/**
 * Invokes [visitField] and applies [block] to the [FieldVisitor].
 *
 * @author Iaroslav Postovalov
 */
internal inline fun ClassWriter.visitField(
    access: Int,
    name: String,
    descriptor: String,
    signature: String?,
    value: Any?,
    block: FieldVisitor.() -> Unit
): FieldVisitor {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return visitField(access, name, descriptor, signature, value).apply(block)
}

private fun <T> AsmBuilder<T>.findSpecific(context: Algebra<T>, name: String, parameterTypes: Array<MstType>): Method? =
    context.javaClass.methods.find { method ->
        val nameValid = method.name == name
        val arityValid = method.parameters.size == parameterTypes.size
        val notBridgeInPrimitive = !(primitiveMode && method.isBridge)

        val paramsValid = method.parameterTypes.zip(parameterTypes).all { (type, mstType) ->
            !(mstType != MstType.NUMBER && type == java.lang.Number::class.java)
        }

        nameValid && arityValid && notBridgeInPrimitive && paramsValid
    }

/**
 * Checks if the target [context] for code generation contains a method with needed [name] and arity, also builds
 * type expectation stack for needed arity.
 *
 * @author Iaroslav Postovalov
 */
private fun <T> AsmBuilder<T>.buildExpectationStack(
    context: Algebra<T>,
    name: String,
    parameterTypes: Array<MstType>
): Boolean {
    val arity = parameterTypes.size
    val specific = findSpecific(context, methodNameAdapters[name to arity] ?: name, parameterTypes)

    if (specific != null)
        mapTypes(specific, parameterTypes).reversed().forEach { expectationStack.push(it) }
    else
        expectationStack.addAll(Collections.nCopies(arity, tType))

    return specific != null
}

private fun <T> AsmBuilder<T>.mapTypes(method: Method, parameterTypes: Array<MstType>): List<Type> = method
    .parameterTypes
    .zip(parameterTypes)
    .map { (type, mstType) ->
        when {
            type == java.lang.Number::class.java && mstType == MstType.NUMBER -> AsmBuilder.NUMBER_TYPE
            else -> if (primitiveMode) primitiveMask else primitiveMaskBoxed
        }
    }

/**
 * Checks if the target [context] for code generation contains a method with needed [name] and arity and inserts
 * [AsmBuilder.invokeAlgebraOperation] of this method.
 *
 * @author Iaroslav Postovalov
 */
private fun <T> AsmBuilder<T>.tryInvokeSpecific(
    context: Algebra<T>,
    name: String,
    parameterTypes: Array<MstType>
): Boolean {
    val arity = parameterTypes.size
    val theName = methodNameAdapters[name to arity] ?: name
    val spec = findSpecific(context, theName, parameterTypes) ?: return false
    val owner = context.javaClass.asm

    invokeAlgebraOperation(
        owner = owner.internalName,
        method = theName,
        descriptor = Type.getMethodDescriptor(primitiveMaskBoxed, *mapTypes(spec, parameterTypes).toTypedArray()),
        expectedArity = arity,
        opcode = INVOKEVIRTUAL
    )

    return true
}

/**
 * Builds specialized [context] call with option to fallback to generic algebra operation accepting [String].
 *
 * @author Iaroslav Postovalov
 */
internal inline fun <T> AsmBuilder<T>.buildAlgebraOperationCall(
    context: Algebra<T>,
    name: String,
    fallbackMethodName: String,
    parameterTypes: Array<MstType>,
    parameters: AsmBuilder<T>.() -> Unit
) {
    contract { callsInPlace(parameters, InvocationKind.EXACTLY_ONCE) }
    val arity = parameterTypes.size
    loadAlgebra()
    if (!buildExpectationStack(context, name, parameterTypes)) loadStringConstant(name)
    parameters()

    if (!tryInvokeSpecific(context, name, parameterTypes)) invokeAlgebraOperation(
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
