package scientifik.kmath.linear

class VirtualMatrix<T : Any>(
    override val rowNum: Int,
    override val colNum: Int,
    override val features: Set<MatrixFeature> = emptySet(),
    val generator: (i: Int, j: Int) -> T
) : Matrix<T> {
    override fun get(i: Int, j: Int): T = generator(i, j)
}