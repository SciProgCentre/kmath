package scientifik.kmath.expressions

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space

abstract class AsmCompiled<T>(@JvmField val algebra: Algebra<T>, @JvmField val constants: MutableList<T>) {
    abstract fun evaluate(arguments: Map<String, T>): T
}

class AsmGenerationContext<T>(classOfT: Class<*>, private val algebra: Algebra<T>, private val className: String) {
    private class ClassLoader(parent: java.lang.ClassLoader) : java.lang.ClassLoader(parent) {
        internal fun defineClass(name: String?, b: ByteArray): Class<*> = defineClass(name, b, 0, b.size)
    }

    private val classLoader: ClassLoader = ClassLoader(javaClass.classLoader)

    @Suppress("PrivatePropertyName")
    private val T_ALGEBRA_CLASS: String = algebra.javaClass.name.replace(oldChar = '.', newChar = '/')

    @Suppress("PrivatePropertyName")
    private val T_CLASS: String = classOfT.name.replace('.', '/')

    private val slashesClassName: String = className.replace(oldChar = '.', newChar = '/')
    private val evaluateThisVar: Int = 0
    private val evaluateArgumentsVar: Int = 1
    private var maxStack: Int = 0
    private lateinit var constants: MutableList<T>
    private lateinit var asmCompiledClassWriter: ClassWriter
    private lateinit var evaluateMethodVisitor: MethodVisitor
    private lateinit var evaluateL0: Label
    private lateinit var evaluateL1: Label

    init {
        start()
    }

