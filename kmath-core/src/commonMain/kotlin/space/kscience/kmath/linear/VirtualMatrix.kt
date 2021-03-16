package space.kscience.kmath.linear

/**
 * The matrix where each element is evaluated each time when is being accessed.
 *
 * @property generator the function that provides elements.
 */
public class VirtualMatrix<T : Any>(
    override val rowNum: Int,
    override val colNum: Int,
    public val generator: (i: Int, j: Int) -> T,
) : Matrix<T> {

    override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    override operator fun get(i: Int, j: Int): T = generator(i, j)
}
