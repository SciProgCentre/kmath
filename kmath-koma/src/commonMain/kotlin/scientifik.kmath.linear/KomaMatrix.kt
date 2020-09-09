package scientifik.kmath.linear

import koma.extensions.fill
import koma.matrix.MatrixFactory
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.invoke
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.NDStructure

public class KomaMatrixContext<T : Any>(
    private val factory: MatrixFactory<koma.matrix.Matrix<T>>,
    private val space: Space<T>
) : MatrixContext<T> {
    public override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): KomaMatrix<T> =
        KomaMatrix(factory.zeros(rows, columns).fill(initializer))

    public fun Matrix<T>.toKoma(): KomaMatrix<T> = if (this is KomaMatrix)
        this
    else
        produce(rowNum, colNum) { i, j -> get(i, j) }

    public fun Point<T>.toKoma(): KomaVector<T> = if (this is KomaVector)
        this
    else
        KomaVector(factory.zeros(size, 1).fill { i, _ -> get(i) })

    public override fun Matrix<T>.dot(other: Matrix<T>): KomaMatrix<T> =
        KomaMatrix(toKoma().origin * other.toKoma().origin)

    public override fun Matrix<T>.dot(vector: Point<T>): KomaVector<T> =
        KomaVector(toKoma().origin * vector.toKoma().origin)

    public override operator fun Matrix<T>.unaryMinus(): KomaMatrix<T> =
        KomaMatrix(toKoma().origin.unaryMinus())

    public override fun add(a: Matrix<T>, b: Matrix<T>): KomaMatrix<T> =
        KomaMatrix(a.toKoma().origin + b.toKoma().origin)

    public override operator fun Matrix<T>.minus(b: Matrix<T>): KomaMatrix<T> =
        KomaMatrix(toKoma().origin - b.toKoma().origin)

    public override fun multiply(a: Matrix<T>, k: Number): Matrix<T> =
        produce(a.rowNum, a.colNum) { i, j -> space { a[i, j] * k } }

    public override operator fun Matrix<T>.times(value: T): KomaMatrix<T> =
        KomaMatrix(toKoma().origin * value)

    public companion object
}

public fun <T : Any> KomaMatrixContext<T>.solve(a: Matrix<T>, b: Matrix<T>): KomaMatrix<T> =
    KomaMatrix(a.toKoma().origin.solve(b.toKoma().origin))

public fun <T : Any> KomaMatrixContext<T>.solve(a: Matrix<T>, b: Point<T>): KomaVector<T> =
    KomaVector(a.toKoma().origin.solve(b.toKoma().origin))

public fun <T : Any> KomaMatrixContext<T>.inverse(a: Matrix<T>): KomaMatrix<T> =
    KomaMatrix(a.toKoma().origin.inv())

public class KomaMatrix<T : Any>(public val origin: koma.matrix.Matrix<T>, features: Set<MatrixFeature>? = null) :
    FeaturedMatrix<T> {
    public override val rowNum: Int get() = origin.numRows()
    public override val colNum: Int get() = origin.numCols()

    public override val shape: IntArray get() = intArrayOf(origin.numRows(), origin.numCols())

    public override val features: Set<MatrixFeature> = features ?: hashSetOf(
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

public class KomaVector<T : Any> internal constructor(public val origin: koma.matrix.Matrix<T>) : Point<T> {
    override val size: Int get() = origin.numRows()

    init {
        require(origin.numCols() == 1) { error("Only single column matrices are allowed") }
    }

    override operator fun get(index: Int): T = origin.getGeneric(index)
    override operator fun iterator(): Iterator<T> = origin.toIterable().iterator()
}
