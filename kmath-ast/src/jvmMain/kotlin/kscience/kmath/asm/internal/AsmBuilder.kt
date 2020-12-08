package kscience.kmath.asm.internal

import kscience.kmath.asm.internal.AsmBuilder.ClassLoader
import kscience.kmath.ast.MST
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.Algebra
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.InstructionAdapter
import java.util.stream.Collectors.toMap
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * ASM Builder is a structure that abstracts building a class designated to unwrap [MST] to plain Java expression.
 * This class uses [ClassLoader] for loading the generated class, then it is able to instantiate the new class.
 *
 * @property T the type of AsmExpression to unwrap.
 * @property className the unique class name of new loaded class.
 * @property invokeLabel0Visitor the function to apply to this object when generating invoke method, label 0.
 * @author Iaroslav Postovalov
 */
internal class AsmBuilder<T> internal constructor(
    classOfT: Class<*>,
    private val className: String,
    private val invokeLabel0Visitor: AsmBuilder<T>.() -> Unit,
) {
    /**
     * Internal classloader of [AsmBuilder] with alias to define class from byte array.
     */
    private class ClassLoader(parent: java.lang.ClassLoader) : java.lang.ClassLoader(parent) {
        fun defineClass(name: String?, b: ByteArray): Class<*> = defineClass(name, b, 0, b.size)
    }

    /**
     * The instance of [ClassLoader] used by this builder.
     */
    private val classLoader: ClassLoader = ClassLoader(javaClass.classLoader)

    /**
     * ASM type for [T].
     */
    private val tType: Type = classOfT.asm

    /**
     * ASM type for new class.
     */
    private val classType: Type = Type.getObjectType(className.replace(oldChar = '.', newChar = '/'))!!

    /**
     * List of constants to provide to the subclass.
     */
    private val constants: MutableList<Any> = mutableListOf()

    /**
     * Method visitor of `invoke` method of the subclass.
     */
    private lateinit var invokeMethodVisitor: InstructionAdapter

    /**
     * States whether this [AsmBuilder] needs to generate constants field.
     */
    private var hasConstants: Boolean = true

    /**
     * Subclasses, loads and instantiates [Expression] for given parameters.
     *
     * The built instance is cached.
     */
    @Suppress("UNCHECKED_CAST")
    val instance: Expression<T> by lazy {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            visit(
                V1_8,
                ACC_PUBLIC or ACC_FINAL or ACC_SUPER,
                classType.internalName,
                "${OBJECT_TYPE.descriptor}L${EXPRESSION_TYPE.internalName}<${tType.descriptor}>;",
                OBJECT_TYPE.internalName,
                arrayOf(EXPRESSION_TYPE.internalName)
            )

            visitMethod(
                ACC_PUBLIC or ACC_FINAL,
                "invoke",
                Type.getMethodDescriptor(tType, MAP_TYPE),
                "(L${MAP_TYPE.internalName}<${STRING_TYPE.descriptor}+${tType.descriptor}>;)${tType.descriptor}",
                null
            ).instructionAdapter {
                invokeMethodVisitor = this
                visitCode()
                val l0 = label()
                invokeLabel0Visitor()
                areturn(tType)
                val l1 = label()

                visitLocalVariable(
                    "this",
                    classType.descriptor,
                    null,
                    l0,
                    l1,
                    invokeThisVar
                )

                visitLocalVariable(
                    "arguments",
                    MAP_TYPE.descriptor,
                    "L${MAP_TYPE.internalName}<${STRING_TYPE.descriptor}+${tType.descriptor}>;",
                    l0,
                    l1,
                    invokeArgumentsVar
                )

                visitMaxs(0, 2)
                visitEnd()
            }

            visitMethod(
                ACC_PUBLIC or ACC_FINAL or ACC_BRIDGE or ACC_SYNTHETIC,
                "invoke",
                Type.getMethodDescriptor(OBJECT_TYPE, MAP_TYPE),
                null,
                null
            ).instructionAdapter {
                val thisVar = 0
                val argumentsVar = 1
                visitCode()
                val l0 = label()
                load(thisVar, OBJECT_TYPE)
                load(argumentsVar, MAP_TYPE)
                invokevirtual(classType.internalName, "invoke", Type.getMethodDescriptor(tType, MAP_TYPE), false)
                areturn(tType)
                val l1 = label()

                visitLocalVariable(
                    "this",
                    classType.descriptor,
                    null,
                    l0,
                    l1,
                    thisVar
                )

                visitMaxs(0, 2)
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
                    block = FieldVisitor::visitEnd
                )

            visitMethod(
                ACC_PUBLIC,
                "<init>",
                Type.getMethodDescriptor(Type.VOID_TYPE, *OBJECT_ARRAY_TYPE.wrapToArrayIf { hasConstants }),
                null,
                null
            ).instructionAdapter {
                val thisVar = 0
                val constantsVar = 1
                val l0 = label()
                load(thisVar, classType)
                invokespecial(OBJECT_TYPE.internalName, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE), false)
                label()
                load(thisVar, classType)

                if (hasConstants) {
                    label()
                    load(thisVar, classType)
                    load(constantsVar, OBJECT_ARRAY_TYPE)
                    putfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
                }

                label()
                visitInsn(RETURN)
                val l4 = label()
                visitLocalVariable("this", classType.descriptor, null, l0, l4, thisVar)

                if (hasConstants)
                    visitLocalVariable("constants", OBJECT_ARRAY_TYPE.descriptor, null, l0, l4, constantsVar)

                visitMaxs(0, 3)
                visitEnd()
            }

            visitEnd()
        }

