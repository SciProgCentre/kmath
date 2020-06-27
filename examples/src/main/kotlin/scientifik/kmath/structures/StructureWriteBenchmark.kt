package scientifik.kmath.structures

import kotlin.system.measureTimeMillis


fun main(args: Array<String>) {

    val n = 6000

    val structure = NDStructure.build(intArrayOf(n, n), Buffer.Companion::auto) { 1.0 }

    structure.mapToBuffer { it + 1 } // warm-up

    val time1 = measureTimeMillis {
        val res = structure.mapToBuffer { it + 1 }
    }
    println("Structure mapping finished in $time1 millis")

    val array = DoubleArray(n * n) { 1.0 }

    val time2 = measureTimeMillis {
        val target = DoubleArray(n * n)
        val res = array.forEachIndexed { index, value ->
            target[index] = value + 1
        }
    }
    println("Array mapping finished in $time2 millis")

    val buffer = RealBuffer(DoubleArray(n * n) { 1.0 })

    val time3 = measureTimeMillis {
        val target = RealBuffer(DoubleArray(n * n))
        val res = array.forEachIndexed { index, value ->
            target[index] = value + 1
        }
    }
    println("Buffer mapping finished in $time3 millis")
}