package scientifik.kmath.asm.internal

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.AALOAD
import org.objectweb.asm.Opcodes.RETURN
import org.objectweb.asm.commons.InstructionAdapter
import scientifik.kmath.asm.internal.AsmBuilder.ClassLoader
import scientifik.kmath.ast.MST
import scientifik.kmath.expressions.Expression
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

    private val tAlgebraType: Type = algebra::class.asm
    internal val tType: Type = classOfT.asm
    private val classType: Type = Type.getObjectType(className.replace(oldChar = '.', newChar = '/'))!!

    /**
     * Index of `this` variable in invoke method of the built subclass.
     */
    private val invokeThisVar: Int = 0

    /**
     * Index of `arguments` variable in invoke method of the built subclass.
     */
    private val invokeArgumentsVar: Int = 1

    /**
     * List of constants to provide to the subclass.
     */
    private val constants: MutableList<Any> = mutableListOf()

    /**
     * Method visitor of `invoke` method of the subclass.
     */
    private lateinit var invokeMethodVisitor: InstructionAdapter
    internal var primitiveMode = false

    @Suppress("PropertyName")
    internal var PRIMITIVE_MASK: Type = OBJECT_TYPE

    @Suppress("PropertyName")
    internal var PRIMITIVE_MASK_BOXED: Type = OBJECT_TYPE
    private val typeStack = Stack<Type>()
    internal val expectationStack: Stack<Type> = Stack<Type>().apply { push(tType) }

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
    fun getInstance(): Expression<T> {
        generatedInstance?.let { return it }

        if (SIGNATURE_LETTERS.containsKey(classOfT)) {
            primitiveMode = true
            PRIMITIVE_MASK = SIGNATURE_LETTERS.getValue(classOfT)
            PRIMITIVE_MASK_BOXED = tType
        }

        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
                classType.internalName,
                "${OBJECT_TYPE.descriptor}L${EXPRESSION_TYPE.internalName}<${tType.descriptor}>;",
                OBJECT_TYPE.internalName,
                arrayOf(EXPRESSION_TYPE.internalName)
            )

            visitField(
                access = Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL,
                name = "algebra",
                descriptor = tAlgebraType.descriptor,
                signature = null,
                value = null,
                block = FieldVisitor::visitEnd
            )

            visitField(
                access = Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL,
                name = "constants",
                descriptor = OBJECT_ARRAY_TYPE.descriptor,
                signature = null,
                value = null,
                block = FieldVisitor::visitEnd
            )

            visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                Type.getMethodDescriptor(Type.VOID_TYPE, tAlgebraType, OBJECT_ARRAY_TYPE),
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
                label()
                load(thisVar, classType)
                load(constantsVar, OBJECT_ARRAY_TYPE)
                putfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
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

                visitLocalVariable("constants", OBJECT_ARRAY_TYPE.descriptor, null, l0, l4, constantsVar)
                visitMaxs(0, 3)
                visitEnd()
            }

            visitMethod(
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
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
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_BRIDGE or Opcodes.ACC_SYNTHETIC,
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

            visitEnd()
        }

        val new = classLoader
            .defineClass(className, classWriter.toByteArray())
            .constructors
            .first()
            .newInstance(algebra, constants.toTypedArray()) as Expression<T>

        generatedInstance = new
        return new
    }

    /**
     * Loads a constant from
     */
    internal fun loadTConstant(value: T) {
        if (classOfT in INLINABLE_NUMBERS) {
            val expectedType = expectationStack.pop()!!
            val mustBeBoxed = expectedType.sort == Type.OBJECT
            loadNumberConstant(value as Number, mustBeBoxed)
            if (mustBeBoxed) typeStack.push(tType) else typeStack.push(PRIMITIVE_MASK)
            return
        }

        loadConstant(value as Any, tType)
    }

    private fun box(): Unit = invokeMethodVisitor.invokestatic(
        tType.internalName,
        "valueOf",
        Type.getMethodDescriptor(tType, PRIMITIVE_MASK),
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
        getfield(classType.internalName, "constants", OBJECT_ARRAY_TYPE.descriptor)
        iconst(idx)
        visitInsn(AALOAD)
        checkcast(type)
    }

    private fun loadThis(): Unit = invokeMethodVisitor.load(invokeThisVar, classType)

    /**
     * Either loads a numeric constant [value] from the class's constants field or boxes a primitive
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
                invokeMethodVisitor.checkcast(tType)
            }

            return
        }

        loadConstant(value, boxed)
        if (!mustBeBoxed) unbox()
        else invokeMethodVisitor.checkcast(tType)
    }

    /**
     * Loads a variable [name] arguments [Map] parameter of [Expression.invoke]. The [defaultValue] may be provided.
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

            invokeMethodVisitor.checkcast(tType)
            return
        }

        loadStringConstant(name)

        invokeinterface(
            MAP_TYPE.internalName,
            "get",
            Type.getMethodDescriptor(OBJECT_TYPE, OBJECT_TYPE)
        )

        invokeMethodVisitor.checkcast(tType)
        val expectedType = expectationStack.pop()!!

        if (expectedType.sort == Type.OBJECT)
            typeStack.push(tType)
        else {
            unbox()
            typeStack.push(PRIMITIVE_MASK)
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
        run loop@{
            repeat(tArity) {
                if (typeStack.empty()) return@loop
                typeStack.pop()
            }
        }

        invokeMethodVisitor.visitMethodInsn(
            opcode,
            owner,
            method,
            descriptor,
            opcode == Opcodes.INVOKEINTERFACE
        )

        invokeMethodVisitor.checkcast(tType)
        val isLastExpr = expectationStack.size == 1
        val expectedType = expectationStack.pop()!!

        if (expectedType.sort == Type.OBJECT || isLastExpr)
            typeStack.push(tType)
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
        private val SIGNATURE_LETTERS: Map<KClass<out Any>, Type> by lazy {
            hashMapOf(
                java.lang.Byte::class to Type.BYTE_TYPE,
                java.lang.Short::class to Type.SHORT_TYPE,
                java.lang.Integer::class to Type.INT_TYPE,
                java.lang.Long::class to Type.LONG_TYPE,
                java.lang.Float::class to Type.FLOAT_TYPE,
                java.lang.Double::class to Type.DOUBLE_TYPE
            )
        }

        private val BOXED_TO_PRIMITIVES: Map<Type, Type> by lazy { SIGNATURE_LETTERS.mapKeys { (k, _) -> k.asm } }

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
        private val INLINABLE_NUMBERS: Set<KClass<out Any>> by lazy { SIGNATURE_LETTERS.keys }
        internal val EXPRESSION_TYPE: Type by lazy { Expression::class.asm }
        internal val NUMBER_TYPE: Type by lazy { java.lang.Number::class.asm }
        internal val MAP_TYPE: Type by lazy { java.util.Map::class.asm }
        internal val OBJECT_TYPE: Type by lazy { java.lang.Object::class.asm }

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "RemoveRedundantQualifierName")
        internal val OBJECT_ARRAY_TYPE: Type by lazy { Array<java.lang.Object>::class.asm }
        internal val ALGEBRA_TYPE: Type by lazy { Algebra::class.asm }
        internal val STRING_TYPE: Type by lazy { java.lang.String::class.asm }
    }
}
