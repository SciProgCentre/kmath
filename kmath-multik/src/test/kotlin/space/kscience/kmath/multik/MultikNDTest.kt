package space.kscience.kmath.multik

import org.junit.jupiter.api.Test
import space.kscience.kmath.nd.one
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke

internal class MultikNDTest {
    @Test
    fun basicAlgebra(): Unit = DoubleField.multikND{
        one(2,2) + 1.0
    }
}