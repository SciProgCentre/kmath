package scientifik.kmath.linear

import koma.extensions.fill
import koma.matrix.MatrixFactory
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.invoke
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.NDStructure

class KomaMatrixContext<T : Any>(
    private val factory: MatrixFactory<koma.matrix.Matrix<T>>,
    private val space: Space<T>
) : MatrixContext<T> {

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): KomaMatrix<T> =
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


    override fun Matrix<T>.dot(other: Matrix<T>): KomaMatrix<T> =
        KomaMatrix(toKoma().origin * other.toKoma().origin)

    override fun Matrix<T>.dot(vector: Point<T>): KomaVector<T> =
        KomaVector(toKoma().origin * vector.toKoma().origin)

    override operator fun Matrix<T>.unaryMinus(): KomaMatrix<T> =
        KomaMatrix(toKoma().origin.unaryMinus())

    override fun add(a: Matrix<T>, b: Matrix<T>): KomaMatrix<T> =
        KomaMatrix(a.toKoma().origin + b.toKoma().origin)

    override operator fun Matrix<T>.minus(b: Matrix<T>): KomaMatrix<T> =
        KomaMatrix(toKoma().origin - b.toKoma().origin)

    override fun multiply(a: Matrix<T>, k: Number): Matrix<T> =
        produce(a.rowNum, a.colNum) { i, j -> space { a[i, j] * k } }

    override operator fun Matrix<T>.times(value: T): KomaMatrix<T> =
        KomaMatrix(toKoma().origin * value)

    companion object
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

    override val features: Set<MatrixFeature> = features ?: hashSetOf(
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

    override operator fun get(i: Int, j: Int): T = origin.getGeneric(i, j)

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
    override val size: Int get() = origin.numRows()

    init {
        require(origin.numCols() == 1) { error("Only single column matrices are allowed") }
    }

    override operator fun get(index: Int): T = origin.getGeneric(index)
    override operator fun iterator(): Iterator<T> = origin.toIterable().iterator()
}
