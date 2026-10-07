/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package space.kscience.kmath.asm.internal

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import org.objectweb.asm.commons.InstructionAdapter
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.ast.TypedMst
import space.kscience.kmath.ast.positionalAdapter
import space.kscience.kmath.ast.positionalArguments
import space.kscience.kmath.ast.resolveFunction
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Float64
import java.lang.invoke.MethodType
import java.nio.file.Paths
import kotlin.io.path.writeBytes

@UnstableKMathAPI
internal sealed class PrimitiveAsmBuilder<T : Number, out E : Expression<T>>(
    protected val algebra: NumericAlgebra<T>,
    classOfT: Class<*>,
    protected val classOfTPrimitive: Class<*>,
    expressionParent: Class<E>,
    protected val target: TypedMst<T>,
    private val functions: Map<String, Expression<T>>,
) : AsmBuilder() {
    private val className: String = buildName("${target.hashCode()}_${classOfT.simpleName}")

    /**
     * ASM type for [tType].
     */
    private val tType: Type = classOfT.asm

    /**
     * ASM type for [classOfTPrimitive].
     */
    protected val tTypePrimitive: Type = classOfTPrimitive.asm

    /**
     * ASM type for array of [classOfTPrimitive].
     */
    protected val tTypePrimitiveArray: Type = getType("[" + classOfTPrimitive.asm.descriptor)

    /**
     * ASM type for expression parent.
     */
    private val expressionParentType = expressionParent.asm

    /**
     * ASM type for new class.
     */
    private val classType: Type = getObjectType(className.replace(oldChar = '.', newChar = '/'))

    /**
     * Method visitor of `invoke` method of the subclass.
     */
    protected lateinit var invokeMethodVisitor: InstructionAdapter

    /**
     * Indexer for arguments in [target].
     */
    private val argumentsIndexer = mutableListOf<Symbol>()

    /**
     * Function call adapters to provide to the subclass.
     */
    private val constants = mutableListOf<Any>()

    private val functionCallIndices = hashMapOf<TypedMst.FunctionCall<T>, Int>()

    /**
     * Subclasses, loads and instantiates [Expression] for given parameters.
     *
     * The built instance is cached.
     */
    @Suppress("UNCHECKED_CAST")
    val instance: E by lazy {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            visit(
                V11,
                ACC_PUBLIC or ACC_FINAL or ACC_SUPER,
                classType.internalName,
                "${OBJECT_TYPE.descriptor}${expressionParentType.descriptor}",
                OBJECT_TYPE.internalName,
                arrayOf(expressionParentType.internalName),
            )

            visitField(
                access = ACC_PRIVATE or ACC_FINAL,
                name = "indexer",
                descriptor = SYMBOL_INDEXER_TYPE.descriptor,
                signature = null,
                value = null,
                block = FieldVisitor::visitEnd,
            )

            visitField(
                access = ACC_PRIVATE or ACC_FINAL,
                name = "constants",
                descriptor = OBJECT_ARRAY_TYPE.descriptor,
                signature = null,
                value = null,
                block = FieldVisitor::visitEnd,
            )

            visitMethod(
                ACC_PUBLIC,
                "getIndexer",
                getMethodDescriptor(SYMBOL_INDEXER_TYPE),
                null,
                null,
            ).instructionAdapter {
                visitCode()
                val start = label()
                load(0, classType)
                getfield(classType.internalName, "indexer", SYMBOL_INDEXER_TYPE.descriptor)
                areturn(SYMBOL_INDEXER_TYPE)
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
                ACC_PUBLIC,
                "invoke",
                getMethodDescriptor(tTypePrimitive, tTypePrimitiveArray),
                null,
                null,
            ).instructionAdapter {
                invokeMethodVisitor = this
                visitCode()
                val start = label()
                visitVariables(target, arrayMode = true)
                visitExpression(target)
                areturn(tTypePrimitive)
                val end = label()

                visitLocalVariable(
                    "this",
                    classType.descriptor,
                    null,
                    start,
                    end,
                    0,
                )

                visitLocalVariable(
                    "arguments",
                    tTypePrimitiveArray.descriptor,
                    null,
                    start,
                    end,
                    1,
                )

                visitMaxs(0, 0)
                visitEnd()
            }

            visitMethod(
                ACC_PUBLIC or ACC_FINAL,
                "invoke",
                getMethodDescriptor(tType, MAP_TYPE),
                "(L${MAP_TYPE.internalName}<${SYMBOL_TYPE.descriptor}+${tType.descriptor}>;)${tType.descriptor}",
                null,
            ).instructionAdapter {
                invokeMethodVisitor = this
                visitCode()
                val start = label()
                visitVariables(target, arrayMode = false)
                visitExpression(target)
                box()
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

                visitLocalVariable(
                    "arguments",
                    MAP_TYPE.descriptor,
                    "L${MAP_TYPE.internalName}<${SYMBOL_TYPE.descriptor}+${tType.descriptor}>;",
                    start,
                    end,
                    1,
                )

                visitMaxs(0, 0)
                visitEnd()
            }

            visitMethod(
                ACC_PUBLIC or ACC_FINAL or ACC_BRIDGE or ACC_SYNTHETIC,
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
                ACC_PUBLIC or ACC_SYNTHETIC,
                "<init>",
                getMethodDescriptor(VOID_TYPE, SYMBOL_INDEXER_TYPE, OBJECT_ARRAY_TYPE),
                null,
                null,
            ).instructionAdapter {
                val start = label()
                load(0, classType)
                invokespecial(OBJECT_TYPE.internalName, "<init>", getMethodDescriptor(VOID_TYPE), false)
                load(0, classType)
                load(1, SYMBOL_INDEXER_TYPE)
                putfield(classType.internalName, "indexer", SYMBOL_INDEXER_TYPE.descriptor)
                load(0, classType)
                load(2, OBJECT_ARRAY_TYPE)
                putfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
                areturn(VOID_TYPE)
                val end = label()
                visitLocalVariable("this", classType.descriptor, null, start, end, 0)
                visitLocalVariable("indexer", SYMBOL_INDEXER_TYPE.descriptor, null, start, end, 1)
                visitLocalVariable("constants", OBJECT_ARRAY_TYPE.descriptor, null, start, end, 2)
                visitMaxs(0, 0)
                visitEnd()
            }

            visitEnd()
        }

        val binary = classWriter.toByteArray()

        if (System.getProperty("space.kscience.kmath.ast.dump.generated.classes") == "1")
            Paths.get("${className.split('.').last()}.class").writeBytes(binary)

        val lookup = defineHiddenClass(binary)

        lookup
            .findConstructor(
                lookup.lookupClass(),
                MethodType.methodType(Void.TYPE, SymbolIndexer::class.java, Array<Any>::class.java),
            )
            .invoke(SimpleSymbolIndexer(argumentsIndexer), constants.toTypedArray()) as E
    }

    /**
     * Loads a numeric constant [value] from the class's constants.
     */
    protected fun loadNumberConstant(value: Number) {
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
     * Stores value variable [name] into a local. Should be called before using [loadVariable]. Should be called only
     * once for a variable.
     */
    protected fun prepareVariable(name: Symbol, arrayMode: Boolean): Unit = invokeMethodVisitor.run {
        var argumentIndex = argumentsIndexer.indexOf(name)

        if (argumentIndex == -1) {
            argumentsIndexer += name
            argumentIndex = argumentsIndexer.lastIndex
        }

        val localIndex = 2 + argumentIndex * tTypePrimitive.size

        if (arrayMode) {
            load(1, tTypePrimitiveArray)
            iconst(argumentIndex)
            aload(tTypePrimitive)
            store(localIndex, tTypePrimitive)
        } else {
            load(1, MAP_TYPE)
            aconst(name.identity)

            invokestatic(
                MAP_INTRINSICS_TYPE.internalName,
                "getOrFail",
                getMethodDescriptor(OBJECT_TYPE, MAP_TYPE, STRING_TYPE),
                false,
            )

            checkcast(tType)
            unbox()
            store(localIndex, tTypePrimitive)
        }
    }

    /**
     * Loads a variable [name] from arguments [Map] parameter of [Expression.invoke]. The variable should be stored
     * with [prepareVariable] first.
     */
    protected fun loadVariable(name: Symbol) {
        val argumentIndex = argumentsIndexer.indexOf(name)
        val localIndex = 2 + argumentIndex * tTypePrimitive.size
        invokeMethodVisitor.load(localIndex, tTypePrimitive)
    }

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

    private fun visitVariables(
        node: TypedMst<T>,
        arrayMode: Boolean,
        alreadyLoaded: MutableList<Symbol> = mutableListOf(),
    ): Unit = when (node) {
        is TypedMst.Variable -> if (node.symbol !in alreadyLoaded) {
            alreadyLoaded += node.symbol
            prepareVariable(node.symbol, arrayMode)
        } else Unit

        is TypedMst.Unary -> visitVariables(node.value, arrayMode, alreadyLoaded)

        is TypedMst.Binary -> {
            visitVariables(node.left, arrayMode, alreadyLoaded)
            visitVariables(node.right, arrayMode, alreadyLoaded)
        }

        is TypedMst.Constant -> Unit

        is TypedMst.FunctionCall -> node.arguments.values.forEach { visitVariables(it, arrayMode, alreadyLoaded) }
    }

    private fun visitExpression(node: TypedMst<T>): Unit = when (node) {
        is TypedMst.Variable -> loadVariable(node.symbol)

        is TypedMst.Constant -> loadNumberConstant(
            node.number ?: error("Object constants are not supported by pritimive ASM builder"),
        )

        is TypedMst.Unary -> visitUnary(node)
        is TypedMst.Binary -> visitBinary(node)
        is TypedMst.FunctionCall -> visitFunctionCall(node)
    }

    private fun visitFunctionCall(node: TypedMst.FunctionCall<T>): Unit = invokeMethodVisitor.run {
        val function = resolveFunction(functions, node)
        val arguments = positionalArguments(function, node)
        val expressionType = arrayExpressionTypes(function)?.takeIf { it.second == classOfTPrimitive }?.first

        // Both invoke methods visit the same tree, so constants are shared between them.
        val index = functionCallIndices.getOrPut(node) {
            constants += if (expressionType != null) function else positionalAdapter(function, node)
            constants.lastIndex
        }

        load(0, classType)
        getfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
        iconst(index)
        aload(OBJECT_TYPE)

        if (expressionType != null) {
            checkcast(expressionType)
            iconst(arguments.size)
            newarray(tTypePrimitive)

            for ((index, argument) in arguments.withIndex()) {
                dup()
                iconst(index)
                visitExpression(argument)
                astore(tTypePrimitive)
            }

            invokeinterface(expressionType.internalName, "invoke", getMethodDescriptor(tTypePrimitive, tTypePrimitiveArray))
        } else {
            checkcast(FUNCTION1_TYPE)
            iconst(arguments.size)
            newarray(OBJECT_TYPE)

            for ((index, argument) in arguments.withIndex()) {
                dup()
                iconst(index)
                visitExpression(argument)
                box()
                astore(OBJECT_TYPE)
            }

            invokeinterface(FUNCTION1_TYPE.internalName, "invoke", getMethodDescriptor(OBJECT_TYPE, OBJECT_TYPE))
            checkcast(tType)
            unbox()
        }
    }

    protected open fun visitUnary(node: TypedMst.Unary<T>) = visitExpression(node.value)

    protected open fun visitBinary(node: TypedMst.Binary<T>) {
        visitExpression(node.left)
        visitExpression(node.right)
    }

    protected companion object {
        /**
         * ASM type for [SymbolIndexer].
         */
        val SYMBOL_INDEXER_TYPE: Type = getType(SymbolIndexer::class.java)
    }
}

