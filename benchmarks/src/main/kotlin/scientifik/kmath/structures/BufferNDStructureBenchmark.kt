package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val dim = 1000
    val n = 10000

    val bufferedField = NDField.buffered(intArrayOf(dim, dim), DoubleField)
    val specializedField = NDField.real(intArrayOf(dim, dim))
    val genericField = NDField.generic(intArrayOf(dim, dim), DoubleField)

//    val action: NDField<Double, DoubleField, NDStructure<Double>>.() -> Unit = {
//        var res = one
//        repeat(n) {
//            res += 1.0
//        }
//    }



    val doubleTime = measureTimeMillis {

        bufferedField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Buffered addition completed in $doubleTime millis")


    val elementTime = measureTimeMillis {
        var res = bufferedField.produce { one }
        repeat(n) {
            res += 1.0
        }
    }

    println("Element addition completed in $elementTime millis")

    val specializedTime = measureTimeMillis {
        //specializedField.run(action)
        specializedField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Specialized addition completed in $specializedTime millis")


    val genericTime = measureTimeMillis {
        //genericField.run(action)
        genericField.run {
            var res = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Generic addition completed in $genericTime millis")
}