/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package space.kscience.kmath.samplers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler
import kotlin.coroutines.coroutineContext


/**
 * A sampler that creates a chain that could be split at each computation
 */
public class RandomForkingSampler<T: Any>(
    private val scope: CoroutineScope,
    private val initialValue: T,
    private val makeStep: suspend RandomGenerator.(T) -> List<T>
) : Sampler<T?> {

    override fun sample(generator: RandomGenerator): Chain<T?> = buildChain(scope, initialValue) { generator.makeStep(it) }

    public companion object {
        private suspend fun <T> Channel<T>.receiveEvents(
            initial: T,
            makeStep: suspend (T) -> List<T>
        ) {
            send(initial)
            //inner dispatch queue
            val innerChannel = Channel<T>(50)
            innerChannel.send(initial)
            while (coroutineContext.isActive && !innerChannel.isEmpty) {
                val current = innerChannel.receive()
                //add event immediately, but it does not mean that the value is computed immediately as well
                makeStep(current).forEach {
                    innerChannel.send(it)
                    send(it)
                }
            }
            innerChannel.close()
            close()
        }


        public fun <T: Any> buildChain(
            scope: CoroutineScope,
            initial: T,
            makeStep: suspend (T) -> List<T>
        ): Chain<T?> {
            val channel = Channel<T>(Channel.RENDEZVOUS)
            scope.launch {
                channel.receiveEvents(initial, makeStep)
            }

            return object : Chain<T?> {
                override suspend fun next(): T? = channel.receiveCatching().getOrNull()

                override suspend fun fork(): Chain<T?> = buildChain(scope, channel.receive(), makeStep)
            }
        }
    }
}
