package scientifik.kmath.asm

import org.objectweb.asm.Type
import scientifik.kmath.asm.internal.AsmBuilder
import scientifik.kmath.asm.internal.buildExpectationStack
import scientifik.kmath.asm.internal.buildName
import scientifik.kmath.asm.internal.tryInvokeSpecific
import scientifik.kmath.ast.MST
import scientifik.kmath.ast.MSTExpression
import scientifik.kmath.expressions.Expression
import scientifik.kmath.operations.Algebra
import scientifik.kmath.operations.NumericAlgebra
import kotlin.reflect.KClass

/**
 * Compile given MST to an Expression using AST compiler
 */
fun <T : Any> MST.compileWith(type: KClass<T>, algebra: Algebra<T>): Expression<T> {
    fun AsmBuilder<T>.visit(node: MST) {
        when (node) {
            is MST.Symbolic -> loadVariable(node.value)

            is MST.Numeric -> {
                val constant = if (algebra is NumericAlgebra<T>)
                    algebra.number(node.value)
                else
                    error("Number literals are not supported in $algebra")

                loadTConstant(constant)
            }

            is MST.Unary -> {
                loadAlgebra()
                if (!buildExpectationStack(algebra, node.operation, 1)) loadStringConstant(node.operation)
                visit(node.value)

                if (!tryInvokeSpecific(algebra, node.operation, 1)) invokeAlgebraOperation(
                    owner = AsmBuilder.ALGEBRA_TYPE.internalName,
                    method = "unaryOperation",

                    descriptor = Type.getMethodDescriptor(
                        AsmBuilder.OBJECT_TYPE,
                        AsmBuilder.STRING_TYPE,
                        AsmBuilder.OBJECT_TYPE
                    ),

                    tArity = 1
                )
            }
            is MST.Binary -> {
                loadAlgebra()
                if (!buildExpectationStack(algebra, node.operation, 2)) loadStringConstant(node.operation)
                visit(node.left)
                visit(node.right)

                if (!tryInvokeSpecific(algebra, node.operation, 2)) invokeAlgebraOperation(
                    owner = AsmBuilder.ALGEBRA_TYPE.internalName,
                    method = "binaryOperation",

                    descriptor = Type.getMethodDescriptor(
                        AsmBuilder.OBJECT_TYPE,
                        AsmBuilder.STRING_TYPE,
                        AsmBuilder.OBJECT_TYPE,
                        AsmBuilder.OBJECT_TYPE
                    ),

                    tArity = 2
                )
            }
        }
    }

    return AsmBuilder(type, algebra, buildName(this)) { visit(this@compileWith) }.getInstance()
}

/**
 * Compile an [MST] to ASM using given algebra
 */
inline fun <reified T : Any> Algebra<T>.expression(mst: MST): Expression<T> = mst.compileWith(T::class, this)

/**
 * Optimize performance of an [MSTExpression] using ASM codegen
 */
inline fun <reified T : Any> MSTExpression<T>.compile(): Expression<T> = mst.compileWith(T::class, algebra)
