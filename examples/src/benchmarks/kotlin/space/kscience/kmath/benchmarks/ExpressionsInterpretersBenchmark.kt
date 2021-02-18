package space.kscience.kmath.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.asm.compile
import space.kscience.kmath.ast.mstInField
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.expressionInField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.Field
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.bindSymbol
import kotlin.random.Random

@State(Scope.Benchmark)
internal class ExpressionsInterpretersBenchmark {
    private val algebra: Field<Double> = RealField
    val x by symbol

    @Benchmark
    fun functionalExpression() {
        val expr = algebra.expressionInField {
            val x = bindSymbol(x)
            x * const(2.0) + const(2.0) / x - const(16.0)
        }

        invokeAndSum(expr)
    }

    @Benchmark
    fun mstExpression() {
        val expr = algebra.mstInField {
            val x = bindSymbol(x)
            x * 2.0 + 2.0 / x - 16.0
        }

        invokeAndSum(expr)
    }

    @Benchmark
    fun asmExpression() {
        val expr = algebra.mstInField {
            val x = bindSymbol(x)
            x * 2.0 + 2.0 / x - 16.0
        }.compile()

        invokeAndSum(expr)
    }

    @Benchmark
    fun rawExpression() {
        val expr = Expression<Double> { args ->
            val x = args.getValue(x)
            x * 2.0 + 2.0 / x - 16.0
        }
        invokeAndSum(expr)
    }

    private fun invokeAndSum(expr: Expression<Double>) {
        val random = Random(0)
        var sum = 0.0

        repeat(1000000) {
            sum += expr(x to random.nextDouble())
        }

        println(sum)
    }
}
