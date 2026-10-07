/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linearsystemsolving.jacobimethod

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.functions.machineEpsilonPrecision
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.Floa64FieldOpsND.Companion.mutableStructureND
import space.kscience.kmath.nd.Floa64FieldOpsND.Companion.structureND
import space.kscience.kmath.nd.MutableStructure1D
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.as1D
import kotlin.math.abs

/**
 * Jacobi's method implementation.
 *
 * In numerical linear algebra, the Jacobi method is an iterative algorithm for determining the solutions of a strictly
 * diagonally dominant system of linear equations.
 *
 * Asymptotic complexity is O(n^3).
 *
 * **See Also:** [https://en.wikipedia.org/wiki/Jacobi_method], [https://ru.wikipedia.org/wiki/Метод_Якоби]
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
public fun solveSystemByJacobiMethod(
    A: Matrix<Double>,
    B: Point<Double>,
    initialApproximation: Point<Double> = getDefaultInitialApproximation(A, B),
    epsilonPrecision: Double = machineEpsilonPrecision,
): Point<Double> {
    val n: Int = A.rowNum

    require(n == A.colNum) {
        "The number of rows of matrix 'A' must match the number of columns."
    }
    require(n == B.size) {
        "The number of matrix 'A' rows must match the number of vector 'B' elements."
    }
    require(B.size == initialApproximation.size) {
        "The size of vector 'B' must match the size of 'initialApproximation' vector."
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

    var X: Point<Double> = initialApproximation
    var norm: Double

    do {
        val xTmp: MutableStructure1D<Double> = mutableStructureND(ShapeND(n)) { 0.0 }.as1D()

        for (i in 0 until n) {
            var sum = 0.0
            for (j in 0 until n) {
                if (j != i) {
                    sum += A[i, j] * X[j]
                }
            }
            xTmp[i] = (B[i] - sum) / A[i, i]
        }

        norm = calcNorm(X, xTmp)
        X = xTmp
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

private fun getDefaultInitialApproximation(A: Matrix<Double>, B: Point<Double>): Point<Double> =
    structureND(ShapeND(A.rowNum)) { (i) ->
        B[i] / A[i, i]
    }.as1D()
