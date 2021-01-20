package kscience.kmath.asm.internal

import kscience.kmath.ast.MST
import kscience.kmath.expressions.Expression
import org.objectweb.asm.*
import org.objectweb.asm.commons.InstructionAdapter
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
