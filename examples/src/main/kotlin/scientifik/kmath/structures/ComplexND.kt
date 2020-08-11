package scientifik.kmath.structures

import scientifik.kmath.linear.transpose
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import scientifik.kmath.operations.invoke
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
            var res: NDBuffer<Complex> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Complex addition completed in $complexTime millis")
}


fun complexExample() {
    //Create a context for 2-d structure with complex values
    ComplexField {
        nd(4, 8) {
            //a constant real-valued structure
            val x = one * 2.5
            operator fun Number.plus(other: Complex) = Complex(this.toDouble() + other.re, other.im)
            //a structure generator specific to this context
            val matrix = produce { (k, l) ->
                k + l * i
            }

            //Perform sum
            val sum = matrix + x + 1.0

            //Represent the sum as 2d-structure and transpose
            sum.as2D().transpose()
        }
    }
}
