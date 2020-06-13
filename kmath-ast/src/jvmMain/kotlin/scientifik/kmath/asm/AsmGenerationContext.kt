package scientifik.kmath.asm

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import scientifik.kmath.asm.AsmGenerationContext.ClassLoader
import scientifik.kmath.asm.internal.visitLdcOrDConstInsn
import scientifik.kmath.asm.internal.visitLdcOrFConstInsn
import scientifik.kmath.asm.internal.visitLdcOrIConstInsn
import scientifik.kmath.operations.Algebra

/**
 * AsmGenerationContext is a structure that abstracts building a class that unwraps [AsmExpression] to plain Java
 * expression. This class uses [ClassLoader] for loading the generated class, then it is able to instantiate the new
 * class.
 *
 * @param T the type of AsmExpression to unwrap.
 * @param algebra the algebra the applied AsmExpressions use.
 * @param className the unique class name of new loaded class.
 */
class AsmGenerationContext<T> @PublishedApi internal constructor(
    private val classOfT: Class<*>,
    private val algebra: Algebra<T>,
    private val className: String
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
    private var maxStack: Int = 0
    private val constants: MutableList<Any> = mutableListOf()
    private val asmCompiledClassWriter: ClassWriter = ClassWriter(0)
    private val invokeMethodVisitor: MethodVisitor
    private val invokeL0: Label
    private lateinit var invokeL1: Label
    private var generatedInstance: FunctionalCompiledExpression<T>? = null

    init {
        asmCompiledClassWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
            slashesClassName,
            "L$FUNCTIONAL_COMPILED_EXPRESSION_CLASS<L$T_CLASS;>;",
            FUNCTIONAL_COMPILED_EXPRESSION_CLASS,
            arrayOf()
        )

        asmCompiledClassWriter.run {
            visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(L$ALGEBRA_CLASS;[L$OBJECT_CLASS;)V", null, null).run {
                val thisVar = 0
                val algebraVar = 1
                val constantsVar = 2
                val l0 = Label()
                visitLabel(l0)
                visitVarInsn(Opcodes.ALOAD, thisVar)
                visitVarInsn(Opcodes.ALOAD, algebraVar)
                visitVarInsn(Opcodes.ALOAD, constantsVar)

                visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    FUNCTIONAL_COMPILED_EXPRESSION_CLASS,
                    "<init>",
                    "(L$ALGEBRA_CLASS;[L$OBJECT_CLASS;)V",
                    false
                )

                val l1 = Label()
                visitLabel(l1)
                visitInsn(Opcodes.RETURN)
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
                visitMaxs(3, 3)
                visitEnd()
            }

            invokeMethodVisitor = visitMethod(
                Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
                "invoke",
                "(L$MAP_CLASS;)L$T_CLASS;",
                "(L$MAP_CLASS<L$STRING_CLASS;+L$T_CLASS;>;)L$T_CLASS;",
                null
            )

            invokeMethodVisitor.run {
                visitCode()
                invokeL0 = Label()
                visitLabel(invokeL0)
            }
        }
    }

    @PublishedApi
    @Suppress("UNCHECKED_CAST")
    internal fun generate(): FunctionalCompiledExpression<T> {
        generatedInstance?.let { return it }

        invokeMethodVisitor.run {
            visitInsn(Opcodes.ARETURN)
            invokeL1 = Label()
            visitLabel(invokeL1)

            visitLocalVariable(
                "this",
                "L$slashesClassName;",
                T_CLASS,
                invokeL0,
                invokeL1,
                invokeThisVar
            )

            visitLocalVariable(
                "arguments",
                "L$MAP_CLASS;",
                "L$MAP_CLASS<L$STRING_CLASS;+L$T_CLASS;>;",
                invokeL0,
                invokeL1,
                invokeArgumentsVar
            )

            visitMaxs(maxStack + 1, 2)
            visitEnd()
        }

        asmCompiledClassWriter.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_BRIDGE or Opcodes.ACC_SYNTHETIC,
            "invoke",
            "(L$MAP_CLASS;)L$OBJECT_CLASS;",
            null,
            null
        ).run {
            val thisVar = 0
            visitCode()
            val l0 = Label()
            visitLabel(l0)
            visitVarInsn(Opcodes.ALOAD, 0)
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, slashesClassName, "invoke", "(L$MAP_CLASS;)L$T_CLASS;", false)
            visitInsn(Opcodes.ARETURN)
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

            visitMaxs(2, 2)
            visitEnd()
        }

        asmCompiledClassWriter.visitEnd()

        val new = classLoader
            .defineClass(className, asmCompiledClassWriter.toByteArray())
            .constructors
            .first()
            .newInstance(algebra, constants.toTypedArray()) as FunctionalCompiledExpression<T>

        generatedInstance = new
        return new
    }

    internal fun visitLoadFromConstants(value: T) {
        if (classOfT in INLINABLE_NUMBERS) {
            visitNumberConstant(value as Number)
            visitCastToT()
            return
        }

        visitLoadAnyFromConstants(value as Any, T_CLASS)
    }

    private fun visitLoadAnyFromConstants(value: Any, type: String) {
        val idx = if (value in constants) constants.indexOf(value) else constants.apply { add(value) }.lastIndex
        maxStack++

        invokeMethodVisitor.run {
            visitLoadThis()
            visitFieldInsn(Opcodes.GETFIELD, slashesClassName, "constants", "[L$OBJECT_CLASS;")
            visitLdcOrIConstInsn(idx)
            visitInsn(Opcodes.AALOAD)
            invokeMethodVisitor.visitTypeInsn(Opcodes.CHECKCAST, type)
        }
    }

    private fun visitLoadThis(): Unit = invokeMethodVisitor.visitVarInsn(Opcodes.ALOAD, invokeThisVar)

    internal fun visitNumberConstant(value: Number) {
        maxStack++
        val clazz = value.javaClass
        val c = clazz.name.replace('.', '/')
        val sigLetter = SIGNATURE_LETTERS[clazz]

        if (sigLetter != null) {
            when (value) {
                is Int -> invokeMethodVisitor.visitLdcOrIConstInsn(value)
                is Double -> invokeMethodVisitor.visitLdcOrDConstInsn(value)
                is Float -> invokeMethodVisitor.visitLdcOrFConstInsn(value)
                else -> invokeMethodVisitor.visitLdcInsn(value)
            }

            invokeMethodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, c, "valueOf", "($sigLetter)L${c};", false)
            return
        }

        visitLoadAnyFromConstants(value, c)
    }

    internal fun visitLoadFromVariables(name: String, defaultValue: T? = null): Unit = invokeMethodVisitor.run {
        maxStack += 2
        visitVarInsn(Opcodes.ALOAD, invokeArgumentsVar)

        if (defaultValue != null) {
            visitLdcInsn(name)
            visitLoadFromConstants(defaultValue)

            visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                MAP_CLASS,
                "getOrDefault",
                "(L$OBJECT_CLASS;L$OBJECT_CLASS;)L$OBJECT_CLASS;",
                true
            )

            visitCastToT()
            return
        }

        visitLdcInsn(name)
        visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            MAP_CLASS, "get", "(L$OBJECT_CLASS;)L$OBJECT_CLASS;", true
        )
        visitCastToT()
    }

    internal fun visitLoadAlgebra() {
        maxStack++
        invokeMethodVisitor.visitVarInsn(Opcodes.ALOAD, invokeThisVar)

        invokeMethodVisitor.visitFieldInsn(
            Opcodes.GETFIELD,
            FUNCTIONAL_COMPILED_EXPRESSION_CLASS, "algebra", "L$ALGEBRA_CLASS;"
        )

        invokeMethodVisitor.visitTypeInsn(Opcodes.CHECKCAST, T_ALGEBRA_CLASS)
    }

    internal fun visitAlgebraOperation(
        owner: String,
        method: String,
        descriptor: String,
        opcode: Int = Opcodes.INVOKEINTERFACE,
        isInterface: Boolean = true
    ) {
        maxStack++
        invokeMethodVisitor.visitMethodInsn(opcode, owner, method, descriptor, isInterface)
        visitCastToT()
    }

    private fun visitCastToT(): Unit = invokeMethodVisitor.visitTypeInsn(Opcodes.CHECKCAST, T_CLASS)

    internal fun visitStringConstant(string: String) {
        invokeMethodVisitor.visitLdcInsn(string)
    }

    internal companion object {
        private val SIGNATURE_LETTERS by lazy {
            mapOf(
                java.lang.Byte::class.java to "B",
                java.lang.Short::class.java to "S",
                java.lang.Integer::class.java to "I",
                java.lang.Long::class.java to "J",
                java.lang.Float::class.java to "F",
                java.lang.Double::class.java to "D"
            )
        }

        private val INLINABLE_NUMBERS by lazy { SIGNATURE_LETTERS.keys }

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
