/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linearsystemsolving.thomasmethod

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.MutableMatrix
import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.Floa64FieldOpsND.Companion.mutableStructureND
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D

/**
 * Thoma's method (tridiagonal matrix algorithm) implementation.
 *
 * In numerical linear algebra, the tridiagonal matrix algorithm, also known as the Thomas algorithm,
 * is a simplified form of Gaussian elimination that can be used to solve tridiagonal systems of equations.
 * This method is based on forward and backward sweeps.
 *
 * Tridiagonal matrix is a matrix that has nonzero elements on the main diagonal,
 * the first diagonal below this and the first diagonal above the main diagonal only.
 *
 * **See Also:** [https://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm], [https://ru.wikipedia.org/wiki/Метод_прогонки]
 *
 * @param A is the input matrix of the system.
 * @param B is the input vector of the right side of the system.
 * @param isStrictMode is the flag, that says that the method will make validation of input matrix 'A',
 * which must be tridiagonal.
 * If [isStrictMode] is true, asymptotic complexity will be O(n^2) and
 * method will throw an exception if the matrix 'A' is not tridiagonal.
 * If [isStrictMode] is false (is default value), asymptotic complexity will be O(n) and
 * solution will be incorrect if the matrix 'A' is not tridiagonal (the responsibility lies with you).
 *
 * @return vector X - solution of the system 'A*X=B'.
 */
@UnstableKMathAPI
public fun solveSystemByThomasMethod(
    A: Matrix<Double>,
    B: Point<Double>,
    isStrictMode: Boolean = false,
): Point<Double> {
    val n: Int = A.rowNum

    require(n == A.colNum) {
        "The number of rows of matrix 'A' must match the number of columns."
    }
    require(n == B.size) {
        "The number of matrix 'A' rows must match the number of vector 'B' elements."
    }

    // Check the condition of the convergence of the Thomas method.
    // The matrix 'A' must be tridiagonal matrix:
    // all elements of the matrix must be zero, except for the elements of the main diagonal and adjacent to it.
    if (isStrictMode) {
        var matrixAisTridiagonal = true
        loop@ for (i in 0 until n) {
            for (j in 0 until n) {
                if (i == 0 && j != 0 && j != 1 && A[i, j] != 0.0) {
                    matrixAisTridiagonal = false
                    break@loop
                } else if (i == n - 1 && j != n - 2 && j != n - 1 && A[i, j] != 0.0) {
                    matrixAisTridiagonal = false
                    break@loop
                } else if (j != i - 1 && j != i && j != i + 1 && A[i, j] != 0.0) {
                    matrixAisTridiagonal = false
                    break@loop
                }
            }
        }
        require(matrixAisTridiagonal) {
            "The matrix 'A' must be tridiagonal: all elements must be zero, except elements of the main diagonal and adjacent to it."
        }
    }

    // Forward sweep
    val alphaBettaCoefficients: MutableMatrix<Double> =
        mutableStructureND(ShapeND(n, 2)) { 0.0 }.as2D()
    for (i in 0 until n) {
        val alpha: Double
        val betta: Double
        if (i == 0) {
            val y: Double = A[i, i]
            alpha = -A[i, i + 1] / y
            betta = B[i] / y
        } else {
            val y: Double = A[i, i] + A[i, i - 1] * alphaBettaCoefficients[i - 1, 0]
            alpha =
                if (i != n - 1) { // For the last row, alpha is not needed and should be 0.0
                    -A[i, i + 1] / y
                } else {
                    0.0
                }
            betta = (B[i] - A[i, i - 1] * alphaBettaCoefficients[i - 1, 1]) / y
        }
        alphaBettaCoefficients[i, 0] = alpha
        alphaBettaCoefficients[i, 1] = betta
    }

    // Backward sweep
    val result = mutableStructureND(ShapeND(n)) { 0.0 }.as1D()
    for (i in n - 1 downTo 0) {
        result[i] =
            if (i == n - 1) {
                alphaBettaCoefficients[i, 1]
            } else {
                alphaBettaCoefficients[i, 0] * result[i + 1] + alphaBettaCoefficients[i, 1]
            }
    }

    return result
}
