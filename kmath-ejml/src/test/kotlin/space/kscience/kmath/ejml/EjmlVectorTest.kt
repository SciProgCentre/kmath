/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ejml

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.RandomMatrices_DDRM
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class EjmlVectorTest {
    private val random = Random(0)

    private val randomMatrix: DMatrixRMaj
        get() {
            val d = DMatrixRMaj(1, random.nextInt(2, 100))
            RandomMatrices_DDRM.fillUniform(d, random.asJavaRandom())
            return d
        }

    @Test
    fun size() {
        val m = randomMatrix
        val w = EjmlDoubleVector(m)
        assertEquals(m.numCols, w.size)
    }

    @Test
    fun get() {
        val m = randomMatrix
        val w = EjmlDoubleVector(m)
        assertEquals(m[0, 0], w[0])
    }

    @Test
    fun iterator() {
        val m = randomMatrix
        val w = EjmlDoubleVector(m)

        assertEquals(
            m.iterator(true, 0, 0, 0, m.numCols - 1).asSequence().toList(),
            w.iterator().asSequence().toList()
        )
    }

    @Test
    fun origin() {
        val m = randomMatrix
        val w = EjmlDoubleVector(m)
        assertSame(m, w.origin)
    }
}
