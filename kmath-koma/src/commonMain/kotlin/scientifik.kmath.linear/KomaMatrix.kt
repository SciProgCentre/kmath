package scientifik.kmath.linear

import scientifik.kmath.operations.Ring

class KomaMatrixContext<T: Any, R: Ring<T>> : MatrixContext<T,R> {
    override val elementContext: R
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): Matrix<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun point(size: Int, initializer: (Int) -> T): Point<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

inline class KomaMatrix<T : Any>(val matrix: koma.matrix.Matrix<T>) : Matrix<T> {
    override val rowNum: Int get() = matrix.numRows()
    override val colNum: Int get() = matrix.numCols()
    override val features: Set<MatrixFeature> get() = emptySet()

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun get(i: Int, j: Int): T = matrix.getGeneric(i, j)

}