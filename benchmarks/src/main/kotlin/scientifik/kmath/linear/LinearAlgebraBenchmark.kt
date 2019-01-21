package scientifik.kmath.linear

import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val random = Random(12224)
    val dim = 100
    //creating invertible matrix
    val u = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
    val l = Matrix.real(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
    val matrix = l dot u

    val n = 500 // iterations

    val solver = LUSolver.real

    repeat(50) {
        val res = solver.inverse(matrix)
    }

    val inverseTime = measureTimeMillis {
        repeat(n) {
            val res = solver.inverse(matrix)
        }
    }

    println("[kmath] Inversion of $n matrices $dim x $dim finished in $inverseTime millis")

    //commons-math

    val cmSolver = CMMatrixContext

    val commonsTime = measureTimeMillis {
        val cm = matrix.toCM()             //avoid overhead on conversion
        repeat(n) {
            val res = cmSolver.inverse(cm)
        }
    }


    println("[commons-math] Inversion of $n matrices $dim x $dim finished in $commonsTime millis")
}