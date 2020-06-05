package scientifik.kmath.expressions

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

fun MethodVisitor.visitLdcOrIConstInsn(value: Int) {
    when (value) {
        -1 -> visitInsn(Opcodes.ICONST_M1)
        0 -> visitInsn(Opcodes.ICONST_0)
        1 -> visitInsn(Opcodes.ICONST_1)
        2 -> visitInsn(Opcodes.ICONST_2)
        3 -> visitInsn(Opcodes.ICONST_3)
        4 -> visitInsn(Opcodes.ICONST_4)
        5 -> visitInsn(Opcodes.ICONST_5)
        else -> visitLdcInsn(value)
    }
}
