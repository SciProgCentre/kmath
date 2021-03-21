package space.kscience.kmath.commons.integration

import org.junit.jupiter.api.Test
import space.kscience.kmath.integration.integrate
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField.sin
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.assertTrue

@UnstableKMathAPI
internal class IntegrationTest {
    private val function: (Double) -> Double = { sin(it) }

    @Test
    fun simpson() {
        val res = CMIntegrator.simpson().integrate(0.0..2 * PI, function)
        assertTrue { abs(res) < 1e-3 }
    }

    @Test
    fun customSimpson() {
        val res = CMIntegrator.simpson().integrate(0.0..PI, function) {
            targetRelativeAccuracy = 1e-4
            targetAbsoluteAccuracy = 1e-4
        }
        assertTrue { abs(res - 2) < 1e-3 }
        assertTrue { abs(res - 2) > 1e-12 }
    }
}