package scientifik.kmath.structures

import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.toComplex
import kotlin.system.measureTimeMillis

fun main() {
    val dim = 1000
    val n = 1000

    val realField = NDField.real(intArrayOf(dim, dim))
    val complexField = NDField.complex(intArrayOf(dim, dim))


    val realTime = measureTimeMillis {
        realField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Real addition completed in $realTime millis")

    val complexTime = measureTimeMillis {
        complexField.run {
            var res: ComplexNDElement = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Complex addition completed in $complexTime millis")
}