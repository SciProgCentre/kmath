/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(PerformancePitfall::class)

package space.kscience.kmath.ejml

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.RandomMatrices_DDRM
import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.*
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.toArray
import space.kscience.kmath.operations.algebra
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.test.*

internal fun <T : Any> assertMatrixEquals(expected: StructureND<T>, actual: StructureND<T>) {
    assertTrue { StructureND.contentEquals(expected, actual) }
}

@OptIn(UnstableKMathAPI::class)
internal class EjmlMatrixTest {
    private val random = Random(0)

    private val randomMatrix: DMatrixRMaj
        get() {
            val s = random.nextInt(2, 100)
            val d = DMatrixRMaj(s, s)
            RandomMatrices_DDRM.fillUniform(d, random.asJavaRandom())
            return d
        }

    @Test
    fun rowNum() {
        val m = randomMatrix
        assertEquals(m.numRows, EjmlDoubleMatrix(m).rowNum)
    }

    @Test
    fun colNum() {
        val m = randomMatrix
        assertEquals(m.numCols, EjmlDoubleMatrix(m).rowNum)
    }

    @Test
    fun shape() {
        val m = randomMatrix
        val w = EjmlDoubleMatrix(m)
        assertContentEquals(intArrayOf(m.numRows, m.numCols), w.shape.toArray())
    }

    @OptIn(UnstableKMathAPI::class)
    @Test
    fun features() {
        val m = randomMatrix
        val w = EjmlDoubleMatrix(m)
        val det: DeterminantFeature<Double> = EjmlLinearSpaceDDRM.computeFeature(w) ?: fail()
        assertEquals(CommonOps_DDRM.det(m), det.determinant)
        val lup: LupDecompositionFeature<Double> = EjmlLinearSpaceDDRM.computeFeature(w) ?: fail()

        val ludecompositionF64 = DecompositionFactory_DDRM.lu(m.numRows, m.numCols)
            .also { it.decompose(m.copy()) }

        assertMatrixEquals(EjmlDoubleMatrix(ludecompositionF64.getLower(null)), lup.l)
        assertMatrixEquals(EjmlDoubleMatrix(ludecompositionF64.getUpper(null)), lup.u)
        assertMatrixEquals(EjmlDoubleMatrix(ludecompositionF64.getRowPivot(null)), lup.p)
    }

    @Test
    fun get() {
        val m = randomMatrix
        assertEquals(m[0, 0], EjmlDoubleMatrix(m)[0, 0])
    }

    @Test
    fun origin() {
        val m = randomMatrix
        assertSame(m, EjmlDoubleMatrix(m).origin)
    }

    @Test
    fun inverse() = EjmlLinearSpaceDDRM {
        val random = Random(1224)
        val dim = 20

        val space = Double.algebra.linearSpace

        //creating invertible matrix
        val u = space.buildMatrix(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        val l = space.buildMatrix(dim, dim) { i, j -> if (i >= j) random.nextDouble() else 0.0 }
        val matrix = space { l dot u }
        val inverted = matrix.toEjml().inverse()

        val res = matrix dot inverted

        println(StructureND.toString(res))

        assertTrue { StructureND.contentEquals(one(dim, dim), res, 1e-3) }
    }
}
