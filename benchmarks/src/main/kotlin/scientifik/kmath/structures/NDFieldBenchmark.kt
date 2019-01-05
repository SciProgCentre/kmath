package scientifik.kmath.structures

import scientifik.kmath.operations.RealField
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val dim = 1000
    val n = 1000

    val bufferedField = NDField.auto(intArrayOf(dim, dim), RealField)
    val specializedField = NDField.real(intArrayOf(dim, dim))
    val genericField = NDField.generic(intArrayOf(dim, dim), RealField)
    val lazyNDField = NDField.lazy(intArrayOf(dim, dim), RealField)

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


    val lazyTime = measureTimeMillis {
        val tr : RealField.(Double)->Double = {arg->
            var r = arg
            repeat(n) {
                r += 1.0
            }
            r
        }
        lazyNDField.run {
            val res = one.map(tr)

            res.elements().sumByDouble { it.second }
        }
    }

    println("Lazy addition completed in $lazyTime millis")

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