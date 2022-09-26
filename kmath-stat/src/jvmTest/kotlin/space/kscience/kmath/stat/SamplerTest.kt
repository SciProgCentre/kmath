/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.random.chain
import kotlin.test.Test

class SamplerTest {

    @Test
    fun bufferSamplerTest() {
        val sampler = Sampler { it.chain { nextDouble() } }
        val data = sampler.sampleBuffer(RandomGenerator.default, 100)
        runBlocking { println(data.next()) }
    }
}