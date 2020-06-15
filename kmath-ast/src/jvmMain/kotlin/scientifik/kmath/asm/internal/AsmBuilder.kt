package scientifik.kmath.asm.internal

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import scientifik.kmath.asm.AsmExpression
import scientifik.kmath.asm.FunctionalCompiledExpression
import scientifik.kmath.asm.internal.AsmBuilder.ClassLoader
import scientifik.kmath.operations.Algebra

/**
 * ASM Builder is a structure that abstracts building a class that unwraps [AsmExpression] to plain Java expression.
 * This class uses [ClassLoader] for loading the generated class, then it is able to instantiate the new class.
 *
 * @param T the type of AsmExpression to unwrap.
 * @param algebra the algebra the applied AsmExpressions use.
 * @param className the unique class name of new loaded class.
 */
@PublishedApi
internal class AsmBuilder<T> @PublishedApi internal constructor(
    private val classOfT: Class<*>,
    private val algebra: Algebra<T>,
    private val className: String,
    private val root: AsmExpression<T>
) {
    private class ClassLoader(parent: java.lang.ClassLoader) : java.lang.ClassLoader(parent) {
        internal fun defineClass(name: String?, b: ByteArray): Class<*> = defineClass(name, b, 0, b.size)
    }

    private val classLoader: ClassLoader =
        ClassLoader(javaClass.classLoader)

    @Suppress("PrivatePropertyName")
    private val T_ALGEBRA_CLASS: String = algebra.javaClass.name.replace(oldChar = '.', newChar = '/')

    @Suppress("PrivatePropertyName")
    private val T_CLASS: String = classOfT.name.replace('.', '/')

    private val slashesClassName: String = className.replace(oldChar = '.', newChar = '/')
    private val invokeThisVar: Int = 0
    private val invokeArgumentsVar: Int = 1
    private val constants: MutableList<Any> = mutableListOf()
    private lateinit var invokeMethodVisitor: MethodVisitor
    private var generatedInstance: FunctionalCompiledExpression<T>? = null

    @Suppress("UNCHECKED_CAST")
    fun getInstance(): FunctionalCompiledExpression<T> {
        generatedInstance?.let { return it }

        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES) {
            visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
                slashesClassName,
                "L$FUNCTIONAL_COMPILED_EXPRESSION_CLASS<L$T_CLASS;>;",
                FUNCTIONAL_COMPILED_EXPRESSION_CLASS,
                arrayOf()
            )

            visitMethod(
                access = Opcodes.ACC_PUBLIC,
                name = "<init>",
                descriptor = "(L$ALGEBRA_CLASS;[L$OBJECT_CLASS;)V",
                signature = null,
                exceptions = null
            ) {
                val thisVar = 0
                val algebraVar = 1
                val constantsVar = 2
                val l0 = Label()
                visitLabel(l0)
                visitLoadObjectVar(thisVar)
                visitLoadObjectVar(algebraVar)
                visitLoadObjectVar(constantsVar)

                visitInvokeSpecial(
                    FUNCTIONAL_COMPILED_EXPRESSION_CLASS,
                    "<init>",
                    "(L$ALGEBRA_CLASS;[L$OBJECT_CLASS;)V"
                )

                val l1 = Label()
                visitLabel(l1)
                visitReturn()
                val l2 = Label()
                visitLabel(l2)
                visitLocalVariable("this", "L$slashesClassName;", null, l0, l2, thisVar)

                visitLocalVariable(
                    "algebra",
                    "L$ALGEBRA_CLASS;",
                    "L$ALGEBRA_CLASS<L$T_CLASS;>;",
                    l0,
                    l2,
                    algebraVar
                )

                visitLocalVariable("constants", "[L$OBJECT_CLASS;", null, l0, l2, constantsVar)
                visitMaxs(0, 3)
                visitEnd()
            }

            visitMethod(
                access = Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
                name = "invoke",
                descriptor = "(L$MAP_CLASS;)L$T_CLASS;",
                signature = "(L$MAP_CLASS<L$STRING_CLASS;+L$T_CLASS;>;)L$T_CLASS;",
                exceptions = null
            ) {
                invokeMethodVisitor = this
                visitCode()
                val l0 = Label()
                visitLabel(l0)
                root.compile(this@AsmBuilder)
                visitReturnObject()
                val l1 = Label()
                visitLabel(l1)

                visitLocalVariable(
                    "this",
                    "L$slashesClassName;",
                    null,
                    l0,
                    l1,
                    invokeThisVar
                )

                visitLocalVariable(
                    "arguments",
                    "L$MAP_CLASS;",
                    "L$MAP_CLASS<L$STRING_CLASS;+L$T_CLASS;>;",
                    l0,
                    l1,
                    invokeArgumentsVar
                )

                visitMaxs(0, 2)
                visitEnd()
            }

            visitMethod(
                access = Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_BRIDGE or Opcodes.ACC_SYNTHETIC,
                name = "invoke",
                descriptor = "(L$MAP_CLASS;)L$OBJECT_CLASS;",
                signature = null,
                exceptions = null
            ) {
                val thisVar = 0
                val argumentsVar = 1
                visitCode()
                val l0 = Label()
                visitLabel(l0)
                visitLoadObjectVar(thisVar)
                visitLoadObjectVar(argumentsVar)
                visitInvokeVirtual(owner = slashesClassName, name = "invoke", descriptor = "(L$MAP_CLASS;)L$T_CLASS;")
                visitReturnObject()
                val l1 = Label()
                visitLabel(l1)

                visitLocalVariable(
                    "this",
                    "L$slashesClassName;",
                    T_CLASS,
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
            .newInstance(algebra, constants.toTypedArray()) as FunctionalCompiledExpression<T>

        generatedInstance = new
        return new
    }

    internal fun loadTConstant(value: T) {
        if (classOfT in INLINABLE_NUMBERS) {
            loadNumberConstant(value as Number)
            invokeMethodVisitor.visitCheckCast(T_CLASS)
            return
        }

        loadConstant(value as Any, T_CLASS)
    }

    private fun loadConstant(value: Any, type: String) {
        val idx = if (value in constants) constants.indexOf(value) else constants.apply { add(value) }.lastIndex

        invokeMethodVisitor.run {
            loadThis()
            visitGetField(owner = slashesClassName, name = "constants", descriptor = "[L$OBJECT_CLASS;")
            visitLdcOrIntConstant(idx)
            visitGetObjectArrayElement()
            invokeMethodVisitor.visitCheckCast(type)
        }
    }

    private fun loadThis(): Unit = invokeMethodVisitor.visitLoadObjectVar(invokeThisVar)

    internal fun loadNumberConstant(value: Number) {
        val clazz = value.javaClass
        val c = clazz.name.replace('.', '/')
        val sigLetter = SIGNATURE_LETTERS[clazz]

        if (sigLetter != null) {
            when (value) {
                is Int -> invokeMethodVisitor.visitLdcOrIntConstant(value)
                is Double -> invokeMethodVisitor.visitLdcOrDoubleConstant(value)
                is Float -> invokeMethodVisitor.visitLdcOrFloatConstant(value)
                else -> invokeMethodVisitor.visitLdcInsn(value)
            }

            invokeMethodVisitor.visitInvokeStatic(c, "valueOf", "($sigLetter)L${c};")
            return
        }

        loadConstant(value, c)
    }

    internal fun loadVariable(name: String, defaultValue: T? = null): Unit = invokeMethodVisitor.run {
        visitLoadObjectVar(invokeArgumentsVar)

        if (defaultValue != null) {
            visitLdcInsn(name)
            loadTConstant(defaultValue)

            visitInvokeInterface(
                owner = MAP_CLASS,
                name = "getOrDefault",
                descriptor = "(L$OBJECT_CLASS;L$OBJECT_CLASS;)L$OBJECT_CLASS;"
            )

            invokeMethodVisitor.visitCheckCast(T_CLASS)
            return
        }

        visitLdcInsn(name)

        visitInvokeInterface(
            owner = MAP_CLASS,
            name = "get",
            descriptor = "(L$OBJECT_CLASS;)L$OBJECT_CLASS;"
        )

        invokeMethodVisitor.visitCheckCast(T_CLASS)
    }

    internal fun loadAlgebra() {
        loadThis()

        invokeMethodVisitor.visitGetField(
            owner = FUNCTIONAL_COMPILED_EXPRESSION_CLASS,
            name = "algebra",
            descriptor = "L$ALGEBRA_CLASS;"
        )

        invokeMethodVisitor.visitCheckCast(T_ALGEBRA_CLASS)
    }

    internal fun invokeAlgebraOperation(
        owner: String,
        method: String,
        descriptor: String,
        opcode: Int = Opcodes.INVOKEINTERFACE,
        isInterface: Boolean = true
    ) {
        invokeMethodVisitor.visitMethodInsn(opcode, owner, method, descriptor, isInterface)
        invokeMethodVisitor.visitCheckCast(T_CLASS)
    }

    internal fun loadStringConstant(string: String): Unit = invokeMethodVisitor.visitLdcInsn(string)

    internal companion object {
        private val SIGNATURE_LETTERS: Map<Class<out Any>, String> by lazy {
            mapOf(
                java.lang.Byte::class.java to "B",
                java.lang.Short::class.java to "S",
                java.lang.Integer::class.java to "I",
                java.lang.Long::class.java to "J",
                java.lang.Float::class.java to "F",
                java.lang.Double::class.java to "D"
            )
        }

        private val INLINABLE_NUMBERS: Set<Class<out Any>> by lazy { SIGNATURE_LETTERS.keys }

        internal const val FUNCTIONAL_COMPILED_EXPRESSION_CLASS =
            "scientifik/kmath/asm/FunctionalCompiledExpression"

        internal const val MAP_CLASS = "java/util/Map"
        internal const val OBJECT_CLASS = "java/lang/Object"
        internal const val ALGEBRA_CLASS = "scientifik/kmath/operations/Algebra"
        internal const val SPACE_OPERATIONS_CLASS = "scientifik/kmath/operations/SpaceOperations"
        internal const val STRING_CLASS = "java/lang/String"
        internal const val NUMBER_CLASS = "java/lang/Number"
    }
}
