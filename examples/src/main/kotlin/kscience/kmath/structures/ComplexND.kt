package kscience.kmath.structures

import kscience.kmath.complex.Complex
import kscience.kmath.complex.ComplexField
import kscience.kmath.complex.complex
import kscience.kmath.complex.nd
import kscience.kmath.linear.transpose
import kscience.kmath.operations.invoke
import kotlin.system.measureTimeMillis

fun main() {
    val dim = 1000
    val n = 1000

    val realField = NDField.real(dim, dim)
    val complexField = NDField.complex(dim, dim)

    val realTime = measureTimeMillis {
        realField {
            var res: NDBuffer<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    println("Real addition completed in $realTime millis")

    val complexTime = measureTimeMillis {
        complexField {
            var res: NDBuffer<Complex> = one
            repeat(n) { res += 1.0 }
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
            //a structure generator specific to this context
            val matrix = produce { (k, l) -> k + l * i }
            //Perform sum
            val sum = matrix + x + 1.0

            //Represent the sum as 2d-structure and transpose
            sum.as2D().transpose()
        }
    }
}
