package kscience.kmath.linear

import kscience.kmath.commons.linear.CMMatrixContext
import kscience.kmath.commons.linear.toCM
import kscience.kmath.ejml.EjmlMatrixContext
import kscience.kmath.operations.RealField
import kscience.kmath.operations.invoke
import kscience.kmath.structures.Matrix
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val random = Random(12224)
    val dim = 1000
    //creating invertible matrix
    val matrix1 = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
    val matrix2 = Matrix.real(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }

//    //warmup
//    matrix1 dot matrix2

    CMMatrixContext {
        val cmMatrix1 = matrix1.toCM()
        val cmMatrix2 = matrix2.toCM()
        val cmTime = measureTimeMillis { cmMatrix1 dot cmMatrix2 }
        println("CM implementation time: $cmTime")
    }

    (EjmlMatrixContext(RealField)) {
        val ejmlMatrix1 = matrix1.toEjml()
        val ejmlMatrix2 = matrix2.toEjml()
        val ejmlTime = measureTimeMillis { ejmlMatrix1 dot ejmlMatrix2 }
        println("EJML implementation time: $ejmlTime")
    }

    val genericTime = measureTimeMillis { val res = matrix1 dot matrix2 }
    println("Generic implementation time: $genericTime")
}
