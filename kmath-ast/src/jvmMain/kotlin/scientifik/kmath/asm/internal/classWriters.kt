package scientifik.kmath.asm.internal

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor

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
