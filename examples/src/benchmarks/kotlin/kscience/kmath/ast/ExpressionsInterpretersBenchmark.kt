package kscience.kmath.ast

import kscience.kmath.asm.compile
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.expressionInField
import kscience.kmath.expressions.invoke
import kscience.kmath.expressions.symbol
import kscience.kmath.operations.Field
import kscience.kmath.operations.RealField
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import kotlin.random.Random

@State(Scope.Benchmark)
internal class ExpressionsInterpretersBenchmark {
    private val algebra: Field<Double> = RealField

    @Benchmark
    fun functionalExpression() {
        val expr = algebra.expressionInField {
            symbol("x") * const(2.0) + const(2.0) / symbol("x") - const(16.0)
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

    @Benchmark
    fun rawExpression() {
        val x by symbol
        val expr = Expression<Double> { args -> args.getValue(x) * 2.0 + 2.0 / args.getValue(x) - 16.0 }
        invokeAndSum(expr)
    }

    private fun invokeAndSum(expr: Expression<Double>) {
        val random = Random(0)
        var sum = 0.0

        repeat(1000000) {
            sum += expr("x" to random.nextDouble())
        }

        println(sum)
    }
}
