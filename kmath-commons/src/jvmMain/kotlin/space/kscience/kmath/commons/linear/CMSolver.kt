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

private fun CMLinearSpace.solver(
    a: Matrix<Float64>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): DecompositionSolver = when (decomposition) {
    CMDecomposition.LUP -> LUDecomposition(a.toCM().origin).solver
    CMDecomposition.RRQR -> RRQRDecomposition(a.toCM().origin).solver
    CMDecomposition.QR -> QRDecomposition(a.toCM().origin).solver
    CMDecomposition.EIGEN -> EigenDecomposition(a.toCM().origin).solver
    CMDecomposition.CHOLESKY -> CholeskyDecomposition(a.toCM().origin).solver
}

public fun CMLinearSpace.solve(
    a: Matrix<Float64>,
    b: Matrix<Float64>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMMatrix = solver(a, decomposition).solve(b.toCM().origin).wrap()

public fun CMLinearSpace.solve(
    a: Matrix<Float64>,
    b: Point<Float64>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMVector = solver(a, decomposition).solve(b.toCM().origin).toPoint()

public fun CMLinearSpace.inverse(
    a: Matrix<Float64>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMMatrix = solver(a, decomposition).inverse.wrap()


public fun CMLinearSpace.solver(decomposition: CMDecomposition): LinearSolver<Float64> = object : LinearSolver<Float64> {
    override fun solve(a: Matrix<Float64>, b: Matrix<Float64>): Matrix<Float64> =
        solver(a, decomposition).solve(b.toCM().origin).wrap()

    override fun solve(a: Matrix<Float64>, b: Point<Float64>): Point<Float64> =
        solver(a, decomposition).solve(b.toCM().origin).toPoint()

    override fun inverse(matrix: Matrix<Float64>): Matrix<Float64> = solver(matrix, decomposition).inverse.wrap()
}

public fun CMLinearSpace.lupSolver(): LinearSolver<Float64> = solver((CMDecomposition.LUP))