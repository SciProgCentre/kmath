package space.kscience.kmath.commons.linear

import org.apache.commons.math3.linear.*
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point

public enum class CMDecomposition {
    LUP,
    QR,
    RRQR,
    EIGEN,
    CHOLESKY
}

public fun CMLinearSpace.solver(
    a: Matrix<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): DecompositionSolver = when (decomposition) {
    CMDecomposition.LUP -> LUDecomposition(a.toCM().origin).solver
    CMDecomposition.RRQR -> RRQRDecomposition(a.toCM().origin).solver
    CMDecomposition.QR -> QRDecomposition(a.toCM().origin).solver
    CMDecomposition.EIGEN -> EigenDecomposition(a.toCM().origin).solver
    CMDecomposition.CHOLESKY -> CholeskyDecomposition(a.toCM().origin).solver
}

public fun CMLinearSpace.solve(
    a: Matrix<Double>,
    b: Matrix<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMMatrix = solver(a, decomposition).solve(b.toCM().origin).wrap()

public fun CMLinearSpace.solve(
    a: Matrix<Double>,
    b: Point<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMVector = solver(a, decomposition).solve(b.toCM().origin).toPoint()

public fun CMLinearSpace.inverse(
    a: Matrix<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP,
): CMMatrix = solver(a, decomposition).inverse.wrap()
