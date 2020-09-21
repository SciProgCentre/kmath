package kscience.kmath.operations.internal

import kscience.kmath.operations.Algebra

internal interface AlgebraicVerifier<T, out A> where A : Algebra<T> {
    val algebra: A

    fun verify()
}
