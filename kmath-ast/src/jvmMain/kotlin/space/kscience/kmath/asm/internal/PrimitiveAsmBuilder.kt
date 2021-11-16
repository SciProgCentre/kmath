/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm.internal

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import org.objectweb.asm.commons.InstructionAdapter
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.*
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.file.Paths
import kotlin.io.path.writeBytes

internal sealed class PrimitiveAsmBuilder<T : Number>(
    protected val algebra: Algebra<T>,
    classOfT: Class<*>,
    protected val classOfTPrimitive: Class<*>,
    protected val target: MST,
) : AsmBuilder() {
    private val className: String = buildName(target)

    /**
     * ASM type for [T].
     */
    private val tType: Type = classOfT.asm

    /**
     * ASM type for [T].
     */
    protected val tTypePrimitive: Type = classOfTPrimitive.asm

    /**
     * ASM type for new class.
     */
    private val classType: Type = getObjectType(className.replace(oldChar = '.', newChar = '/'))

    /**
     * Method visitor of `invoke` method of the subclass.
     */
    protected lateinit var invokeMethodVisitor: InstructionAdapter

    /**
     * Local variables indices are indices of symbols in this list.
     */
    private val argumentsLocals = mutableListOf<String>()

    /**
     * Subclasses, loads and instantiates [Expression] for given parameters.
     *
     * The built instance is cached.
     */
    @Suppress("UNCHECKED_CAST", "UNUSED_VARIABLE")
    val instance: Expression<T> by lazy {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
                classType.internalName,
                "${OBJECT_TYPE.descriptor}L${EXPRESSION_TYPE.internalName}<${tType.descriptor}>;",
                OBJECT_TYPE.internalName,
                arrayOf(EXPRESSION_TYPE.internalName),
            )

            visitMethod(
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
                "invoke",
                getMethodDescriptor(tType, MAP_TYPE),
                "(L${MAP_TYPE.internalName}<${SYMBOL_TYPE.descriptor}+${tType.descriptor}>;)${tType.descriptor}",
                null,
            ).instructionAdapter {
                invokeMethodVisitor = this
                visitCode()
                val preparingVariables = label()
                visitVariables(target)
                val expressionResult = label()
                visitExpression(target)
                box()
                areturn(tType)
                val end = label()

                visitLocalVariable(
                    "this",
                    classType.descriptor,
                    null,
                    preparingVariables,
                    end,
                    0,
                )

                visitLocalVariable(
                    "arguments",
                    MAP_TYPE.descriptor,
                    "L${MAP_TYPE.internalName}<${SYMBOL_TYPE.descriptor}+${tType.descriptor}>;",
                    preparingVariables,
                    end,
                    1,
                )

                visitMaxs(0, 0)
                visitEnd()
            }

            visitMethod(
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_BRIDGE or Opcodes.ACC_SYNTHETIC,
                "invoke",
                getMethodDescriptor(OBJECT_TYPE, MAP_TYPE),
                null,
                null,
            ).instructionAdapter {
                visitCode()
                val start = label()
                load(0, OBJECT_TYPE)
                load(1, MAP_TYPE)
                invokevirtual(classType.internalName, "invoke", getMethodDescriptor(tType, MAP_TYPE), false)
                areturn(tType)
                val end = label()

                visitLocalVariable(
                    "this",
                    classType.descriptor,
                    null,
                    start,
                    end,
                    0,
                )

                visitMaxs(0, 0)
                visitEnd()
            }

            visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                getMethodDescriptor(VOID_TYPE),
                null,
                null,
            ).instructionAdapter {
                val start = label()
                load(0, classType)
                invokespecial(OBJECT_TYPE.internalName, "<init>", getMethodDescriptor(VOID_TYPE), false)
                label()
                load(0, classType)
                label()
                visitInsn(Opcodes.RETURN)
                val end = label()
                visitLocalVariable("this", classType.descriptor, null, start, end, 0)
                visitMaxs(0, 0)
                visitEnd()
            }

            visitEnd()
        }

        val binary = classWriter.toByteArray()
        val cls = classLoader.defineClass(className, binary)

        if (System.getProperty("space.kscience.kmath.ast.dump.generated.classes") == "1")
            Paths.get("${className.split('.').last()}.class").writeBytes(binary)

        MethodHandles.publicLookup().findConstructor(cls, MethodType.methodType(Void.TYPE))() as Expression<T>
    }

    /**
     * Either loads a numeric constant [value] from the class's constants field or boxes a primitive
     * constant from the constant pool.
     */
    fun loadNumberConstant(value: Number) {
        when (tTypePrimitive) {
            BYTE_TYPE -> invokeMethodVisitor.iconst(value.toInt())
            DOUBLE_TYPE -> invokeMethodVisitor.dconst(value.toDouble())
            FLOAT_TYPE -> invokeMethodVisitor.fconst(value.toFloat())
            LONG_TYPE -> invokeMethodVisitor.lconst(value.toLong())
            INT_TYPE -> invokeMethodVisitor.iconst(value.toInt())
            SHORT_TYPE -> invokeMethodVisitor.iconst(value.toInt())
        }
    }

    /**
     * Stores value variable [name] into a local. Should be called within [variablesPrepareCallback] before using
     * [loadVariable].
     */
    fun prepareVariable(name: String): Unit = invokeMethodVisitor.run {
        if (name in argumentsLocals) return@run
        load(1, MAP_TYPE)
        aconst(name)

        invokestatic(
            MAP_INTRINSICS_TYPE.internalName,
            "getOrFail",
            getMethodDescriptor(OBJECT_TYPE, MAP_TYPE, STRING_TYPE),
            false,
        )

        checkcast(tType)
        var idx = argumentsLocals.indexOf(name)

        if (idx == -1) {
            argumentsLocals += name
            idx = argumentsLocals.lastIndex
        }

        unbox()
        store(2 + idx, tTypePrimitive)
    }

    /**
     * Loads a variable [name] from arguments [Map] parameter of [Expression.invoke]. The variable should be stored
     * with [prepareVariable] first.
     */
    fun loadVariable(name: String): Unit = invokeMethodVisitor.load(2 + argumentsLocals.indexOf(name), tTypePrimitive)

    private fun unbox() = invokeMethodVisitor.run {
        invokevirtual(
            NUMBER_TYPE.internalName,
            "${classOfTPrimitive.simpleName}Value",
            getMethodDescriptor(tTypePrimitive),
            false
        )
    }

    private fun box() = invokeMethodVisitor.run {
        invokestatic(tType.internalName, "valueOf", getMethodDescriptor(tType, tTypePrimitive), false)
    }

    protected fun visitVariables(node: MST): Unit = when (node) {
        is Symbol -> prepareVariable(node.identity)
        is MST.Unary -> visitVariables(node.value)

        is MST.Binary -> {
            visitVariables(node.left)
            visitVariables(node.right)
        }

        else -> Unit
    }

    protected fun visitExpression(mst: MST): Unit = when (mst) {
        is Symbol -> loadVariable(mst.identity)
        is MST.Numeric -> loadNumberConstant(mst.value)

        is MST.Unary -> when {
            algebra is NumericAlgebra && mst.value is MST.Numeric -> {
                loadNumberConstant(
                    MST.Numeric(
                        algebra.unaryOperationFunction(mst.operation)(algebra.number((mst.value as MST.Numeric).value)),
                    ).value,
                )
            }

            else -> visitUnary(mst)
        }

        is MST.Binary -> when {
            algebra is NumericAlgebra && mst.left is MST.Numeric && mst.right is MST.Numeric -> {
                loadNumberConstant(
                    MST.Numeric(
                        algebra.binaryOperationFunction(mst.operation)(
                            algebra.number((mst.left as MST.Numeric).value),
                            algebra.number((mst.right as MST.Numeric).value),
                        ),
                    ).value,
                )
            }

            else -> visitBinary(mst)
        }
    }

    protected open fun visitUnary(mst: MST.Unary) {
        visitExpression(mst.value)
    }

    protected open fun visitBinary(mst: MST.Binary) {
        visitExpression(mst.left)
        visitExpression(mst.right)
    }

    protected companion object {
        /**
         * ASM type for [java.lang.Number].
         */
        val NUMBER_TYPE: Type by lazy { getObjectType("java/lang/Number") }
    }
}

