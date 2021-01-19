package kscience.kmath.torch

import kotlin.test.*


class TestUtils {

    @Test
    fun testJTorch() {
        val tensor = JTorch.fullInt(54, intArrayOf(3), 0)
        println(JTorch.tensorToString(tensor))
        JTorch.disposeTensor(tensor)
    }

}