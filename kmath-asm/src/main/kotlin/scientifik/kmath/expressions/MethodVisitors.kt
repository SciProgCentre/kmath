package scientifik.kmath.expressions

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

fun MethodVisitor.visitLdcOrIConstInsn(value: Int) = when (value) {
    -1 -> visitInsn(ICONST_M1)
    0 -> visitInsn(ICONST_0)
    1 -> visitInsn(ICONST_1)
    2 -> visitInsn(ICONST_2)
    3 -> visitInsn(ICONST_3)
    4 -> visitInsn(ICONST_4)
    5 -> visitInsn(ICONST_5)
    else -> visitLdcInsn(value)
}

fun MethodVisitor.visitLdcOrDConstInsn(value: Double) = when (value) {
    0.0 -> visitInsn(DCONST_0)
    1.0 -> visitInsn(DCONST_1)
    else -> visitLdcInsn(value)
}

fun MethodVisitor.visitLdcOrFConstInsn(value: Float) = when (value) {
    0f -> visitInsn(FCONST_0)
    1f -> visitInsn(FCONST_1)
    2f -> visitInsn(FCONST_2)
    else -> visitLdcInsn(value)
}

private val signatureLetters = mapOf(
    java.lang.Byte::class.java to "B",
    java.lang.Short::class.java to "S",
    java.lang.Integer::class.java to "I",
    java.lang.Long::class.java to "J",
    java.lang.Float::class.java to "F",
    java.lang.Double::class.java to "D",
    java.lang.Short::class.java to "S"
)

fun MethodVisitor.visitBoxedNumberConstant(number: Number) {
    val clazz = number.javaClass
    val c = clazz.name.replace('.', '/')

    when (number) {
        is Int -> visitLdcOrIConstInsn(number)
        is Double -> visitLdcOrDConstInsn(number)
        is Float -> visitLdcOrFConstInsn(number)
        else -> visitLdcInsn(number)
    }

    visitMethodInsn(INVOKESTATIC, c, "valueOf", "(${signatureLetters[clazz]})L${c};", false)
}
