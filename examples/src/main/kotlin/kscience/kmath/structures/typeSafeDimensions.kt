package kscience.kmath.structures

import kscience.kmath.dimensions.D2
import kscience.kmath.dimensions.D3
import kscience.kmath.dimensions.DMatrixContext
import kscience.kmath.dimensions.Dimension

private fun DMatrixContext<Double>.simple() {
    val m1 = produce<D2, D3> { i, j -> (i + j).toDouble() }
    val m2 = produce<D3, D2> { i, j -> (i + j).toDouble() }

    //Dimension-safe addition
    m1.transpose() + m2
}

private object D5 : Dimension {
    override val dim: UInt = 5u
}

private fun DMatrixContext<Double>.custom() {
    val m1 = produce<D2, D5> { i, j -> (i + j).toDouble() }
    val m2 = produce<D5, D2> { i, j -> (i - j).toDouble() }
    val m3 = produce<D2, D2> { i, j -> (i - j).toDouble() }
    (m1 dot m2) + m3
}

fun main(): Unit = with(DMatrixContext.real) {
    simple()
    custom()
}