internal class DoubleAsmBuilder(target: MST) :
    PrimitiveAsmBuilder<Double>(DoubleField, java.lang.Double::class.java, java.lang.Double.TYPE, target) {

    private fun buildUnaryJavaMathCall(name: String) {
        invokeMethodVisitor.invokestatic(
            MATH_TYPE.internalName,
            name,
            getMethodDescriptor(tTypePrimitive, tTypePrimitive),
            false,
        )
    }

    private fun buildBinaryJavaMathCall(name: String) {
        invokeMethodVisitor.invokestatic(
            MATH_TYPE.internalName,
            name,
            getMethodDescriptor(tTypePrimitive, tTypePrimitive, tTypePrimitive),
            false,
        )
    }

    private fun buildUnaryKotlinMathCall(name: String) {
        invokeMethodVisitor.invokestatic(
            MATH_KT_TYPE.internalName,
            name,
            getMethodDescriptor(tTypePrimitive, tTypePrimitive),
            false,
        )
    }

    override fun visitUnary(mst: MST.Unary) {
        super.visitUnary(mst)

        when (mst.operation) {
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.DNEG)
            GroupOps.PLUS_OPERATION -> Unit
            PowerOperations.SQRT_OPERATION -> buildUnaryJavaMathCall("sqrt")
            TrigonometricOperations.SIN_OPERATION -> buildUnaryJavaMathCall("sin")
            TrigonometricOperations.COS_OPERATION -> buildUnaryJavaMathCall("cos")
            TrigonometricOperations.TAN_OPERATION -> buildUnaryJavaMathCall("tan")
            TrigonometricOperations.ASIN_OPERATION -> buildUnaryJavaMathCall("asin")
            TrigonometricOperations.ACOS_OPERATION -> buildUnaryJavaMathCall("acos")
            TrigonometricOperations.ATAN_OPERATION -> buildUnaryJavaMathCall("atan")
            ExponentialOperations.SINH_OPERATION -> buildUnaryJavaMathCall("sqrt")
            ExponentialOperations.COSH_OPERATION -> buildUnaryJavaMathCall("cosh")
            ExponentialOperations.TANH_OPERATION -> buildUnaryJavaMathCall("tanh")
            ExponentialOperations.ASINH_OPERATION -> buildUnaryKotlinMathCall("asinh")
            ExponentialOperations.ACOSH_OPERATION -> buildUnaryKotlinMathCall("acosh")
            ExponentialOperations.ATANH_OPERATION -> buildUnaryKotlinMathCall("atanh")
            ExponentialOperations.EXP_OPERATION -> buildUnaryJavaMathCall("exp")
            ExponentialOperations.LN_OPERATION -> buildUnaryJavaMathCall("log")
            else -> super.visitUnary(mst)
        }
    }

    override fun visitBinary(mst: MST.Binary) {
        super.visitBinary(mst)

        when (mst.operation) {
            GroupOps.PLUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.DADD)
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.DSUB)
            RingOps.TIMES_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.DMUL)
            FieldOps.DIV_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.DDIV)
            PowerOperations.POW_OPERATION -> buildBinaryJavaMathCall("pow")
            else -> super.visitBinary(mst)
        }
    }

    companion object {
        val MATH_TYPE: Type by lazy { getObjectType("java/lang/Math") }
        val MATH_KT_TYPE: Type by lazy { getObjectType("kotlin/math/MathKt") }
    }
}

