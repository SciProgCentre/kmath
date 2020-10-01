package space.kscience.kmath.ojalgo

import org.ojalgo.structure.Access1D
import org.ojalgo.structure.Access2D
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point

public class OjalgoMatrix<T : Comparable<T>>(public val origin: Access2D<T>) : Matrix<T> {
    public override val rowNum: Int
        get() = Math.toIntExact(origin.countRows())

    public override val colNum: Int
        get() = Math.toIntExact(origin.countColumns())

    public override fun get(i: Int, j: Int): T = origin[i.toLong(), j.toLong()]
}

public class OjalgoVector<T : Comparable<T>>(public val origin: Access1D<T>) : Point<T> {
    public override val size: Int
        get() = origin.size()

    override fun get(index: Int): T = origin[index.toLong()]

    public override operator fun iterator(): Iterator<T> = object : Iterator<T> {
        private var cursor: Int = 0

        override fun next(): T {
            cursor += 1
            return this@OjalgoVector[cursor - 1]
        }

        override fun hasNext(): Boolean = cursor < size
    }
}
