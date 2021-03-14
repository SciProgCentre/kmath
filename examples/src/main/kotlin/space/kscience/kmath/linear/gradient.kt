package space.kscience.kmath.linear

import space.kscience.kmath.real.*
import space.kscience.kmath.structures.RealBuffer

fun main() {
    val x0 = Vector(0.0, 0.0, 0.0)
    val sigma = Vector(1.0, 1.0, 1.0)

    val gaussian: (Vector<Double>) -> Double = { x ->
        require(x.size == x0.size)
        kotlin.math.exp(-((x - x0) / sigma).square().sum())
    }

    fun ((Vector<Double>) -> Double).grad(x: Vector<Double>): Vector<Double> {
        require(x.size == x0.size)
        return RealBuffer(x.size) { i ->
            val h = sigma[i] / 5
            val dVector = RealBuffer(x.size) { if (it == i) h else 0.0 }
            val f1 = invoke(x + dVector / 2)
            val f0 = invoke(x - dVector / 2)
            (f1 - f0) / h
        }
    }

    println(gaussian.grad(x0))

}