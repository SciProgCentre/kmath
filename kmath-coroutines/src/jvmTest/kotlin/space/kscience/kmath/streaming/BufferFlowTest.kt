/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.streaming

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import org.junit.jupiter.api.Timeout
import space.kscience.kmath.coroutines.async
import space.kscience.kmath.coroutines.collect
import space.kscience.kmath.coroutines.mapParallel
import java.util.concurrent.Executors
import kotlin.test.Test


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@FlowPreview
internal class BufferFlowTest {
    val dispatcher: CoroutineDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    @Test
    @Timeout(2000)
    fun map() {
        runBlocking {
            (1..20).asFlow().mapParallel(dispatcher) {
                println("Started $it on ${Thread.currentThread().name}")
                @Suppress("BlockingMethodInNonBlockingContext")
                Thread.sleep(200)
                it
            }.collect {
                println("Completed $it on ${Thread.currentThread().name}")
            }
        }
    }

    @Test
    @Timeout(2000)
    fun async() {
        runBlocking {
            (1..20).asFlow().async(dispatcher) {
                println("Started $it on ${Thread.currentThread().name}")
                @Suppress("BlockingMethodInNonBlockingContext")
                Thread.sleep(200)
                it
            }.collect(4) {
                println("Completed $it on ${Thread.currentThread().name}")
            }
        }
    }

}