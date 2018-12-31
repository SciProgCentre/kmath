package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val dim = 1000
    val n = 1000

    val genericField = NDField.generic(intArrayOf(dim, dim), DoubleField)
    val doubleField = NDField.inline(intArrayOf(dim, dim), DoubleField)
    val specializedField = NDField.real(intArrayOf(dim, dim))


    val doubleTime = measureTimeMillis {
        var res = doubleField.produce { one }
        repeat(n) {
            res += 1.0
        }
    }

    println("Inlined addition completed in $doubleTime millis")

    val specializedTime = measureTimeMillis {
        var res = specializedField.produce { one }
        repeat(n) {
            res += 1.0
        }
    }

    println("Specialized addition completed in $specializedTime millis")


    val genericTime = measureTimeMillis {
        var res = genericField.produce { one }
        repeat(n) {
            res += 1.0
        }
    }

    println("Generic addition completed in $genericTime millis")
}