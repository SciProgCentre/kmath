package scientifik.kmath.expressions

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space

abstract class AsmCompiledExpression<T> internal constructor(
    @JvmField val algebra: Algebra<T>,
    @JvmField val constants: MutableList<out Any>
) : Expression<T> {
    abstract override fun invoke(arguments: Map<String, T>): T
}

internal fun buildName(expression: AsmExpression<*>, collision: Int = 0): String {
    val name = "scientifik.kmath.expressions.generated.AsmCompiledExpression_${expression.hashCode()}_$collision"

    try {
        Class.forName(name)
    } catch (ignored: ClassNotFoundException) {
        return name
    }

    return buildName(expression, collision + 1)
}

class AsmGenerationContext<T>(
    classOfT: Class<*>,
    private val algebra: Algebra<T>,
    private val className: String
) {
    private class ClassLoader(parent: java.lang.ClassLoader) : java.lang.ClassLoader(parent) {
        internal fun defineClass(name: String?, b: ByteArray): Class<*> = defineClass(name, b, 0, b.size)
    }

    private val classLoader: ClassLoader = ClassLoader(javaClass.classLoader)

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
    private var generatedInstance: AsmCompiledExpression<T>? = null

    init {
        asmCompiledClassWriter.visit(
            V1_8,
            ACC_PUBLIC or ACC_FINAL or ACC_SUPER,
            slashesClassName,
            "L$ASM_COMPILED_EXPRESSION_CLASS<L$T_CLASS;>;",
            ASM_COMPILED_EXPRESSION_CLASS,
            arrayOf()
        )

        asmCompiledClassWriter.run {
            visitMethod(ACC_PUBLIC, "<init>", "(L$ALGEBRA_CLASS;L$LIST_CLASS;)V", null, null).run {
                val thisVar = 0
                val algebraVar = 1
                val constantsVar = 2
                val l0 = Label()
                visitLabel(l0)
                visitVarInsn(ALOAD, thisVar)
                visitVarInsn(ALOAD, algebraVar)
                visitVarInsn(ALOAD, constantsVar)

                visitMethodInsn(
                    INVOKESPECIAL,
                    ASM_COMPILED_EXPRESSION_CLASS,
                    "<init>",
                    "(L$ALGEBRA_CLASS;L$LIST_CLASS;)V",
                    false
                )

                val l1 = Label()
                visitLabel(l1)
                visitInsn(RETURN)
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

                visitLocalVariable("constants", "L$LIST_CLASS;", "L$LIST_CLASS<L$T_CLASS;>;", l0, l2, constantsVar)
                visitMaxs(3, 3)
                visitEnd()
            }

            invokeMethodVisitor = visitMethod(
                ACC_PUBLIC or ACC_FINAL,
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
    internal fun generate(): AsmCompiledExpression<T> {
        generatedInstance?.let { return it }

        invokeMethodVisitor.run {
            visitInsn(ARETURN)
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
            ACC_PUBLIC or ACC_FINAL or ACC_BRIDGE or ACC_SYNTHETIC,
            "invoke",
            "(L$MAP_CLASS;)L$OBJECT_CLASS;",
            null,
            null
        ).run {
            val thisVar = 0
            visitCode()
            val l0 = Label()
            visitLabel(l0)
            visitVarInsn(ALOAD, 0)
            visitVarInsn(ALOAD, 1)
            visitMethodInsn(INVOKEVIRTUAL, slashesClassName, "invoke", "(L$MAP_CLASS;)L$T_CLASS;", false)
            visitInsn(ARETURN)
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
            .newInstance(algebra, constants) as AsmCompiledExpression<T>

        generatedInstance = new
        return new
    }

    internal fun visitLoadFromConstants(value: T) = visitLoadAnyFromConstants(value as Any, T_CLASS)

    private fun visitLoadAnyFromConstants(value: Any, type: String) {
        val idx = if (value in constants) constants.indexOf(value) else constants.apply { add(value) }.lastIndex
        maxStack++

        invokeMethodVisitor.run {
            visitLoadThis()
            visitFieldInsn(GETFIELD, slashesClassName, "constants", "L$LIST_CLASS;")
            visitLdcOrIConstInsn(idx)
            visitMethodInsn(INVOKEINTERFACE, LIST_CLASS, "get", "(I)L$OBJECT_CLASS;", true)
            invokeMethodVisitor.visitTypeInsn(CHECKCAST, type)
        }
    }

    private fun visitLoadThis(): Unit = invokeMethodVisitor.visitVarInsn(ALOAD, invokeThisVar)

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

            invokeMethodVisitor.visitMethodInsn(INVOKESTATIC, c, "valueOf", "($sigLetter)L${c};", false)
            return
        }

        visitLoadAnyFromConstants(value, c)
    }

    internal fun visitLoadFromVariables(name: String, defaultValue: T? = null) = invokeMethodVisitor.run {
        maxStack += 2
        visitVarInsn(ALOAD, invokeArgumentsVar)

        if (defaultValue != null) {
            visitLdcInsn(name)
            visitLoadFromConstants(defaultValue)

            visitMethodInsn(
                INVOKEINTERFACE,
                MAP_CLASS,
                "getOrDefault",
                "(L$OBJECT_CLASS;L$OBJECT_CLASS;)L$OBJECT_CLASS;",
                true
            )

            visitCastToT()
            return
        }

        visitLdcInsn(name)
        visitMethodInsn(INVOKEINTERFACE, MAP_CLASS, "get", "(L$OBJECT_CLASS;)L$OBJECT_CLASS;", true)
        visitCastToT()
    }

    internal fun visitLoadAlgebra() {
        invokeMethodVisitor.visitVarInsn(ALOAD, invokeThisVar)

        invokeMethodVisitor.visitFieldInsn(
            GETFIELD,
            ASM_COMPILED_EXPRESSION_CLASS, "algebra", "L$ALGEBRA_CLASS;"
        )

        invokeMethodVisitor.visitTypeInsn(CHECKCAST, T_ALGEBRA_CLASS)
    }

    internal fun visitAlgebraOperation(owner: String, method: String, descriptor: String) {
        maxStack++
        invokeMethodVisitor.visitMethodInsn(INVOKEINTERFACE, owner, method, descriptor, true)
        visitCastToT()
    }

    private fun visitCastToT(): Unit = invokeMethodVisitor.visitTypeInsn(CHECKCAST, T_CLASS)

    internal companion object {
        private val SIGNATURE_LETTERS = mapOf(
            java.lang.Byte::class.java to "B",
            java.lang.Short::class.java to "S",
            java.lang.Integer::class.java to "I",
            java.lang.Long::class.java to "J",
            java.lang.Float::class.java to "F",
            java.lang.Double::class.java to "D"
        )

        internal const val ASM_COMPILED_EXPRESSION_CLASS = "scientifik/kmath/expressions/AsmCompiledExpression"
        internal const val LIST_CLASS = "java/util/List"
        internal const val MAP_CLASS = "java/util/Map"
        internal const val OBJECT_CLASS = "java/lang/Object"
        internal const val ALGEBRA_CLASS = "scientifik/kmath/operations/Algebra"
        internal const val SPACE_OPERATIONS_CLASS = "scientifik/kmath/operations/SpaceOperations"
        internal const val STRING_CLASS = "java/lang/String"
        internal const val FIELD_OPERATIONS_CLASS = "scientifik/kmath/operations/FieldOperations"
        internal const val RING_OPERATIONS_CLASS = "scientifik/kmath/operations/RingOperations"
        internal const val NUMBER_CLASS = "java/lang/Number"
    }
}

interface AsmExpression<T> {
    fun invoke(gen: AsmGenerationContext<T>)
}

internal class AsmVariableExpression<T>(val name: String, val default: T? = null) :
    AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromVariables(name, default)
}

internal class AsmConstantExpression<T>(val value: T) :
    AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>): Unit = gen.visitLoadFromConstants(value)
}

