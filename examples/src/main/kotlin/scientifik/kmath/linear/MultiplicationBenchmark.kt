package scientifik.kmath.linear

import scientifik.kmath.commons.linear.CMMatrixContext
import scientifik.kmath.commons.linear.toCM
import scientifik.kmath.ejml.EjmlMatrixContext
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.invoke
import scientifik.kmath.structures.Matrix
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