@UnstableKMathAPI
internal class DoubleAsmBuilder(target: TypedMst<Float64>, functions: Map<String, Expression<Float64>>) : PrimitiveAsmBuilder<Double, DoubleExpression>(
    Float64Field,
    java.lang.Double::class.java,
    java.lang.Double.TYPE,
    DoubleExpression::class.java,
    target,
    functions,
) {
    private fun buildUnaryJavaMathCall(name: String) = invokeMethodVisitor.invokestatic(
        MATH_TYPE.internalName,
        name,
        getMethodDescriptor(tTypePrimitive, tTypePrimitive),
        false,
    )

    @Suppress("SameParameterValue")
    private fun buildBinaryJavaMathCall(name: String) = invokeMethodVisitor.invokestatic(
        MATH_TYPE.internalName,
        name,
        getMethodDescriptor(tTypePrimitive, tTypePrimitive, tTypePrimitive),
        false,
    )

    private fun buildUnaryKotlinMathCall(name: String) = invokeMethodVisitor.invokestatic(
        MATH_KT_TYPE.internalName,
        name,
        getMethodDescriptor(tTypePrimitive, tTypePrimitive),
        false,
    )

    override fun visitUnary(node: TypedMst.Unary<Float64>) {
        super.visitUnary(node)

        when (node.operation) {
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(DNEG)
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
            else -> super.visitUnary(node)
        }
    }

    override fun visitBinary(node: TypedMst.Binary<Float64>) {
        super.visitBinary(node)

        when (node.operation) {
            GroupOps.PLUS_OPERATION -> invokeMethodVisitor.visitInsn(DADD)
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(DSUB)
            RingOps.TIMES_OPERATION -> invokeMethodVisitor.visitInsn(DMUL)
            FieldOps.DIV_OPERATION -> invokeMethodVisitor.visitInsn(DDIV)
            PowerOperations.POW_OPERATION -> buildBinaryJavaMathCall("pow")
            else -> super.visitBinary(node)
        }
    }

    private companion object {
        val MATH_TYPE: Type = getObjectType("java/lang/Math")
        val MATH_KT_TYPE: Type = getObjectType("kotlin/math/MathKt")
    }
}

