package scientifik.kmath.structures

import kotlin.system.measureTimeMillis

fun main() {
    val dim = 1000
    val n = 1000

    val realField = NDField.real(dim, dim)
    val complexField = NDField.complex(dim, dim)


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
            var res = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Complex addition completed in $complexTime millis")
}