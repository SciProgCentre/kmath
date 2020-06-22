package scientifik.kmath.ast

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import scientifik.kmath.asm.compile
import scientifik.kmath.expressions.Expression
import scientifik.kmath.expressions.expressionInField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import kotlin.random.Random

@State(Scope.Benchmark)
class ExpressionsInterpretersBenchmark {
    private val algebra: Field<Double> = RealField
    private val random: Random = Random(1)

    @Benchmark
    fun functionalExpression() {
        val expr = algebra.expressionInField {
            variable("x") * const(2.0) + const(2.0) / variable("x") - const(16.0)
        }

        invokeAndSum(expr)
    }

    @Benchmark
    fun mstExpression() {
        val expr = algebra.mstInField {
            symbol("x") * number(2.0) + number(2.0) / symbol("x") - number(16.0)
        }

        invokeAndSum(expr)
    }

    @Benchmark
    fun asmExpression() {
        val expr = algebra.mstInField {
            symbol("x") * number(2.0) + number(2.0) / symbol("x") - number(16.0)
        }.compile()

        invokeAndSum(expr)
    }

    private fun invokeAndSum(expr: Expression<Double>) {
        var sum = 0.0

        repeat(1000000) {
            sum += expr("x" to random.nextDouble())
        }

        println(sum)
    }
}
