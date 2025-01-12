/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.commons.linear

import org.apache.commons.math3.linear.*
import space.kscience.kmath.linear.LinearSolver
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.Float64

public enum class CMDecomposition {
    LUP,
    QR,
    RRQR,
    EIGEN,
    CHOLESKY
}

private fun CMLinearSpace.cmSolver(
    a: Matrix<Float64>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): DecompositionSolver = when (decomposition) {
    CMDecomposition.LUP -> LUDecomposition(a.toCM()).solver
    CMDecomposition.RRQR -> RRQRDecomposition(a.toCM()).solver
    CMDecomposition.QR -> QRDecomposition(a.toCM()).solver
    CMDecomposition.EIGEN -> EigenDecomposition(a.toCM()).solver
    CMDecomposition.CHOLESKY -> CholeskyDecomposition(a.toCM()).solver
}

public fun CMLinearSpace.solve(
    a: Matrix<Float64>,
    b: Matrix<Float64>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMMatrix = cmSolver(a, decomposition).solve(b.toCM()).asMatrix()

public fun CMLinearSpace.solve(
    a: Matrix<Float64>,
    b: Point<Float64>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMVector = cmSolver(a, decomposition).solve(b.toCM()).asVector()

public fun CMLinearSpace.inverse(
    a: Matrix<Float64>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMMatrix = cmSolver(a, decomposition).inverse.asMatrix()


public fun CMLinearSpace.solver(decomposition: CMDecomposition): LinearSolver<Float64> =
    object : LinearSolver<Float64> {
        override fun solve(a: Matrix<Float64>, b: Matrix<Float64>): Matrix<Float64> =
            cmSolver(a, decomposition).solve(b.toCM()).asMatrix()

        override fun solve(a: Matrix<Float64>, b: Point<Float64>): Point<Float64> =
            cmSolver(a, decomposition).solve(b.toCM()).asVector()

        override fun inverse(matrix: Matrix<Float64>): Matrix<Float64> = cmSolver(matrix, decomposition).inverse.asMatrix()
    }

public fun CMLinearSpace.lupSolver(): LinearSolver<Float64> = solver((CMDecomposition.LUP))