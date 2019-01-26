package scientifik.kmath.linear

/**
 * A marker interface representing some matrix feature like diagonal, sparce, zero, etc. Features used to optimize matrix
 * operations performance in some cases.
 */
interface MatrixFeature

/**
 * The matrix with this feature is considered to have only diagonal non-null elements
 */
object DiagonalFeature : MatrixFeature

/**
 * Matix with this feature has all zero elements
 */
object ZeroFeature : MatrixFeature

/**
 * Matrix with this feature have unit elements on diagonal and zero elements in all other places
 */
object UnitFeature : MatrixFeature

interface InverseMatrixFeature<T : Any> : MatrixFeature {
    val inverse: Matrix<T>
}

interface DeterminantFeature<T : Any> : MatrixFeature {
    val determinant: T
}