@UnstableKMathAPI
internal class IntAsmBuilder(target: TypedMst<Int>, functions: Map<String, Expression<Int>>) :
    PrimitiveAsmBuilder<Int, IntExpression>(
        Int32Ring,
        Integer::class.java,
        Integer.TYPE,
        IntExpression::class.java,
        target,
        functions,
    ) {
    override fun visitUnary(node: TypedMst.Unary<Int>) {
        super.visitUnary(node)

        when (node.operation) {
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(INEG)
            GroupOps.PLUS_OPERATION -> Unit
            else -> super.visitUnary(node)
        }
    }

    override fun visitBinary(node: TypedMst.Binary<Int>) {
        super.visitBinary(node)

        when (node.operation) {
            GroupOps.PLUS_OPERATION -> invokeMethodVisitor.visitInsn(IADD)
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(ISUB)
            RingOps.TIMES_OPERATION -> invokeMethodVisitor.visitInsn(IMUL)
            else -> super.visitBinary(node)
        }
    }
}

@UnstableKMathAPI
internal class LongAsmBuilder(target: TypedMst<Long>, functions: Map<String, Expression<Long>>) : PrimitiveAsmBuilder<Long, LongExpression>(
    Int64Ring,
    java.lang.Long::class.java,
    java.lang.Long.TYPE,
    LongExpression::class.java,
    target,
    functions,
) {
    override fun visitUnary(node: TypedMst.Unary<Long>) {
        super.visitUnary(node)

        when (node.operation) {
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(LNEG)
            GroupOps.PLUS_OPERATION -> Unit
            else -> super.visitUnary(node)
        }
    }

    override fun visitBinary(node: TypedMst.Binary<Long>) {
        super.visitBinary(node)

        when (node.operation) {
            GroupOps.PLUS_OPERATION -> invokeMethodVisitor.visitInsn(LADD)
            GroupOps.MINUS_OPERATION -> invokeMethodVisitor.visitInsn(LSUB)
            RingOps.TIMES_OPERATION -> invokeMethodVisitor.visitInsn(LMUL)
            else -> super.visitBinary(node)
        }
    }
}
