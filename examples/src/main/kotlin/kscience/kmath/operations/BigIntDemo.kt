package kscience.kmath.operations

fun main() {
    val res = BigIntField { number(1) * 2 }
    println("bigint:$res")
}