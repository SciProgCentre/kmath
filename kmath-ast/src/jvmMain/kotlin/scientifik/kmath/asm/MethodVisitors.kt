package scientifik.kmath.asm

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

internal fun MethodVisitor.visitLdcOrIConstInsn(value: Int) = when (value) {
    -1 -> visitInsn(ICONST_M1)
    0 -> visitInsn(ICONST_0)
    1 -> visitInsn(ICONST_1)
    2 -> visitInsn(ICONST_2)
    3 -> visitInsn(ICONST_3)
    4 -> visitInsn(ICONST_4)
    5 -> visitInsn(ICONST_5)
    else -> visitLdcInsn(value)
}

internal fun MethodVisitor.visitLdcOrDConstInsn(value: Double) = when (value) {
    0.0 -> visitInsn(DCONST_0)
    1.0 -> visitInsn(DCONST_1)
    else -> visitLdcInsn(value)
}

internal fun MethodVisitor.visitLdcOrFConstInsn(value: Float) = when (value) {
    0f -> visitInsn(FCONST_0)
    1f -> visitInsn(FCONST_1)
    2f -> visitInsn(FCONST_2)
    else -> visitLdcInsn(value)
}