//        java.io.File("dump.class").writeBytes(classWriter.toByteArray())

        classLoader
            .defineClass(className, classWriter.toByteArray())
            .constructors
            .first()
            .newInstance(*(constants.toTypedArray().wrapToArrayIf { hasConstants })) as Expression<T>
    }

    /**
     * Loads [java.lang.Object] constant from constants.
     */
    fun loadObjectConstant(value: Any, type: Type = tType): Unit = invokeMethodVisitor.run {
        val idx = if (value in constants) constants.indexOf(value) else constants.also { it += value }.lastIndex
        loadThis()
        getfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
        iconst(idx)
        visitInsn(AALOAD)
        checkcast(type)
    }

    /**
     * Loads `this` variable.
     */
    private fun loadThis(): Unit = invokeMethodVisitor.load(invokeThisVar, classType)

    /**
     * Either loads a numeric constant [value] from the class's constants field or boxes a primitive
     * constant from the constant pool.
     */
    fun loadNumberConstant(value: Number) {
        val boxed = value.javaClass.asm
        val primitive = BOXED_TO_PRIMITIVES[boxed]

        if (primitive != null) {
            when (primitive) {
                Type.BYTE_TYPE -> invokeMethodVisitor.iconst(value.toInt())
                Type.DOUBLE_TYPE -> invokeMethodVisitor.dconst(value.toDouble())
                Type.FLOAT_TYPE -> invokeMethodVisitor.fconst(value.toFloat())
                Type.LONG_TYPE -> invokeMethodVisitor.lconst(value.toLong())
                Type.INT_TYPE -> invokeMethodVisitor.iconst(value.toInt())
                Type.SHORT_TYPE -> invokeMethodVisitor.iconst(value.toInt())
            }

            box(primitive)
            return
        }

        loadObjectConstant(value, boxed)
    }

    /**
     * Boxes the current value and pushes it.
     */
    private fun box(primitive: Type) {
        val r = PRIMITIVES_TO_BOXED.getValue(primitive)

        invokeMethodVisitor.invokestatic(
            r.internalName,
            "valueOf",
            Type.getMethodDescriptor(r, primitive),
            false
        )
    }

    /**
     * Loads a variable [name] from arguments [Map] parameter of [Expression.invoke].
     */
    fun loadVariable(name: String): Unit = invokeMethodVisitor.run {
        load(invokeArgumentsVar, MAP_TYPE)
        aconst(name)

        invokestatic(
            MAP_INTRINSICS_TYPE.internalName,
            "getOrFail",
            Type.getMethodDescriptor(OBJECT_TYPE, MAP_TYPE, STRING_TYPE),
            false
        )

        checkcast(tType)
    }

    inline fun buildCall(function: Function<T>, parameters: AsmBuilder<T>.() -> Unit) {
        contract { callsInPlace(parameters, InvocationKind.EXACTLY_ONCE) }
        val `interface` = function.javaClass.interfaces.first { it.interfaces.contains(Function::class.java) }

        val arity = `interface`.methods.find { it.name == "invoke" }?.parameterCount
            ?: error("Provided function object doesn't contain invoke method")

        val type = Type.getType(`interface`)
        loadObjectConstant(function, type)
        parameters(this)

        invokeMethodVisitor.invokeinterface(
            type.internalName,
            "invoke",
            Type.getMethodDescriptor(OBJECT_TYPE, *Array(arity) { OBJECT_TYPE}),
        )

        invokeMethodVisitor.checkcast(tType)
    }

    internal companion object {
        /**
         * Index of `this` variable in invoke method of the built subclass.
         */
        private const val invokeThisVar: Int = 0

        /**
         * Index of `arguments` variable in invoke method of the built subclass.
         */
        private const val invokeArgumentsVar: Int = 1

        /**
         * Maps JVM primitive numbers boxed types to their primitive ASM types.
         */
        private val SIGNATURE_LETTERS: Map<Class<out Any>, Type> by lazy {
            hashMapOf(
                java.lang.Byte::class.java to Type.BYTE_TYPE,
                java.lang.Short::class.java to Type.SHORT_TYPE,
                java.lang.Integer::class.java to Type.INT_TYPE,
                java.lang.Long::class.java to Type.LONG_TYPE,
                java.lang.Float::class.java to Type.FLOAT_TYPE,
                java.lang.Double::class.java to Type.DOUBLE_TYPE
            )
        }

        /**
         * Maps JVM primitive numbers boxed ASM types to their primitive ASM types.
         */
        private val BOXED_TO_PRIMITIVES: Map<Type, Type> by lazy { SIGNATURE_LETTERS.mapKeys { (k, _) -> k.asm } }

        /**
         * Maps JVM primitive numbers boxed ASM types to their primitive ASM types.
         */
        private val PRIMITIVES_TO_BOXED: Map<Type, Type> by lazy {
            BOXED_TO_PRIMITIVES.entries.stream().collect(
                toMap(
                    Map.Entry<Type, Type>::value,
                    Map.Entry<Type, Type>::key
                )
            )
        }

        /**
         * ASM type for [Expression].
         */
        internal val EXPRESSION_TYPE: Type by lazy { Type.getObjectType("kscience/kmath/expressions/Expression") }

        /**
         * ASM type for [java.lang.Number].
         */
        internal val NUMBER_TYPE: Type by lazy { Type.getObjectType("java/lang/Number") }

        /**
         * ASM type for [java.util.Map].
         */
        internal val MAP_TYPE: Type by lazy { Type.getObjectType("java/util/Map") }

        /**
         * ASM type for [java.lang.Object].
         */
        internal val OBJECT_TYPE: Type by lazy { Type.getObjectType("java/lang/Object") }

        /**
         * ASM type for array of [java.lang.Object].
         */
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "RemoveRedundantQualifierName")
        internal val OBJECT_ARRAY_TYPE: Type by lazy { Type.getType("[Ljava/lang/Object;") }

        /**
         * ASM type for [Algebra].
         */
        internal val ALGEBRA_TYPE: Type by lazy { Type.getObjectType("kscience/kmath/operations/Algebra") }

        /**
         * ASM type for [java.lang.String].
         */
        internal val STRING_TYPE: Type by lazy { Type.getObjectType("java/lang/String") }

        /**
         * ASM type for MapIntrinsics.
         */
        internal val MAP_INTRINSICS_TYPE: Type by lazy { Type.getObjectType("kscience/kmath/asm/internal/MapIntrinsics") }
    }
}
