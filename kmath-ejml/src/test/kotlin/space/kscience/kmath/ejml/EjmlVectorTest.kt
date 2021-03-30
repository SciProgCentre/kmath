package space.kscience.kmath.ejml

import org.ejml.simple.SimpleMatrix
import kotlin.random.Random
import kotlin.random.asJavaRandom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class EjmlVectorTest {
    private val random = Random(0)

    private val randomMatrix: SimpleMatrix
        get() = SimpleMatrix.random_DDRM(random.nextInt(2, 100), 1, 0.0, 10.0, random.asJavaRandom())

    @Test
    fun size() {
        val m = randomMatrix
        val w = EjmlVector(m)
        assertEquals(m.numRows(), w.size)
    }

    @Test
    fun get() {
        val m = randomMatrix
        val w = EjmlVector(m)
        assertEquals(m[0, 0], w[0])
    }

    @Test
    fun iterator() {
        val m = randomMatrix
        val w = EjmlVector(m)

        assertEquals(
            m.iterator(true, 0, 0, m.numRows() - 1, 0).asSequence().toList(),
            w.iterator().asSequence().toList()
        )
    }

    @Test
    fun origin() {
        val m = randomMatrix
        val w = EjmlVector(m)
        assertSame(m, w.origin)
    }
}
