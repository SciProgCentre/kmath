/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import kotlin.test.Test

class TestUtils {

    @Test
    fun testExceptions() {
        try {
            println(cudaAvailable())
        } catch(e:NoaException) {
            println(e)
            println("ALL GOOD")
        }
    }
}