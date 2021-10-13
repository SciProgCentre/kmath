/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class SamplerTest {

    @Test
    fun bufferSamplerTest() {
        val sampler = Sampler { it.chain { nextDouble() } }
        val data = sampler.sampleBuffer(RandomGenerator.default, 100)
        runBlocking { println(data.next()) }
    }
}