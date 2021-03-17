package space.kscience.kmath.ejml

import org.ejml.simple.SimpleMatrix
import space.kscience.kmath.linear.Matrix

/**
 * The matrix implementation over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 * @author Iaroslav Postovalov
 */
public inline class EjmlMatrix(public val origin: SimpleMatrix) : Matrix<Double> {
    public override val rowNum: Int get() = origin.numRows()
    public override val colNum: Int get() = origin.numCols()

    public override operator fun get(i: Int, j: Int): Double = origin[i, j]
}
