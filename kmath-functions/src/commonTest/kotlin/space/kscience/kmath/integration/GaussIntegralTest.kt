package space.kscience.kmath.integration

import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

class GaussIntegralTest {
    @Test
    fun gaussSin() {
        val res = GaussIntegrator.integrate(0.0..2 * PI) { x ->
            sin(x)
        }
        assertEquals(0.0, res.value!!, 1e-4)
    }

    @Test
    fun gaussUniform() {
        val res = GaussIntegrator.integrate(0.0..100.0,300) { x ->
            if(x in 30.0..50.0){
                1.0
            } else {
                0.0
            }
        }
        assertEquals(20.0, res.value!!, 0.1)
    }


}