package kscience.kmath.ast

import kscience.kmath.expressions.invoke
import kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.time.measureTime

internal class Test {
    @Test
    fun c() {
        measureTime {
            val expr = compileMstToWasmF64(MstExtendedField { sin(symbol("x")) + cos(symbol("x")).pow(2) })
            println(expr("x" to 3.0))
        }.also { println(it) }
    }
}
