/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linearsystemsolving.seidelmethod

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.functions.machineEpsilonPrecision
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.*
import space.kscience.kmath.nd.Floa64FieldOpsND.Companion.mutableStructureND
import space.kscience.kmath.nd.Floa64FieldOpsND.Companion.structureND
import kotlin.math.abs

/**
 * Seidel method implementation.
 *
 * In numerical linear algebra, the Gauss–Seidel method, also known as the Liebmann method or
 * the method of successive displacement,
 * is an iterative method used to solve a system of linear equations and is similar to the 'Jacobi Method'.
 * The Gauss-Seidel method can be considered as a modification of the Jacobi method, which, as practice shows,
 * requires approximately half the number of iterations compared to the Jacobi method.
 * Though it can be applied to any matrix with non-zero elements on the diagonals,
 * convergence is only guaranteed if the matrix is either strictly diagonally dominant,
 * or symmetric and positive definite.
 *
 * Asymptotic complexity: O(n^3)
 *
 * **See Also:** [https://en.wikipedia.org/wiki/Gauss–Seidel_method], [https://ru.wikipedia.org/wiki/Метод_Гаусса_—_Зейделя_решения_системы_линейных_уравнений]
 *
 * @param [A] is the input matrix of the system.
 * @param [B] is the input vector of the right side of the system.
 * @param [initialApproximation] is the input initial approximation vector.
 * If the user does not pass custom initial approximation vector, then it will be calculated with default algorithm.
 * @param [epsilonPrecision] is the input precision of the result.
 * The user can use, for example, 'epsilonPrecision = 0.001' if he need quickly solution with the small solution error.
 * If the user does not pass [epsilonPrecision],
 * then will be used default machine precision as the most accurate precision, but with smaller performance.
 *
 * @return vector X - solution of the system 'A*X=B'.
 */
@UnstableKMathAPI
public fun solveSystemBySeidelMethod(
    A: Matrix<Double>,
    B: Point<Double>,
    initialApproximation: Point<Double>? = null,
    epsilonPrecision: Double = machineEpsilonPrecision,
): Point<Double> {
    val n: Int = A.rowNum

    require(n == A.colNum) {
        "The number of rows of matrix 'A' must match the number of columns."
    }
    require(n == B.size) {
        "The number of matrix 'A' rows must match the number of vector 'B' elements."
    }
    initialApproximation?.let {
        require(B.size == initialApproximation.size) {
            "The size of vector 'B' must match the size of 'initialApproximation' vector."
        }
    }

    // Check the sufficient condition of the convergence of the method: must be diagonal dominance in the matrix 'A'
    for (i in 0 until n) {
        var sumOfRowWithoutDiagElem = 0.0
        var diagElemAbs = 0.0
        for (j in 0 until n) {
            if (i != j) {
                sumOfRowWithoutDiagElem += abs(A[i, j])
            } else {
                diagElemAbs = abs(A[i, j])
            }
        }
        require(sumOfRowWithoutDiagElem < diagElemAbs) {
            "The sufficient condition for the convergence of the Jacobi method is not satisfied: there is no diagonal dominance in the matrix 'A'."
        }
    }

    val X: MutableStructure1D<Double> = initialApproximation?.let {
        mutableStructureND(ShapeND(n)) { (i) ->
            initialApproximation[i]
        }.as1D()
    } ?: getDefaultInitialApproximation(A, B)

    var xTmp: Structure1D<Double> = structureND(ShapeND(n)) { 0.0 }.as1D()
    var norm: Double

    do {
        for (i in 0 until n) {
            var sum = B[i]
            for (j in 0 until n) {
                if (j != i) {
                    sum -= A[i, j] * X[j]
                }
            }
            X[i] = sum / A[i, i]
        }

        norm = calcNorm(X, xTmp)
        xTmp = structureND(ShapeND(n)) { (i) ->
            X[i]
        }.as1D() // TODO find another way to make copy, for example, support 'clone' method in the library
    } while (norm > epsilonPrecision)

    return X
}

private fun calcNorm(x1: Point<Double>, x2: Point<Double>): Double {
    var sum = 0.0
    for (i in 0 until x1.size) {
        sum += abs(x1[i] - x2[i])
    }

    return sum
}

private fun getDefaultInitialApproximation(A: Matrix<Double>, B: Point<Double>): MutableStructure1D<Double> =
    mutableStructureND(ShapeND(A.rowNum)) { (i) ->
        B[i] / A[i, i]
    }.as1D()
