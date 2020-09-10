package scientifik.kmath.linear

/**
 * A marker interface representing some matrix feature like diagonal, sparse, zero, etc. Features used to optimize matrix
 * operations performance in some cases.
 */
interface MatrixFeature

/**
 * The matrix with this feature is considered to have only diagonal non-null elements
 */
object DiagonalFeature : MatrixFeature

/**
 * Matrix with this feature has all zero elements
 */
object ZeroFeature : MatrixFeature

/**
 * Matrix with this feature have unit elements on diagonal and zero elements in all other places
 */
object UnitFeature : MatrixFeature

/**
 * Inverted matrix feature
 */
interface InverseMatrixFeature<T : Any> : MatrixFeature {
    val inverse: FeaturedMatrix<T>
}

/**
 * A determinant container
 */
interface DeterminantFeature<T : Any> : MatrixFeature {
    val determinant: T
}

@Suppress("FunctionName")
fun <T : Any> DeterminantFeature(determinant: T): DeterminantFeature<T> = object : DeterminantFeature<T> {
    override val determinant: T = determinant
}

/**
 * Lower triangular matrix
 */
object LFeature : MatrixFeature

/**
 * Upper triangular feature
 */
object UFeature : MatrixFeature

/**
 * TODO add documentation
 */
interface LUPDecompositionFeature<T : Any> : MatrixFeature {
    val l: FeaturedMatrix<T>
    val u: FeaturedMatrix<T>
    val p: FeaturedMatrix<T>
}

//TODO add sparse matrix feature
