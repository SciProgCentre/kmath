package space.kscience.kmath.testutils

import space.kscience.kmath.operations.Algebra

internal interface AlgebraicVerifier<T, out A> where A : Algebra<T> {
    val algebra: A

    fun verify()
}
