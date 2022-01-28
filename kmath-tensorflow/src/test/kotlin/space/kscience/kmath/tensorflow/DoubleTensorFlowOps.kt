package space.kscience.kmath.tensorflow

import org.junit.jupiter.api.Test
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.structureND
import space.kscience.kmath.operations.DoubleField

class DoubleTensorFlowOps {
    @Test
    fun basicOps() {
        val res = DoubleField.produceWithTF {
            val initial = structureND(2, 2) { 1.0 }

            initial + (initial * 2.0)
        }
        println(StructureND.toString(res))
    }

}