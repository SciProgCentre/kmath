package kscience.kmath.asm.internal

import kscience.kmath.asm.internal.AsmBuilder.ClassLoader
import kscience.kmath.ast.MST
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.Algebra
import kscience.kmath.operations.NumericAlgebra
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.InstructionAdapter
import java.util.*
import java.util.stream.Collectors

/**
 * ASM Builder is a structure that abstracts building a class designated to unwrap [MST] to plain Java expression.
 * This class uses [ClassLoader] for loading the generated class, then it is able to instantiate the new class.
 *
 * @property T the type of AsmExpression to unwrap.
 * @property algebra the algebra the applied AsmExpressions use.
 * @property className the unique class name of new loaded class.
 * @property invokeLabel0Visitor the function to apply to this object when generating invoke method, label 0.
 * @author Iaroslav Postovalov
 */
internal class AsmBuilder<T> internal constructor(
    private val classOfT: Class<*>,
    private val algebra: Algebra<T>,
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
     * ASM Type for [algebra].
     */
    private val tAlgebraType: Type = algebra.javaClass.asm

    /**
     * ASM type for [T].
     */
    internal val tType: Type = classOfT.asm

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
     * States whether [T] a primitive type, so [AsmBuilder] may generate direct primitive calls.
     */
    internal var primitiveMode: Boolean = false

    /**
     * Primitive type to apply for specific primitive calls. Use [OBJECT_TYPE], if not in [primitiveMode].
     */
    internal var primitiveMask: Type = OBJECT_TYPE

    /**
     * Boxed primitive type to apply for specific primitive calls. Use [OBJECT_TYPE], if not in [primitiveMode].
     */
    internal var primitiveMaskBoxed: Type = OBJECT_TYPE

    /**
     * Stack of useful objects types on stack to verify types.
     */
    private val typeStack: ArrayDeque<Type> = ArrayDeque()

    /**
     * Stack of useful objects types on stack expected by algebra calls.
     */
    internal val expectationStack: ArrayDeque<Type> = ArrayDeque<Type>(1).also { it.push(tType) }

    /**
     * The cache for instance built by this builder.
     */
    private var generatedInstance: Expression<T>? = null

    /**
     * Subclasses, loads and instantiates [Expression] for given parameters.
     *
     * The built instance is cached.
     */
    @Suppress("UNCHECKED_CAST")
    internal fun getInstance(): Expression<T> {
        generatedInstance?.let { return it }

        if (SIGNATURE_LETTERS.containsKey(classOfT)) {
            primitiveMode = true
            primitiveMask = SIGNATURE_LETTERS.getValue(classOfT)
            primitiveMaskBoxed = tType
        }

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

            visitField(
                access = ACC_PRIVATE or ACC_FINAL,
                name = "algebra",
                descriptor = tAlgebraType.descriptor,
                signature = null,
                value = null,
                block = FieldVisitor::visitEnd
            )

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

                Type.getMethodDescriptor(
                    Type.VOID_TYPE,
                    tAlgebraType,
                    *OBJECT_ARRAY_TYPE.wrapToArrayIf { hasConstants }),

                null,
                null
            ).instructionAdapter {
                val thisVar = 0
                val algebraVar = 1
                val constantsVar = 2
                val l0 = label()
                load(thisVar, classType)
                invokespecial(OBJECT_TYPE.internalName, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE), false)
                label()
                load(thisVar, classType)
                load(algebraVar, tAlgebraType)
                putfield(classType.internalName, "algebra", tAlgebraType.descriptor)

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

                visitLocalVariable(
                    "algebra",
                    tAlgebraType.descriptor,
                    null,
                    l0,
                    l4,
                    algebraVar
                )

                if (hasConstants)
                    visitLocalVariable("constants", OBJECT_ARRAY_TYPE.descriptor, null, l0, l4, constantsVar)

                visitMaxs(0, 3)
                visitEnd()
            }

            visitEnd()
        }

        val new = classLoader
            .defineClass(className, classWriter.toByteArray())
            .constructors
            .first()
            .newInstance(algebra, *(constants.toTypedArray().wrapToArrayIf { hasConstants })) as Expression<T>

        generatedInstance = new
        return new
    }

    /**
     * Loads a [T] constant from [constants].
     */
    internal fun loadTConstant(value: T) {
        if (classOfT in INLINABLE_NUMBERS) {
            val expectedType = expectationStack.pop()
            val mustBeBoxed = expectedType.sort == Type.OBJECT
            loadNumberConstant(value as Number, mustBeBoxed)

            if (mustBeBoxed)
                invokeMethodVisitor.checkcast(tType)

            if (mustBeBoxed) typeStack.push(tType) else typeStack.push(primitiveMask)
            return
        }

        loadObjectConstant(value as Any, tType)
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
     * Unboxes the current boxed value and pushes it.
     */
    private fun unboxTo(primitive: Type) = invokeMethodVisitor.invokevirtual(
        NUMBER_TYPE.internalName,
        NUMBER_CONVERTER_METHODS.getValue(primitive),
        Type.getMethodDescriptor(primitive),
        false
    )

    /**
     * Loads [java.lang.Object] constant from constants.
     */
    private fun loadObjectConstant(value: Any, type: Type): Unit = invokeMethodVisitor.run {
        val idx = if (value in constants) constants.indexOf(value) else constants.apply { add(value) }.lastIndex
        loadThis()
        getfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
        iconst(idx)
        visitInsn(AALOAD)
        checkcast(type)
    }

    internal fun loadNumeric(value: Number) {
        if (expectationStack.peek() == NUMBER_TYPE) {
            loadNumberConstant(value, true)
            expectationStack.pop()
            typeStack.push(NUMBER_TYPE)
        } else (algebra as? NumericAlgebra<T>)?.number(value)?.let { loadTConstant(it) }
            ?: error("Cannot resolve numeric $value since target algebra is not numeric, and the current operation doesn't accept numbers.")
    }

    /**
     * Loads this variable.
     */
    private fun loadThis(): Unit = invokeMethodVisitor.load(invokeThisVar, classType)

    /**
     * Either loads a numeric constant [value] from the class's constants field or boxes a primitive
     * constant from the constant pool (some numbers with special opcodes like [Opcodes.ICONST_0] aren't even loaded
     * from it).
     */
    private fun loadNumberConstant(value: Number, mustBeBoxed: Boolean) {
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

            if (mustBeBoxed)
                box(primitive)

            return
        }

        loadObjectConstant(value, boxed)

        if (!mustBeBoxed)
            unboxTo(primitiveMask)
    }

    /**
     * Loads a variable [name] from arguments [Map] parameter of [Expression.invoke]. The [defaultValue] may be
     * provided.
     */
    internal fun loadVariable(name: String): Unit = invokeMethodVisitor.run {
        load(invokeArgumentsVar, MAP_TYPE)
        aconst(name)

        invokestatic(
            MAP_INTRINSICS_TYPE.internalName,
            "getOrFail",
            Type.getMethodDescriptor(OBJECT_TYPE, MAP_TYPE, STRING_TYPE),
            false
        )

        checkcast(tType)
        val expectedType = expectationStack.pop()

        if (expectedType.sort == Type.OBJECT)
            typeStack.push(tType)
        else {
            unboxTo(primitiveMask)
            typeStack.push(primitiveMask)
        }
    }

    /**
     * Loads algebra from according field of the class and casts it to class of [algebra] provided.
     */
    internal fun loadAlgebra() {
        loadThis()
        invokeMethodVisitor.getfield(classType.internalName, "algebra", tAlgebraType.descriptor)
    }

    /**
     * Writes a method instruction of opcode with its [owner], [method] and its [descriptor]. The default opcode is
     * [Opcodes.INVOKEINTERFACE], since most Algebra functions are declared in interfaces. [loadAlgebra] should be
     * called before the arguments and this operation.
     *
     * The result is casted to [T] automatically.
     */
    internal fun invokeAlgebraOperation(
        owner: String,
        method: String,
        descriptor: String,
        expectedArity: Int,
        opcode: Int = INVOKEINTERFACE,
    ) {
        run loop@{
            repeat(expectedArity) {
                if (typeStack.isEmpty()) return@loop
                typeStack.pop()
            }
        }

        invokeMethodVisitor.visitMethodInsn(
            opcode,
            owner,
            method,
            descriptor,
            opcode == INVOKEINTERFACE
        )

        invokeMethodVisitor.checkcast(tType)
        val isLastExpr = expectationStack.size == 1
        val expectedType = expectationStack.pop()

        if (expectedType.sort == Type.OBJECT || isLastExpr)
            typeStack.push(tType)
        else {
            unboxTo(primitiveMask)
            typeStack.push(primitiveMask)
        }
    }

    /**
     * Writes a LDC Instruction with string constant provided.
     */
    internal fun loadStringConstant(string: String): Unit = invokeMethodVisitor.aconst(string)

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
                Collectors.toMap(
                    Map.Entry<Type, Type>::value,
                    Map.Entry<Type, Type>::key
                )
            )
        }

        /**
         * Maps primitive ASM types to [Number] functions unboxing them.
         */
        private val NUMBER_CONVERTER_METHODS: Map<Type, String> by lazy {
            hashMapOf(
                Type.BYTE_TYPE to "byteValue",
                Type.SHORT_TYPE to "shortValue",
                Type.INT_TYPE to "intValue",
                Type.LONG_TYPE to "longValue",
                Type.FLOAT_TYPE to "floatValue",
                Type.DOUBLE_TYPE to "doubleValue"
            )
        }

        /**
         * Provides boxed number types values of which can be stored in JVM bytecode constant pool.
         */
        private val INLINABLE_NUMBERS: Set<Class<out Any>> by lazy { SIGNATURE_LETTERS.keys }

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
