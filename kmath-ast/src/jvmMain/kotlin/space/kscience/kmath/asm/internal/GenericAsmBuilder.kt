/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm.internal

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type.*
import org.objectweb.asm.commons.InstructionAdapter
import space.kscience.kmath.expressions.*
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.file.Paths
import java.util.stream.Collectors.toMap
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.path.writeBytes

/**
 * ASM Builder is a structure that abstracts building a class designated to unwrap [MST] to plain Java expression.
 * This class uses [ClassLoader] for loading the generated class, then it is able to instantiate the new class.
 *
 * @property T the type of AsmExpression to unwrap.
 * @property className the unique class name of new loaded class.
 * @property expressionResultCallback the function to apply to this object when generating expression value.
 * @author Iaroslav Postovalov
 */
internal class GenericAsmBuilder<T>(
    classOfT: Class<*>,
    private val className: String,
    private val variablesPrepareCallback: GenericAsmBuilder<T>.() -> Unit,
    private val expressionResultCallback: GenericAsmBuilder<T>.() -> Unit,
) : AsmBuilder() {
    /**
     * ASM type for [T].
     */
    private val tType: Type = classOfT.asm

    /**
     * ASM type for new class.
     */
    private val classType: Type = getObjectType(className.replace(oldChar = '.', newChar = '/'))

    /**
     * List of constants to provide to the subclass.
     */
    private val constants = mutableListOf<Any>()

    /**
     * Method visitor of `invoke` method of the subclass.
     */
    private lateinit var invokeMethodVisitor: InstructionAdapter

    /**
     * Local variables indices are indices of symbols in this list.
     */
    private val argumentsLocals = mutableListOf<Symbol>()

    /**
     * Subclasses, loads and instantiates [Expression] for given parameters.
     *
     * The built instance is cached.
     */
    @Suppress("UNCHECKED_CAST", "UNUSED_VARIABLE")
    val instance: Expression<T> by lazy {
        val hasConstants: Boolean

        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            visit(
                V1_8,
                ACC_PUBLIC or ACC_FINAL or ACC_SUPER,
                classType.internalName,
                "${OBJECT_TYPE.descriptor}L${EXPRESSION_TYPE.internalName}<${tType.descriptor}>;",
                OBJECT_TYPE.internalName,
                arrayOf(EXPRESSION_TYPE.internalName),
            )

            visitMethod(
                ACC_PUBLIC,
                "invoke",
                getMethodDescriptor(tType, MAP_TYPE),
                "(L${MAP_TYPE.internalName}<${SYMBOL_TYPE.descriptor}+${tType.descriptor}>;)${tType.descriptor}",
                null,
            ).instructionAdapter {
                invokeMethodVisitor = this
                visitCode()
                val preparingVariables = label()
                variablesPrepareCallback()
                val expressionResult = label()
                expressionResultCallback()
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
                ACC_PUBLIC or ACC_BRIDGE or ACC_SYNTHETIC,
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

            hasConstants = constants.isNotEmpty()

            if (hasConstants)
                visitField(
                    access = ACC_PRIVATE or ACC_FINAL,
                    name = "constants",
                    descriptor = OBJECT_ARRAY_TYPE.descriptor,
                    signature = null,
                    value = null,
                    block = FieldVisitor::visitEnd,
                )

            visitMethod(
                ACC_PUBLIC or ACC_SYNTHETIC,
                "<init>",
                getMethodDescriptor(VOID_TYPE, *OBJECT_ARRAY_TYPE.wrapToArrayIf { hasConstants }),
                null,
                null,
            ).instructionAdapter {
                val l0 = label()
                load(0, classType)
                invokespecial(OBJECT_TYPE.internalName, "<init>", getMethodDescriptor(VOID_TYPE), false)
                label()
                load(0, classType)

                if (hasConstants) {
                    label()
                    load(0, classType)
                    load(1, OBJECT_ARRAY_TYPE)
                    putfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
                }

                label()
                areturn(VOID_TYPE)
                val l4 = label()
                visitLocalVariable("this", classType.descriptor, null, l0, l4, 0)

                if (hasConstants)
                    visitLocalVariable("constants", OBJECT_ARRAY_TYPE.descriptor, null, l0, l4, 1)

                visitMaxs(0, 0)
                visitEnd()
            }

            visitEnd()
        }

        val binary = classWriter.toByteArray()
        val cls = classLoader.defineClass(className, binary)

        if (System.getProperty("space.kscience.kmath.ast.dump.generated.classes") == "1")
            Paths.get("${className.split('.').last()}.class").writeBytes(binary)

        val l = MethodHandles.publicLookup()

        (if (hasConstants)
            l.findConstructor(cls, MethodType.methodType(Void.TYPE, Array<Any>::class.java))(constants.toTypedArray())
        else
            l.findConstructor(cls, MethodType.methodType(Void.TYPE))()) as Expression<T>
    }

    /**
     * Loads [java.lang.Object] constant from constants.
     */
    fun loadObjectConstant(value: Any, type: Type = tType): Unit = invokeMethodVisitor.run {
        val idx = if (value in constants) constants.indexOf(value) else constants.also { it += value }.lastIndex
        load(0, classType)
        getfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
        iconst(idx)
        aload(OBJECT_TYPE)
        if (type != OBJECT_TYPE) checkcast(type)
    }

    /**
     * Either loads a numeric constant [value] from the class's constants field or boxes a primitive
     * constant from the constant pool.
     */
    fun loadNumberConstant(value: Number) {
        val boxed = value.javaClass.asm
        val primitive = BOXED_TO_PRIMITIVES[boxed]

        if (primitive != null) {
            when (primitive) {
                BYTE_TYPE -> invokeMethodVisitor.iconst(value.toInt())
                DOUBLE_TYPE -> invokeMethodVisitor.dconst(value.toDouble())
                FLOAT_TYPE -> invokeMethodVisitor.fconst(value.toFloat())
                LONG_TYPE -> invokeMethodVisitor.lconst(value.toLong())
                INT_TYPE -> invokeMethodVisitor.iconst(value.toInt())
                SHORT_TYPE -> invokeMethodVisitor.iconst(value.toInt())
            }

            val r = boxed

            invokeMethodVisitor.invokestatic(
                r.internalName,
                "valueOf",
                getMethodDescriptor(r, primitive),
                false,
            )

            return
        }

        loadObjectConstant(value, boxed)
    }

    /**
     * Stores value variable [name] into a local. Should be called within [variablesPrepareCallback] before using
     * [loadVariable].
     */
    fun prepareVariable(name: Symbol): Unit = invokeMethodVisitor.run {
        if (name in argumentsLocals) return@run
        load(1, MAP_TYPE)
        aconst(name.identity)

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

        store(2 + idx, tType)
    }

    /**
     * Loads a variable [name] from arguments [Map] parameter of [Expression.invoke]. The variable should be stored
     * with [prepareVariable] first.
     */
    fun loadVariable(name: Symbol): Unit = invokeMethodVisitor.load(2 + argumentsLocals.indexOf(name), tType)

    inline fun buildCall(function: Function<T>, parameters: GenericAsmBuilder<T>.() -> Unit) {
        contract { callsInPlace(parameters, InvocationKind.EXACTLY_ONCE) }
        val `interface` = function.javaClass.interfaces.first { Function::class.java in it.interfaces }

        val arity = `interface`.methods.find { it.name == "invoke" }?.parameterCount
            ?: error("Provided function object doesn't contain invoke method")

        val type = getType(`interface`)
        loadObjectConstant(function, type)
        parameters(this)

        invokeMethodVisitor.invokeinterface(
            type.internalName,
            "invoke",
            getMethodDescriptor(OBJECT_TYPE, *Array(arity) { OBJECT_TYPE }),
        )

        invokeMethodVisitor.checkcast(tType)
    }

    private companion object {
        /**
         * Maps JVM primitive numbers boxed ASM types to their primitive ASM types.
         */
        private val BOXED_TO_PRIMITIVES: Map<Type, Type> by lazy {
            hashMapOf(
                Byte::class.java.asm to BYTE_TYPE,
                Short::class.java.asm to SHORT_TYPE,
                Integer::class.java.asm to INT_TYPE,
                Long::class.java.asm to LONG_TYPE,
                Float::class.java.asm to FLOAT_TYPE,
                Double::class.java.asm to DOUBLE_TYPE,
            )
        }

        /**
         * ASM type for array of [java.lang.Object].
         */
        val OBJECT_ARRAY_TYPE: Type = getType("[Ljava/lang/Object;")
    }
}