    fun start() {
        constants = mutableListOf()
        asmCompiledClassWriter = ClassWriter(0)
        maxStack = 0

        asmCompiledClassWriter.visit(
            V1_8,
            ACC_PUBLIC or ACC_FINAL or ACC_SUPER,
            slashesClassName,
            "L$ASM_COMPILED_CLASS<L$T_CLASS;>;",
            ASM_COMPILED_CLASS,
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
                    ASM_COMPILED_CLASS,
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

            evaluateMethodVisitor = visitMethod(
                ACC_PUBLIC or ACC_FINAL,
                "evaluate",
                "(L$MAP_CLASS;)L$T_CLASS;",
                "(L$MAP_CLASS<L$STRING_CLASS;+L$T_CLASS;>;)L$T_CLASS;",
                null
            )

            evaluateMethodVisitor.run {
                visitCode()
                evaluateL0 = Label()
                visitLabel(evaluateL0)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun generate(): AsmCompiled<T> {
        evaluateMethodVisitor.run {
            visitInsn(ARETURN)
            evaluateL1 = Label()
            visitLabel(evaluateL1)

            visitLocalVariable(
                "this",
                "L$slashesClassName;",
                T_CLASS,
                evaluateL0,
                evaluateL1,
                evaluateThisVar
            )

            visitLocalVariable(
                "arguments",
                "L$MAP_CLASS;",
                "L$MAP_CLASS<L$STRING_CLASS;+L$T_CLASS;>;",
                evaluateL0,
                evaluateL1,
                evaluateArgumentsVar
            )

            visitMaxs(maxStack + 1, 2)
            visitEnd()
        }

        asmCompiledClassWriter.visitMethod(
            ACC_PUBLIC or ACC_FINAL or ACC_BRIDGE or ACC_SYNTHETIC,
            "evaluate",
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
            visitMethodInsn(INVOKEVIRTUAL, slashesClassName, "evaluate", "(L$MAP_CLASS;)L$T_CLASS;", false)
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

        return classLoader
            .defineClass(className, asmCompiledClassWriter.toByteArray())
            .constructors
            .first()
            .newInstance(algebra, constants) as AsmCompiled<T>
    }

    fun visitLoadFromConstants(value: T) {
        val idx = if (value in constants) constants.indexOf(value) else constants.apply { add(value) }.lastIndex
        maxStack++

        evaluateMethodVisitor.run {
            visitLoadThis()
            visitFieldInsn(GETFIELD, slashesClassName, "constants", "L$LIST_CLASS;")
            visitLdcOrIConstInsn(idx)
            visitMethodInsn(INVOKEINTERFACE, LIST_CLASS, "get", "(I)L$OBJECT_CLASS;", true)
            visitCastToT()
        }
    }

    private fun visitLoadThis(): Unit = evaluateMethodVisitor.visitVarInsn(ALOAD, evaluateThisVar)

    fun visitNumberConstant(value: Number) {
        maxStack++
        evaluateMethodVisitor.visitBoxedNumberConstant(value)
    }

    fun visitLoadFromVariables(name: String, defaultValue: T? = null) = evaluateMethodVisitor.run {
        maxStack += 2
        visitVarInsn(ALOAD, evaluateArgumentsVar)

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

    fun visitLoadAlgebra() {
        evaluateMethodVisitor.visitVarInsn(ALOAD, evaluateThisVar)

        evaluateMethodVisitor.visitFieldInsn(
            GETFIELD,
            ASM_COMPILED_CLASS, "algebra", "L$ALGEBRA_CLASS;"
        )

        evaluateMethodVisitor.visitTypeInsn(CHECKCAST, T_ALGEBRA_CLASS)
    }

    fun visitAlgebraOperation(owner: String, method: String, descriptor: String) {
        maxStack++
        evaluateMethodVisitor.visitMethodInsn(INVOKEINTERFACE, owner, method, descriptor, true)
        visitCastToT()
    }

    private fun visitCastToT(): Unit = evaluateMethodVisitor.visitTypeInsn(CHECKCAST, T_CLASS)

    companion object {
        const val ASM_COMPILED_CLASS = "scientifik/kmath/expressions/AsmCompiled"
        const val LIST_CLASS = "java/util/List"
        const val MAP_CLASS = "java/util/Map"
        const val OBJECT_CLASS = "java/lang/Object"
        const val ALGEBRA_CLASS = "scientifik/kmath/operations/Algebra"
        const val SPACE_OPERATIONS_CLASS = "scientifik/kmath/operations/SpaceOperations"
        const val STRING_CLASS = "java/lang/String"
        const val FIELD_OPERATIONS_CLASS = "scientifik/kmath/operations/FieldOperations"
        const val RING_OPERATIONS_CLASS = "scientifik/kmath/operations/RingOperations"
        const val NUMBER_CLASS = "java/lang/Number"
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

open class AsmFunctionalExpressionSpace<T>(
    val space: Space<T>,
    one: T
) : Space<AsmExpression<T>>,
    ExpressionSpace<T, AsmExpression<T>> {
    override val zero: AsmExpression<T> =
        AsmConstantExpression(space.zero)

    override fun const(value: T): AsmExpression<T> =
        AsmConstantExpression(value)

    override fun variable(name: String, default: T?): AsmExpression<T> =
        AsmVariableExpression(
            name,
            default
        )

    override fun add(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmSumExpression(a, b)

    override fun multiply(a: AsmExpression<T>, k: Number): AsmExpression<T> =
        AsmConstProductExpression(a, k)


    operator fun AsmExpression<T>.plus(arg: T) = this + const(arg)
    operator fun AsmExpression<T>.minus(arg: T) = this - const(arg)

    operator fun T.plus(arg: AsmExpression<T>) = arg + this
    operator fun T.minus(arg: AsmExpression<T>) = arg - this
}

class AsmFunctionalExpressionField<T>(val field: Field<T>) : ExpressionField<T, AsmExpression<T>>,
    AsmFunctionalExpressionSpace<T>(field, field.one) {
    override val one: AsmExpression<T>
        get() = const(this.field.one)

    override fun const(value: Double): AsmExpression<T> = const(field.run { one * value })

    override fun multiply(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmProductExpression(a, b)

    override fun divide(a: AsmExpression<T>, b: AsmExpression<T>): AsmExpression<T> =
        AsmDivExpression(a, b)

    operator fun AsmExpression<T>.times(arg: T) = this * const(arg)
    operator fun AsmExpression<T>.div(arg: T) = this / const(arg)

    operator fun T.times(arg: AsmExpression<T>) = arg * this
    operator fun T.div(arg: AsmExpression<T>) = arg / this
}
