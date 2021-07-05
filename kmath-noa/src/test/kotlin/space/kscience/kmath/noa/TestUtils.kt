/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import kotlin.test.Test

class TestUtils {

    @Test
    fun throwingExceptions() {
        try {
            JNoa.testException()
        } catch(e:NoaException) {
            println("Caught NoaException in JVM\n:$e")
        }
    }

    @Test
    fun cudaSupport() {
        println("CUDA support available: ${cudaAvailable()}")
    }
}