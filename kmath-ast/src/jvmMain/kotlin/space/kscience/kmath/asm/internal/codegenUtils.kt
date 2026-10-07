/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm.internal

import org.objectweb.asm.*
import org.objectweb.asm.commons.InstructionAdapter
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.DoubleExpression
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.IntExpression
import space.kscience.kmath.expressions.LongExpression
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns ASM [Type] for given [Class].
 *
 * @author Iaroslav Postovalov
 */
internal inline val Class<*>.asm: Type
    get() = Type.getType(this)

// Interface type and primitive argument/result class of array-based expressions.
@OptIn(UnstableKMathAPI::class)
internal fun arrayExpressionTypes(function: Expression<*>): Pair<Type, Class<*>>? = when (function) {
    is DoubleExpression -> DoubleExpression::class.java.asm to java.lang.Double.TYPE
    is IntExpression -> IntExpression::class.java.asm to Integer.TYPE
    is LongExpression -> LongExpression::class.java.asm to java.lang.Long.TYPE
    else -> null
}

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
@Suppress("UnusedReceiverParameter")
internal fun MethodVisitor.label(): Label = Label().also(::visitLabel)

// Hidden classes must be in the package of the lookup class defining them; the JVM makes their names unique.
internal fun buildName(marker: String): String = "space.kscience.kmath.asm.internal.CompiledExpression_$marker"

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
