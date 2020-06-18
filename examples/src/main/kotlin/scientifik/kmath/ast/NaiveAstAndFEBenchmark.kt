package scientifik.kmath.ast

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import scientifik.kmath.asm.compile
import scientifik.kmath.expressions.expressionInField
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.RealField
import java.time.Duration
import java.time.Instant
import kotlin.random.Random

fun main() = runBlocking(Dispatchers.Default) {
    val mstJob = async { runMst() }
    val asmJob = async { runAsm() }
    val feJob = async { runFE() }
    println("ASM: ${asmJob.await()}")
    println("FE: ${feJob.await()}")
    println("MST: ${mstJob.await()}")
}

fun runFE(): Any {
    val startTime = Instant.now()!!
    val rand = Random(System.currentTimeMillis())
    var sum = 0.0

    val expr =
        RealField.expressionInField { ((variable("x") * number(2.0) - 234) + 24.toByte()) * variable("x") * variable("x") }

    repeat(10_000_000) { sum += expr("x" to rand.nextDouble()) }
    println("asm-fe = $sum")
    return Duration.between(startTime, Instant.now())
}

fun runAsm(): Any {
    val startTime = Instant.now()!!
    val rand = Random(System.currentTimeMillis())
    var sum = 0.0
    val expr = RealField.mstInField { ((symbol("x") * number(2.0) - 234) + 24.0) * symbol("x") * symbol("x") }.compile()
    repeat(10_000_000) { sum += expr("x" to rand.nextDouble()) }
    println("asm-sum = $sum")
    return Duration.between(startTime, Instant.now())
}

fun runMst(): Any {
    val startTime = Instant.now()!!
    val rand = Random(System.currentTimeMillis())
    var sum = 0.0
    val expr = RealField.mstInField { ((symbol("x") * number(2.0) - 234) + 24.0) * symbol("x") * symbol("x") }
    repeat(10_000_000) { sum += expr("x" to rand.nextDouble()) }
    println("asm-mst = $sum")
    return Duration.between(startTime, Instant.now())
}

