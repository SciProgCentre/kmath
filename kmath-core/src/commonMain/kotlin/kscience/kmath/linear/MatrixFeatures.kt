package kscience.kmath.linear

/**
 * A marker interface representing some matrix feature like diagonal, sparse, zero, etc. Features used to optimize matrix
 * operations performance in some cases.
 */
public interface MatrixFeature

/**
 * The matrix with this feature is considered to have only diagonal non-null elements
 */
public object DiagonalFeature : MatrixFeature

/**
 * Matrix with this feature has all zero elements
 */
public object ZeroFeature : MatrixFeature

/**
 * Matrix with this feature have unit elements on diagonal and zero elements in all other places
 */
public object UnitFeature : MatrixFeature

/**
 * Inverted matrix feature
 */
public interface InverseMatrixFeature<T : Any> : MatrixFeature {
    public val inverse: FeaturedMatrix<T>
}

/**
 * A determinant container
 */
public interface DeterminantFeature<T : Any> : MatrixFeature {
    public val determinant: T
}

@Suppress("FunctionName")
public fun <T : Any> DeterminantFeature(determinant: T): DeterminantFeature<T> = object : DeterminantFeature<T> {
    override val determinant: T = determinant
}

/**
 * Lower triangular matrix
 */
public object LFeature : MatrixFeature

/**
 * Upper triangular feature
 */
public object UFeature : MatrixFeature

/**
 * TODO add documentation
 */
public interface LUPDecompositionFeature<T : Any> : MatrixFeature {
    public val l: FeaturedMatrix<T>
    public val u: FeaturedMatrix<T>
    public val p: FeaturedMatrix<T>
}

//TODO add sparse matrix feature
