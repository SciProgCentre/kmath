package scientifik.kmath.operations.internal

import scientifik.kmath.operations.Algebra

internal interface AlgebraicVerifier<T, out A> where A : Algebra<T> {
    val algebra: A

    fun verify()
}
