package kscience.kmath.ast

import kscience.kmath.asm.compile
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.expressionInField
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.Field
import kscience.kmath.operations.RealField
import kotlin.random.Random
import kotlin.system.measureTimeMillis

internal class ExpressionsInterpretersBenchmark {
    private val algebra: Field<Double> = RealField
    fun functionalExpression() {
        val expr = algebra.expressionInField {
            symbol("x") * const(2.0) + const(2.0) / symbol("x") - const(16.0)
        }

        invokeAndSum(expr)
    }

    fun mstExpression() {
        val expr = algebra.mstInField {
            symbol("x") * number(2.0) + number(2.0) / symbol("x") - number(16.0)
        }

        invokeAndSum(expr)
    }

    fun asmExpression() {
        val expr = algebra.mstInField {
            symbol("x") * number(2.0) + number(2.0) / symbol("x") - number(16.0)
        }.compile()

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

/**
 * This benchmark compares basically evaluation of simple function with MstExpression interpreter, ASM backend and
 * core FunctionalExpressions API.
 *
 * The expected rating is:
 *
 * 1. ASM.
 * 2. MST.
 * 3. FE.
 */
fun main() {
    val benchmark = ExpressionsInterpretersBenchmark()

    val fe = measureTimeMillis {
        benchmark.functionalExpression()
    }

    println("fe=$fe")

    val mst = measureTimeMillis {
        benchmark.mstExpression()
    }

    println("mst=$mst")

    val asm = measureTimeMillis {
        benchmark.asmExpression()
    }

    println("asm=$asm")
}
