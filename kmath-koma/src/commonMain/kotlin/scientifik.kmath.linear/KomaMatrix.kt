package scientifik.kmath.linear

import koma.extensions.fill
import koma.matrix.MatrixFactory
import scientifik.kmath.operations.Space
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.NDStructure

class KomaMatrixContext<T : Any>(
    private val factory: MatrixFactory<koma.matrix.Matrix<T>>,
    private val space: Space<T>
) :
    MatrixContext<T> {

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T) =
        KomaMatrix(factory.zeros(rows, columns).fill(initializer))

    fun Matrix<T>.toKoma(): KomaMatrix<T> = if (this is KomaMatrix) {
        this
    } else {
        produce(rowNum, colNum) { i, j -> get(i, j) }
    }

    fun Point<T>.toKoma(): KomaVector<T> = if (this is KomaVector) {
        this
    } else {
        KomaVector(factory.zeros(size, 1).fill { i, _ -> get(i) })
    }


    override fun Matrix<T>.dot(other: Matrix<T>) =
        KomaMatrix(this.toKoma().origin * other.toKoma().origin)

    override fun Matrix<T>.dot(vector: Point<T>) =
        KomaVector(this.toKoma().origin * vector.toKoma().origin)

    override fun Matrix<T>.unaryMinus() =
        KomaMatrix(this.toKoma().origin.unaryMinus())

    override fun add(a: Matrix<T>, b: Matrix<T>) =
        KomaMatrix(a.toKoma().origin + b.toKoma().origin)

    override fun Matrix<T>.minus(b: Matrix<T>) =
        KomaMatrix(this.toKoma().origin - b.toKoma().origin)

    override fun multiply(a: Matrix<T>, k: Number): Matrix<T> =
        produce(a.rowNum, a.colNum) { i, j -> space.run { a[i, j] * k } }

    override fun Matrix<T>.times(value: T) =
        KomaMatrix(this.toKoma().origin * value)

    companion object {

    }

}

fun <T : Any> KomaMatrixContext<T>.solve(a: Matrix<T>, b: Matrix<T>) =
    KomaMatrix(a.toKoma().origin.solve(b.toKoma().origin))

fun <T : Any> KomaMatrixContext<T>.solve(a: Matrix<T>, b: Point<T>) =
    KomaVector(a.toKoma().origin.solve(b.toKoma().origin))

fun <T : Any> KomaMatrixContext<T>.inverse(a: Matrix<T>) =
    KomaMatrix(a.toKoma().origin.inv())

class KomaMatrix<T : Any>(val origin: koma.matrix.Matrix<T>, features: Set<MatrixFeature>? = null) : FeaturedMatrix<T> {
    override val rowNum: Int get() = origin.numRows()
    override val colNum: Int get() = origin.numCols()

    override val shape: IntArray get() = intArrayOf(origin.numRows(), origin.numCols())

    override val features: Set<MatrixFeature> = features ?: setOf(
        object : DeterminantFeature<T> {
            override val determinant: T get() = origin.det()
        },
        object : LUPDecompositionFeature<T> {
            private val lup by lazy { origin.LU() }
            override val l: FeaturedMatrix<T> get() = KomaMatrix(lup.second)
            override val u: FeaturedMatrix<T> get() = KomaMatrix(lup.third)
            override val p: FeaturedMatrix<T> get() = KomaMatrix(lup.first)
        }
    )

    override fun suggestFeature(vararg features: MatrixFeature): FeaturedMatrix<T> =
        KomaMatrix(this.origin, this.features + features)

    override fun get(i: Int, j: Int): T = origin.getGeneric(i, j)

    override fun equals(other: Any?): Boolean {
        return NDStructure.equals(this, other as? NDStructure<*> ?: return false)
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }


}

class KomaVector<T : Any> internal constructor(val origin: koma.matrix.Matrix<T>) : Point<T> {
    init {
        if (origin.numCols() != 1) error("Only single column matrices are allowed")
    }

    override val size: Int get() = origin.numRows()

    override fun get(index: Int): T = origin.getGeneric(index)

    override fun iterator(): Iterator<T> = origin.toIterable().iterator()
}

