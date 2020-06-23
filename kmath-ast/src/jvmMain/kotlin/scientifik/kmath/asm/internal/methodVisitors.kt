package scientifik.kmath.asm.internal

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

internal fun MethodVisitor.visitLdcOrIntConstant(value: Int): Unit = when (value) {
    -1 -> visitInsn(ICONST_M1)
    0 -> visitInsn(ICONST_0)
    1 -> visitInsn(ICONST_1)
    2 -> visitInsn(ICONST_2)
    3 -> visitInsn(ICONST_3)
    4 -> visitInsn(ICONST_4)
    5 -> visitInsn(ICONST_5)
    in -128..127 -> visitIntInsn(BIPUSH, value)
    in -32768..32767 -> visitIntInsn(SIPUSH, value)
    else -> visitLdcInsn(value)
}

internal fun MethodVisitor.visitLdcOrDoubleConstant(value: Double): Unit = when (value) {
    0.0 -> visitInsn(DCONST_0)
    1.0 -> visitInsn(DCONST_1)
    else -> visitLdcInsn(value)
}

internal fun MethodVisitor.visitLdcOrLongConstant(value: Long): Unit = when (value) {
    0L -> visitInsn(LCONST_0)
    1L -> visitInsn(LCONST_1)
    else -> visitLdcInsn(value)
}

internal fun MethodVisitor.visitLdcOrFloatConstant(value: Float): Unit = when (value) {
    0f -> visitInsn(FCONST_0)
    1f -> visitInsn(FCONST_1)
    2f -> visitInsn(FCONST_2)
    else -> visitLdcInsn(value)
}

internal fun MethodVisitor.visitInvokeInterface(owner: String, name: String, descriptor: String): Unit =
    visitMethodInsn(INVOKEINTERFACE, owner, name, descriptor, true)

internal fun MethodVisitor.visitInvokeVirtual(owner: String, name: String, descriptor: String): Unit =
    visitMethodInsn(INVOKEVIRTUAL, owner, name, descriptor, false)

internal fun MethodVisitor.visitInvokeStatic(owner: String, name: String, descriptor: String): Unit =
    visitMethodInsn(INVOKESTATIC, owner, name, descriptor, false)

internal fun MethodVisitor.visitInvokeSpecial(owner: String, name: String, descriptor: String): Unit =
    visitMethodInsn(INVOKESPECIAL, owner, name, descriptor, false)

internal fun MethodVisitor.visitCheckCast(type: String): Unit = visitTypeInsn(CHECKCAST, type)

internal fun MethodVisitor.visitGetField(owner: String, name: String, descriptor: String): Unit =
    visitFieldInsn(GETFIELD, owner, name, descriptor)

internal fun MethodVisitor.visitLoadObjectVar(`var`: Int): Unit = visitVarInsn(ALOAD, `var`)

internal fun MethodVisitor.visitGetObjectArrayElement(): Unit = visitInsn(AALOAD)

internal fun MethodVisitor.visitReturn(): Unit = visitInsn(RETURN)
internal fun MethodVisitor.visitReturnObject(): Unit = visitInsn(ARETURN)
