package space.kscience.kmath.ejml

import org.ejml.simple.SimpleMatrix
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.nd.StructureND

/**
 * Represents featured matrix over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 * @author Iaroslav Postovalov
 */
public class EjmlMatrix(public val origin: SimpleMatrix) : Matrix<Double> {
    public override val rowNum: Int get() = origin.numRows()
    public override val colNum: Int get() = origin.numCols()

    public override operator fun get(i: Int, j: Int): Double = origin[i, j]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StructureND<*>) return false
        return StructureND.contentEquals(this, other)
    }

    override fun hashCode(): Int = origin.hashCode()


}
