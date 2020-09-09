package scientifik.kmath.ejml

import org.ejml.simple.SimpleMatrix
import scientifik.kmath.linear.Point

/**
 * Represents point over EJML [SimpleMatrix].
 *
 * @property origin the underlying [SimpleMatrix].
 */
class EjmlVector internal constructor(val origin: SimpleMatrix) : Point<Double> {
    override val size: Int get() = origin.numRows()

    init {
        require(origin.numCols() == 1) { error("Only single column matrices are allowed") }
    }

    override operator fun get(index: Int): Double = origin[index]

    override operator fun iterator(): Iterator<Double> = object : Iterator<Double> {
        private var cursor: Int = 0

        override fun next(): Double {
            cursor += 1
            return origin[cursor - 1]
        }

        override fun hasNext(): Boolean = cursor < origin.numCols() * origin.numRows()
    }
}
