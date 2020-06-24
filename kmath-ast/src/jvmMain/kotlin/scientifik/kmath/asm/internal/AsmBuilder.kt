package scientifik.kmath.asm.internal

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.AALOAD
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.commons.InstructionAdapter
import scientifik.kmath.asm.internal.AsmBuilder.ClassLoader
import scientifik.kmath.ast.MST
import scientifik.kmath.operations.Algebra
import java.util.*
import kotlin.reflect.KClass

/**
 * ASM Builder is a structure that abstracts building a class designated to unwrap [MST] to plain Java expression.
 * This class uses [ClassLoader] for loading the generated class, then it is able to instantiate the new class.
 *
 * @param T the type of AsmExpression to unwrap.
 * @param algebra the algebra the applied AsmExpressions use.
 * @param className the unique class name of new loaded class.
 */
internal class AsmBuilder<T> internal constructor(
    private val classOfT: KClass<*>,
    private val algebra: Algebra<T>,
    private val className: String,
    private val invokeLabel0Visitor: AsmBuilder<T>.() -> Unit
) {
    /**
     * Internal classloader of [AsmBuilder] with alias to define class from byte array.
     */
    private class ClassLoader(parent: java.lang.ClassLoader) : java.lang.ClassLoader(parent) {
        internal fun defineClass(name: String?, b: ByteArray): Class<*> = defineClass(name, b, 0, b.size)
    }

    /**
     * The instance of [ClassLoader] used by this builder.
     */
    private val classLoader: ClassLoader = ClassLoader(javaClass.classLoader)

    @Suppress("PrivatePropertyName")
    private val T_ALGEBRA_TYPE: Type = algebra::class.asm

    @Suppress("PrivatePropertyName")
    internal val T_TYPE: Type = classOfT.asm

    @Suppress("PrivatePropertyName")
    private val CLASS_TYPE: Type = Type.getObjectType(className.replace(oldChar = '.', newChar = '/'))!!

    /**
     * Index of `this` variable in invoke method of [AsmCompiledExpression] built subclass.
     */
    private val invokeThisVar: Int = 0

    /**
     * Index of `arguments` variable in invoke method of [AsmCompiledExpression] built subclass.
     */
    private val invokeArgumentsVar: Int = 1

    /**
     * List of constants to provide to [AsmCompiledExpression] subclass.
     */
    private val constants: MutableList<Any> = mutableListOf()

    /**
     * Method visitor of `invoke` method of [AsmCompiledExpression] subclass.
     */
    private lateinit var invokeMethodVisitor: InstructionAdapter
    internal var primitiveMode = false

    @Suppress("PropertyName")
    internal var PRIMITIVE_MASK: Type = OBJECT_TYPE

    @Suppress("PropertyName")
    internal var PRIMITIVE_MASK_BOXED: Type = OBJECT_TYPE
    private val typeStack = Stack<Type>()
    internal val expectationStack = Stack<Type>().apply { push(T_TYPE) }

    /**
     * The cache of [AsmCompiledExpression] subclass built by this builder.
     */
    private var generatedInstance: AsmCompiledExpression<T>? = null

    /**
     * Subclasses, loads and instantiates the [AsmCompiledExpression] for given parameters.
     *
     * The built instance is cached.
     */
    @Suppress("UNCHECKED_CAST")
    fun getInstance(): AsmCompiledExpression<T> {
        generatedInstance?.let { return it }

        if (SIGNATURE_LETTERS.containsKey(classOfT.java)) {
            primitiveMode = true
            PRIMITIVE_MASK = SIGNATURE_LETTERS.getValue(classOfT.java)
            PRIMITIVE_MASK_BOXED = T_TYPE
        }

        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
                CLASS_TYPE.internalName,
                "L${ASM_COMPILED_EXPRESSION_TYPE.internalName}<${T_TYPE.descriptor}>;",
                ASM_COMPILED_EXPRESSION_TYPE.internalName,
                arrayOf()
            )

            visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                Type.getMethodDescriptor(Type.VOID_TYPE, ALGEBRA_TYPE, OBJECT_ARRAY_TYPE),
                null,
                null
            ).instructionAdapter {
                val thisVar = 0
                val algebraVar = 1
                val constantsVar = 2
                val l0 = Label()
                visitLabel(l0)
                load(thisVar, CLASS_TYPE)
                load(algebraVar, ALGEBRA_TYPE)
                load(constantsVar, OBJECT_ARRAY_TYPE)

                invokespecial(
                    ASM_COMPILED_EXPRESSION_TYPE.internalName,
                    "<init>",
                    Type.getMethodDescriptor(Type.VOID_TYPE, ALGEBRA_TYPE, OBJECT_ARRAY_TYPE),
                    false
                )

                val l1 = Label()
                visitLabel(l1)
                visitInsn(RETURN)
                val l2 = Label()
                visitLabel(l2)
                visitLocalVariable("this", CLASS_TYPE.descriptor, null, l0, l2, thisVar)

                visitLocalVariable(
                    "algebra",
                    ALGEBRA_TYPE.descriptor,
                    "L${ALGEBRA_TYPE.internalName}<${T_TYPE.descriptor}>;",
                    l0,
                    l2,
                    algebraVar
                )

                visitLocalVariable("constants", OBJECT_ARRAY_TYPE.descriptor, null, l0, l2, constantsVar)
                visitMaxs(0, 3)
                visitEnd()
            }

            visitMethod(
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
                "invoke",
                Type.getMethodDescriptor(T_TYPE, MAP_TYPE),
                "(L${MAP_TYPE.internalName}<${STRING_TYPE.descriptor}+${T_TYPE.descriptor}>;)${T_TYPE.descriptor}",
                null
            ).instructionAdapter {
                invokeMethodVisitor = this
                visitCode()
                val l0 = Label()
                visitLabel(l0)
                invokeLabel0Visitor()
                areturn(T_TYPE)
                val l1 = Label()
                visitLabel(l1)

                visitLocalVariable(
                    "this",
                    CLASS_TYPE.descriptor,
                    null,
                    l0,
                    l1,
                    invokeThisVar
                )

                visitLocalVariable(
                    "arguments",
                    MAP_TYPE.descriptor,
                    "L${MAP_TYPE.internalName}<${STRING_TYPE.descriptor}+${T_TYPE.descriptor}>;",
                    l0,
                    l1,
                    invokeArgumentsVar
                )

                visitMaxs(0, 2)
                visitEnd()
            }

            visitMethod(
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_BRIDGE or Opcodes.ACC_SYNTHETIC,
                "invoke",
                Type.getMethodDescriptor(OBJECT_TYPE, MAP_TYPE),
                null,
                null
            ).instructionAdapter {
                val thisVar = 0
                val argumentsVar = 1
                visitCode()
                val l0 = Label()
                visitLabel(l0)
                load(thisVar, OBJECT_TYPE)
                load(argumentsVar, MAP_TYPE)
                invokevirtual(CLASS_TYPE.internalName, "invoke", Type.getMethodDescriptor(T_TYPE, MAP_TYPE), false)
                areturn(T_TYPE)
                val l1 = Label()
                visitLabel(l1)

                visitLocalVariable(
                    "this",
                    CLASS_TYPE.descriptor,
                    null,
                    l0,
                    l1,
                    thisVar
                )

                visitMaxs(0, 2)
                visitEnd()
            }

            visitEnd()
        }

        val new = classLoader
            .defineClass(className, classWriter.toByteArray())
            .constructors
            .first()
            .newInstance(algebra, constants.toTypedArray()) as AsmCompiledExpression<T>

        generatedInstance = new
        return new
    }

    /**
     * Loads a constant from
     */
    internal fun loadTConstant(value: T) {
        if (classOfT.java in INLINABLE_NUMBERS) {
            val expectedType = expectationStack.pop()!!
            val mustBeBoxed = expectedType.sort == Type.OBJECT
            loadNumberConstant(value as Number, mustBeBoxed)
            if (mustBeBoxed) typeStack.push(T_TYPE) else typeStack.push(PRIMITIVE_MASK)
            return
        }

        loadConstant(value as Any, T_TYPE)
    }

    private fun box(): Unit = invokeMethodVisitor.invokestatic(
        T_TYPE.internalName,
        "valueOf",
        Type.getMethodDescriptor(T_TYPE, PRIMITIVE_MASK),
        false
    )

    private fun unbox(): Unit = invokeMethodVisitor.invokevirtual(
        NUMBER_TYPE.internalName,
        NUMBER_CONVERTER_METHODS.getValue(PRIMITIVE_MASK),
        Type.getMethodDescriptor(PRIMITIVE_MASK),
        false
    )

    private fun loadConstant(value: Any, type: Type): Unit = invokeMethodVisitor.run {
        val idx = if (value in constants) constants.indexOf(value) else constants.apply { add(value) }.lastIndex
        loadThis()
        getfield(CLASS_TYPE.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
        iconst(idx)
        visitInsn(AALOAD)
        checkcast(type)
    }

    private fun loadThis(): Unit = invokeMethodVisitor.load(invokeThisVar, CLASS_TYPE)

    /**
     * Either loads a numeric constant [value] from [AsmCompiledExpression] constants field or boxes a primitive
     * constant from the constant pool (some numbers with special opcodes like [Opcodes.ICONST_0] aren't even loaded
     * from it).
     */
    private fun loadNumberConstant(value: Number, mustBeBoxed: Boolean) {
        val boxed = value::class.asm
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

            if (mustBeBoxed) {
                box()
                invokeMethodVisitor.checkcast(T_TYPE)
            }

            return
        }

        loadConstant(value, boxed)
        if (!mustBeBoxed) unbox()
        else invokeMethodVisitor.checkcast(T_TYPE)
    }

    /**
     * Loads a variable [name] from [AsmCompiledExpression.invoke] [Map] parameter. The [defaultValue] may be provided.
     */
    internal fun loadVariable(name: String, defaultValue: T? = null): Unit = invokeMethodVisitor.run {
        load(invokeArgumentsVar, OBJECT_ARRAY_TYPE)

        if (defaultValue != null) {
            loadStringConstant(name)
            loadTConstant(defaultValue)

            invokeinterface(
                MAP_TYPE.internalName,
                "getOrDefault",
                Type.getMethodDescriptor(OBJECT_TYPE, OBJECT_TYPE, OBJECT_TYPE)
            )

            invokeMethodVisitor.checkcast(T_TYPE)
            return
        }

        loadStringConstant(name)

        invokeinterface(
            MAP_TYPE.internalName,
            "get",
            Type.getMethodDescriptor(OBJECT_TYPE, OBJECT_TYPE)
        )

        invokeMethodVisitor.checkcast(T_TYPE)
        val expectedType = expectationStack.pop()!!

        if (expectedType.sort == Type.OBJECT)
            typeStack.push(T_TYPE)
        else {
            unbox()
            typeStack.push(PRIMITIVE_MASK)
        }
    }

    /**
     * Loads algebra from according field of [AsmCompiledExpression] and casts it to class of [algebra] provided.
     */
    internal fun loadAlgebra() {
        loadThis()

        invokeMethodVisitor.run {
            getfield(ASM_COMPILED_EXPRESSION_TYPE.internalName, "algebra", ALGEBRA_TYPE.descriptor)
            checkcast(T_ALGEBRA_TYPE)
        }
    }

    /**
     * Writes a method instruction of opcode with its [owner], [method] and its [descriptor]. The default opcode is
     * [Opcodes.INVOKEINTERFACE], since most Algebra functions are declared in interface. [loadAlgebra] should be
     * called before the arguments and this operation.
     *
     * The result is casted to [T] automatically.
     */
    internal fun invokeAlgebraOperation(
        owner: String,
        method: String,
        descriptor: String,
        tArity: Int,
        opcode: Int = Opcodes.INVOKEINTERFACE
    ) {
        repeat(tArity) { typeStack.pop() }

        invokeMethodVisitor.visitMethodInsn(
            opcode,
            owner,
            method,
            descriptor,
            opcode == Opcodes.INVOKEINTERFACE
        )

        invokeMethodVisitor.checkcast(T_TYPE)
        val isLastExpr = expectationStack.size == 1
        val expectedType = expectationStack.pop()!!

        if (expectedType.sort == Type.OBJECT || isLastExpr)
            typeStack.push(T_TYPE)
        else {
            unbox()
            typeStack.push(PRIMITIVE_MASK)
        }
    }

    /**
     * Writes a LDC Instruction with string constant provided.
     */
    internal fun loadStringConstant(string: String): Unit = invokeMethodVisitor.aconst(string)

    internal companion object {
        /**
         * Maps JVM primitive numbers boxed types to their letters of JVM signature convention.
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

        private val BOXED_TO_PRIMITIVES: Map<Type, Type> by lazy {
            hashMapOf(
                java.lang.Byte::class.asm to Type.BYTE_TYPE,
                java.lang.Short::class.asm to Type.SHORT_TYPE,
                java.lang.Integer::class.asm to Type.INT_TYPE,
                java.lang.Long::class.asm to Type.LONG_TYPE,
                java.lang.Float::class.asm to Type.FLOAT_TYPE,
                java.lang.Double::class.asm to Type.DOUBLE_TYPE
            )
        }

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
        internal val ASM_COMPILED_EXPRESSION_TYPE: Type = AsmCompiledExpression::class.asm
        internal val NUMBER_TYPE: Type = java.lang.Number::class.asm
        internal val MAP_TYPE: Type = java.util.Map::class.asm
        internal val OBJECT_TYPE: Type = java.lang.Object::class.asm

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "RemoveRedundantQualifierName")
        internal val OBJECT_ARRAY_TYPE: Type = Array<java.lang.Object>::class.asm
        internal val ALGEBRA_TYPE: Type = Algebra::class.asm
        internal val STRING_TYPE: Type = java.lang.String::class.asm
    }
}