internal class IntAsmBuilder(target: MST) :
    PrimitiveAsmBuilder<Int>(IntRing, Integer::class.java, Integer.TYPE, target) {
    override fun visitUnary(mst: MST.Unary) {
        super.visitUnary(mst)

        when (mst.operation) {
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.INEG)
            GroupOps.PLUS_OPERATION -> Unit
            else -> super.visitUnary(mst)
        }
    }

    override fun visitBinary(mst: MST.Binary) {
        super.visitBinary(mst)

        when (mst.operation) {
            GroupOps.PLUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.IADD)
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.ISUB)
            RingOps.TIMES_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.IMUL)
            else -> super.visitBinary(mst)
        }
    }
}

internal class LongAsmBuilder(target: MST) :
    PrimitiveAsmBuilder<Long>(LongRing, java.lang.Long::class.java, java.lang.Long.TYPE, target) {
    override fun visitUnary(mst: MST.Unary) {
        super.visitUnary(mst)

        when (mst.operation) {
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.LNEG)
            GroupOps.PLUS_OPERATION -> Unit
            else -> super.visitUnary(mst)
        }
    }

    override fun visitBinary(mst: MST.Binary) {
        super.visitBinary(mst)

        when (mst.operation) {
            GroupOps.PLUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.LADD)
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.LSUB)
            RingOps.TIMES_OPERATION -> invokeMethodVisitor.visitInsn(Opcodes.LMUL)
            else -> super.visitBinary(mst)
        }
    }
}

