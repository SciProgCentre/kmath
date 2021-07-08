/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import kotlin.test.Test
import kotlin.test.assertEquals

class TestUtils {

    @Test
    fun throwingExceptions() {
        val i = try {
            JNoa.testException(5)
        } catch (e: NoaException) {
            10
        }
        assertEquals(i, 10)
    }

    @Test
    fun settingNumThreads(){
        val numThreads = 2
        setNumThreads(numThreads)
        assertEquals(numThreads, getNumThreads())
    }


}