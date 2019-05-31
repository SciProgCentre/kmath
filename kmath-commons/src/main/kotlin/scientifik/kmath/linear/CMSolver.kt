package scientifik.kmath.linear

import org.apache.commons.math3.linear.*
import scientifik.kmath.structures.Matrix

enum class CMDecomposition {
    LUP,
    QR,
    RRQR,
    EIGEN,
    CHOLESKY
}


fun CMMatrixContext.solver(a: Matrix<Double>, decomposition: CMDecomposition = CMDecomposition.LUP) =
    when (decomposition) {
        CMDecomposition.LUP -> LUDecomposition(a.toCM().origin).solver
        CMDecomposition.RRQR -> RRQRDecomposition(a.toCM().origin).solver
        CMDecomposition.QR -> QRDecomposition(a.toCM().origin).solver
        CMDecomposition.EIGEN -> EigenDecomposition(a.toCM().origin).solver
        CMDecomposition.CHOLESKY -> CholeskyDecomposition(a.toCM().origin).solver
    }

fun CMMatrixContext.solve(
    a: Matrix<Double>,
    b: Matrix<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP
) = solver(a, decomposition).solve(b.toCM().origin).asMatrix()

fun CMMatrixContext.solve(
    a: Matrix<Double>,
    b: Point<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP
) = solver(a, decomposition).solve(b.toCM().origin).toPoint()

fun CMMatrixContext.inverse(
    a: Matrix<Double>,
    decomposition: CMDecomposition = CMDecomposition.LUP
) = solver(a, decomposition).inverse.asMatrix()
