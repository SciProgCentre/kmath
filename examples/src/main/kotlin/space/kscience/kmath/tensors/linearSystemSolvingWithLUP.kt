/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.tensorAlgebra
import space.kscience.kmath.tensors.core.withBroadcast

// solving linear system with LUP decomposition

fun main() = Double.tensorAlgebra.withBroadcast {// work in context with linear operations

    // set true value of x
    val trueX = fromArray(
        ShapeND(4),
        doubleArrayOf(-2.0, 1.5, 6.8, -2.4)
    )

    // and A matrix
    val a = fromArray(
        ShapeND(4, 4),
        doubleArrayOf(
            0.5, 10.5, 4.5, 1.0,
            8.5, 0.9, 12.8, 0.1,
            5.56, 9.19, 7.62, 5.45,
            1.0, 2.0, -3.0, -2.5
        )
    )

    // calculate y value
    val b = a dot trueX

    // check out A and b
    println("A:\n$a")
    println("b:\n$b")

    // solve `Ax = b` system using LUP decomposition

    // get P, L, U such that PA = LU
    val (p, l, u) = lu(a)

    // check P is permutation matrix
    println("P:\n$p")
    // L is lower triangular matrix and U is upper triangular matrix
    println("L:\n$l")
    println("U:\n$u")
    // and PA = LU
    println("PA:\n${p dot a}")
    println("LU:\n${l dot u}")

    /* Ax = b;
       PAx = Pb;
       LUx = Pb;
       let y = Ux, then
       Ly = Pb -- this system can be easily solved, since the matrix L is lower triangular;
       Ux = y can be solved the same way, since the matrix L is upper triangular
        */



    // this function returns solution x of a system lx = b, l should be lower triangular
    fun solveLT(l: DoubleTensor, b: DoubleTensor): DoubleTensor {
        val n = l.shape[0]
        val x = zeros(ShapeND(n))
        for (i in 0 until n) {
            x[intArrayOf(i)] = (b[intArrayOf(i)] - l.getTensor(i).dot(x).value()) / l[intArrayOf(i, i)]
        }
        return x
    }

    val y = solveLT(l, p dot b)

    // solveLT(l, b) function can be easily adapted for upper triangular matrix by the permutation matrix revMat
    // create it by placing ones on side diagonal
    val revMat = zeroesLike(u)
    val n = revMat.shape[0]
    for (i in 0 until n) {
        revMat[intArrayOf(i, n - 1 - i)] = 1.0
    }

    // solution of system ux = b, u should be upper triangular
    fun solveUT(u: DoubleTensor, b: DoubleTensor): DoubleTensor = revMat dot solveLT(
        revMat dot u dot revMat, revMat dot b
    )

    val x = solveUT(u, y)

    println("True x:\n$trueX")
    println("x founded with LU method:\n$x")
}