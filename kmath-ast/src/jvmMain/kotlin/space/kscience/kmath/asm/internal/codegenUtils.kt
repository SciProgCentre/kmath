/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm.internal

import org.objectweb.asm.*
import org.objectweb.asm.commons.InstructionAdapter
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
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
internal fun MethodVisitor.label(): Label = Label().also(::visitLabel)

/**
 * Creates a class name for [Expression] based with appending [marker] to reduce collisions.
 *
 * These methods help to avoid collisions of class name to prevent loading several classes with the same name. If there
 * is a colliding class, change [collision] parameter or leave it `0` to check existing classes recursively.
 *
 * @author Iaroslav Postovalov
 */
internal tailrec fun buildName(marker: String, collision: Int = 0): String {
    val name = "space.kscience.kmath.asm.generated.CompiledExpression_${marker}_$collision"

    try {
        Class.forName(name)
    } catch (ignored: ClassNotFoundException) {
        return name
    }

    return buildName(marker, collision + 1)
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
    block: FieldVisitor.() -> Unit,
): FieldVisitor {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return visitField(access, name, descriptor, signature, value).apply(block)
}
