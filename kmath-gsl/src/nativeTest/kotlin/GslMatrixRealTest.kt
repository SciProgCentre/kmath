package kscience.kmath.gsl

import kscience.kmath.linear.Matrix
import kscience.kmath.linear.RealMatrixContext
import kscience.kmath.operations.invoke
import kscience.kmath.structures.asIterable
import kscience.kmath.structures.toList
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GslMatrixRealTest {
    @Test
    fun dimensions() = GslRealMatrixContext {
        val mat = produce(42, 24) { _, _ -> 0.0 }
        assertEquals(42, mat.rowNum)
        assertEquals(24, mat.colNum)
    }

    @Test
    fun get() = GslRealMatrixContext {
        val mat = produce(1, 1) { _, _ -> 42.0 }
        assertEquals(42.0, mat[0, 0])
    }

    @Test
    fun copy() = GslRealMatrixContext {
        val mat = produce(1, 1) { _, _ -> 42.0 }
        assertEquals(mat, mat.copy())
    }

    @Test
    fun equals() = GslRealMatrixContext {
        var rng = Random(0)
        val mat: Matrix<Double> = produce(2, 2) { _, _ -> rng.nextDouble() }
        rng = Random(0)
        val mat2: Matrix<Double> = RealMatrixContext { produce(2, 2) { _, _ -> rng.nextDouble() } }
        rng = Random(0)
        val mat3: Matrix<Double> = produce(2, 2) { _, _ -> rng.nextDouble() }
        assertEquals(mat, mat2)
        assertEquals(mat, mat3)
        assertEquals(mat2, mat3)
    }

    @Test
    fun rows() = GslRealMatrixContext {
        val mat = produce(2, 2) { i, j -> i.toDouble() + j }

        mat.rows.asIterable().zip(listOf(listOf(0.0, 1.0), listOf(1.0, 2.0))).forEach { (a, b) ->
            assertEquals(a.toList(), b)
        }
    }

    @Test
    fun columns() = GslRealMatrixContext {
        val mat = produce(2, 2) { i, j -> i.toDouble() + j }

        mat.columns.asIterable().zip(listOf(listOf(0.0, 1.0), listOf(1.0, 2.0))).forEach { (a, b) ->
            assertEquals(a.toList(), b)
        }
    }
}
