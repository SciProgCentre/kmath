package scientifik.kmath.asm.internal

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor

inline fun ClassWriter(flags: Int, block: ClassWriter.() -> Unit): ClassWriter = ClassWriter(flags).apply(block)

inline fun ClassWriter.visitMethod(
    access: Int,
    name: String,
    descriptor: String,
    signature: String?,
    exceptions: Array<String>?,
    block: MethodVisitor.() -> Unit
): MethodVisitor = visitMethod(access, name, descriptor, signature, exceptions).apply(block)