internal class AsmSumExpression<T>(
    val first: AsmExpression<T>,
    val second: AsmExpression<T>
) : AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        first.invoke(gen)
        second.invoke(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.SPACE_OPERATIONS_CLASS,
            method = "add",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};L${AsmGenerationContext.OBJECT_CLASS};)L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmProductExpression<T>(
    val first: AsmExpression<T>,
    val second: AsmExpression<T>
) : AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        first.invoke(gen)
        second.invoke(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.RING_OPERATIONS_CLASS,
            method = "multiply",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};L${AsmGenerationContext.OBJECT_CLASS};)L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmConstProductExpression<T>(
    val expr: AsmExpression<T>,
    val const: Number
) : AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        gen.visitNumberConstant(const)
        expr.invoke(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.SPACE_OPERATIONS_CLASS,
            method = "multiply",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};L${AsmGenerationContext.NUMBER_CLASS};)L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

internal class AsmDivExpression<T>(
    val expr: AsmExpression<T>,
    val second: AsmExpression<T>
) : AsmExpression<T> {
    override fun invoke(gen: AsmGenerationContext<T>) {
        gen.visitLoadAlgebra()
        expr.invoke(gen)
        second.invoke(gen)

        gen.visitAlgebraOperation(
            owner = AsmGenerationContext.FIELD_OPERATIONS_CLASS,
            method = "divide",
            descriptor = "(L${AsmGenerationContext.OBJECT_CLASS};L${AsmGenerationContext.OBJECT_CLASS};)L${AsmGenerationContext.OBJECT_CLASS};"
        )
    }
}

open class AsmExpressionSpace<T>(space: Space<T>) : Space<AsmExpression<T>>,
    ExpressionSpace<T, AsmExpression<T>> {
    override val zero: AsmExpression<T> = AsmConstantExpression(space.zero)
    override fun const(value: T): AsmExpression<T> = AsmConstantExpression(value)
    override fun variable(name: String, default: T?): AsmExpression<T> = AsmVariableExpression(name, default)
    override fun add(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> = AsmSumExpression(a, b)
    override fun multiply(a: AsmExpression<T>, k: Number): AsmExpression<T> = AsmConstProductExpression(a, k)
    operator fun AsmExpression<T>.plus(arg: T) = this + const(arg)
    operator fun AsmExpression<T>.minus(arg: T) = this - const(arg)
    operator fun T.plus(arg: AsmExpression<T>) = arg + this
    operator fun T.minus(arg: AsmExpression<T>) = arg - this
}

class AsmExpressionField<T>(private val field: Field<T>) : ExpressionField<T, AsmExpression<T>>,
    AsmExpressionSpace<T>(field) {
    override val one: AsmExpression<T>
        get() = const(this.field.one)

    override fun number(value: Number): AsmExpression<T> = const(field.run { one * value })

    override fun multiply(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmProductExpression(a, b)

    override fun divide(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmDivExpression(a, b)

    operator fun AsmExpression<T>.times(arg: T) = this * const(arg)
    operator fun AsmExpression<T>.div(arg: T) = this / const(arg)
    operator fun T.times(arg: AsmExpression<T>) = arg * this
    operator fun T.div(arg: AsmExpression<T>) = arg / this
}
