package kscience.kmath.commons.linear

import kscience.kmath.linear.Matrix
import kscience.kmath.linear.Point
import org.apache.commons.math3.linear.*

public enum class CMDecomposition {
    LUP,
    QR,
    RRQR,
    EIGEN,
    CHOLESKY
}

public fun CMMatrixContext.solver(
    a: Matrix<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP
): DecompositionSolver = when (decomposition) {
    CMDecomposition.LUP -> LUDecomposition(a.toCM().origin).solver
    CMDecomposition.RRQR -> RRQRDecomposition(a.toCM().origin).solver
    CMDecomposition.QR -> QRDecomposition(a.toCM().origin).solver
    CMDecomposition.EIGEN -> EigenDecomposition(a.toCM().origin).solver
    CMDecomposition.CHOLESKY -> CholeskyDecomposition(a.toCM().origin).solver
}

public fun CMMatrixContext.solve(
    a: Matrix<Double>,
    b: Matrix<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP
): CMMatrix = solver(a, decomposition).solve(b.toCM().origin).asMatrix()

public fun CMMatrixContext.solve(
    a: Matrix<Double>,
    b: Point<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP
): CMVector = solver(a, decomposition).solve(b.toCM().origin).toPoint()

public fun CMMatrixContext.inverse(
    a: Matrix<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP
): CMMatrix = solver(a, decomposition).inverse.asMatrix()
