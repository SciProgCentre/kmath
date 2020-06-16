package scientifik.kmath.asm

import scientifik.kmath.asm.internal.AsmBuilder
import scientifik.kmath.asm.internal.hasSpecific
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

    fun buildName(mst: MST, collision: Int = 0): String {
        val name = "scientifik.kmath.expressions.generated.AsmCompiledExpression_${mst.hashCode()}_$collision"

        try {
            Class.forName(name)
        } catch (ignored: ClassNotFoundException) {
            return name
        }

        return buildName(mst, collision + 1)
    }

    fun AsmBuilder<T>.visit(node: MST): Unit {
        when (node) {
            is MST.Symbolic -> visitLoadFromVariables(node.value)
            is MST.Numeric -> {
                val constant = if (algebra is NumericAlgebra<T>) {
                    algebra.number(node.value)
                } else {
                    error("Number literals are not supported in $algebra")
                }
                visitLoadFromConstants(constant)
            }
            is MST.Unary -> {
                visitLoadAlgebra()

                if (!hasSpecific(algebra, node.operation, 1)) visitStringConstant(node.operation)

                visit(node.value)

                if (!tryInvokeSpecific(algebra, node.operation, 1)) {
                    visitAlgebraOperation(
                        owner = AsmBuilder.ALGEBRA_CLASS,
                        method = "unaryOperation",
                        descriptor = "(L${AsmBuilder.STRING_CLASS};" +
                                "L${AsmBuilder.OBJECT_CLASS};)" +
                                "L${AsmBuilder.OBJECT_CLASS};"
                    )
                }
            }
            is MST.Binary -> {
                visitLoadAlgebra()

                if (!hasSpecific(algebra, node.operation, 2))
                    visitStringConstant(node.operation)

                visit(node.left)
                visit(node.right)

                if (!tryInvokeSpecific(algebra, node.operation, 2)) {

                    visitAlgebraOperation(
                        owner = AsmBuilder.ALGEBRA_CLASS,
                        method = "binaryOperation",
                        descriptor = "(L${AsmBuilder.STRING_CLASS};" +
                                "L${AsmBuilder.OBJECT_CLASS};" +
                                "L${AsmBuilder.OBJECT_CLASS};)" +
                                "L${AsmBuilder.OBJECT_CLASS};"
                    )
                }
            }
        }
    }

    val builder = AsmBuilder(type.java, algebra, buildName(this))
    builder.visit(this)
    return builder.generate()
}

inline fun <reified T : Any> Algebra<T>.compile(mst: MST): Expression<T> = mst.compileWith(T::class, this)

/**
 * Optimize performance of an [MSTExpression] using ASM codegen
 */
inline fun <reified T : Any> MSTExpression<T>.compile(): Expression<T> = mst.compileWith(T::class, algebra)

//inline fun <reified T : Any, A : Algebra<T>, E : Algebra<MST>> A.asm(
//    mstAlgebra: E,
//    block: E.() -> MST
//): Expression<T> = mstAlgebra.block().compileWith(T::class, this)
//
//inline fun <reified T : Any, A : Space<T>> A.asmInSpace(block: MSTSpace.() -> MST): Expression<T> =
//    MSTSpace.block().compileWith(T::class, this)
//
//inline fun <reified T : Any, A : Ring<T>> A.asmInRing(block: MSTRing.() -> MST): Expression<T> =
//    MSTRing.block().compileWith(T::class, this)
//
//inline fun <reified T : Any, A : Field<T>> A.asmInField(block: MSTField.() -> MST): Expression<T> =
//    MSTField.block().compileWith(T::class, this)
