/*
 * Copyright 2018-2024 KMath contributors.
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
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.testutils.assertStructureEquals
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.test.*

internal fun <T : Any> assertMatrixEquals(expected: StructureND<T>, actual: StructureND<T>) {
    expected.elements().forEach { (index, value) ->
        assertEquals(value, actual[index], "Structure element with index ${index.toList()} should be equal to $value but is ${actual[index]}")
    }
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
    fun features() = EjmlLinearSpaceDDRM {
        val m = randomMatrix
        val w = EjmlDoubleMatrix(m)
        val det: Double = w.getOrComputeAttribute(Determinant) ?: fail()
        assertEquals(CommonOps_DDRM.det(m), det)
        val lup: LupDecomposition<Float64> = w.getOrComputeAttribute(LUP) ?: fail()

        val ludecompositionF64 = DecompositionFactory_DDRM.lu(m.numRows, m.numCols)
            .also { it.decompose(m.copy()) }

        assertMatrixEquals(EjmlDoubleMatrix(ludecompositionF64.getLower(null)), lup.l)
        assertMatrixEquals(EjmlDoubleMatrix(ludecompositionF64.getUpper(null)), lup.u)
        assertMatrixEquals(EjmlDoubleMatrix(ludecompositionF64.getRowPivot(null)), lup.pivotMatrix(this))
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
        val inverted = matrix.toEjml().inverted()

        val res = matrix dot inverted

        println(StructureND.toString(res))

        assertTrue { StructureND.contentEquals(one(dim, dim), res, 1e-3) }
    }

    @Test
    fun eigenValueDecomposition() = EjmlLinearSpaceDDRM {
        val dim = 46
        val u = buildMatrix(dim, dim) { i, j -> if (i <= j) random.nextDouble() else 0.0 }
        val matrix = buildMatrix(dim, dim) { row, col ->
            if (row >= col) u[row, col] else u[col, row]
        }
        val eigen = matrix.getOrComputeAttribute(EIG) ?: fail()
        assertStructureEquals(matrix, eigen.v dot eigen.d dot eigen.v.transposed())
    }
}
