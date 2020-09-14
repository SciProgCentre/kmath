package scientifik.kmath.linear

import koma.matrix.ejml.EJMLMatrixFactory
import scientifik.kmath.commons.linear.CMMatrixContext
import scientifik.kmath.commons.linear.toCM
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

        val cmTime = measureTimeMillis {
            cmMatrix1 dot cmMatrix2
        }

        println("CM implementation time: $cmTime")
    }

    (KomaMatrixContext(EJMLMatrixFactory(), RealField)) {
        val komaMatrix1 = matrix1.toKoma()
        val komaMatrix2 = matrix2.toKoma()

        val komaTime = measureTimeMillis {
            komaMatrix1 dot komaMatrix2
        }

        println("Koma-ejml implementation time: $komaTime")
    }

    val genericTime = measureTimeMillis {
        val res = matrix1 dot matrix2
    }

    println("Generic implementation time: $genericTime")
}