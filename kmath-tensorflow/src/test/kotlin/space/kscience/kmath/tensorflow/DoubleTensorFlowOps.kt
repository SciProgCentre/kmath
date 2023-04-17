/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensorflow

import org.junit.jupiter.api.Test
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.get
import space.kscience.kmath.nd.structureND
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.sum
import space.kscience.kmath.tensors.core.randomNormal
import kotlin.test.assertEquals

@OptIn(UnstableKMathAPI::class)
class DoubleTensorFlowOps {
    @Test
    fun basicOps() {
        val res = DoubleField.produceWithTF {
            val initial = structureND(2, 2) { 1.0 }

            initial + (initial * 2.0)
        }
        //println(StructureND.toString(res))
        assertEquals(3.0, res[0, 0])
    }

    @Test
    fun dot(){
        val dim = 1000

        val tensor1 = DoubleTensorAlgebra.randomNormal(shape = ShapeND(dim, dim), 12224)
        val tensor2 = DoubleTensorAlgebra.randomNormal(shape = ShapeND(dim, dim), 12225)

        DoubleField.produceWithTF {
            tensor1 dot tensor2
        }.sum()
    }

    @Test
    fun extensionOps(){
        val res = DoubleField.produceWithTF {
            val i = structureND(2, 2) { 0.5 }

            sin(i).pow(2) + cos(i).pow(2)
        }

        assertEquals(1.0, res[0,0],0.01)
    }


}