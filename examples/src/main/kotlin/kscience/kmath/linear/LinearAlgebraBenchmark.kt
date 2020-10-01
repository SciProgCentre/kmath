package kscience.kmath.linear

import kscience.kmath.commons.linear.CMMatrixContext
import kscience.kmath.commons.linear.inverse
import kscience.kmath.commons.linear.toCM
import kscience.kmath.ejml.EjmlMatrixContext
import kscience.kmath.ejml.inverse
import kscience.kmath.operations.RealField
import kscience.kmath.operations.invoke
import kscience.kmath.structures.Matrix
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val random = Random(1224)
    val dim = 100
    //creating invertible matrix
    val u = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
    val l = Matrix.real(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
    val matrix = l dot u

    val n = 5000 // iterations

    MatrixContext.real {
        repeat(50) { inverse(matrix) }
        val inverseTime = measureTimeMillis { repeat(n) { inverse(matrix) } }
        println("[kmath] Inversion of $n matrices $dim x $dim finished in $inverseTime millis")
    }

    //commons-math

    val commonsTime = measureTimeMillis {
        CMMatrixContext {
            val cm = matrix.toCM()             //avoid overhead on conversion
            repeat(n) { inverse(cm) }
        }
    }


    println("[commons-math] Inversion of $n matrices $dim x $dim finished in $commonsTime millis")

    val ejmlTime = measureTimeMillis {
        (EjmlMatrixContext(RealField)) {
            val km = matrix.toEjml()      //avoid overhead on conversion
            repeat(n) { inverse(km) }
        }
    }

    println("[ejml] Inversion of $n matrices $dim x $dim finished in $ejmlTime millis")
}
