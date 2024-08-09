/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package space.kscience.kmath.samplers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler
import space.kscience.kmath.structures.Float64
import kotlin.coroutines.coroutineContext

@UnstableKMathAPI
public data class RandomForkingSample<T>(
    val value: Deferred<T>,
    val generation: Int,
    val energy: Float64,
    val stepChain: Chain<T>
)

/**
 * A sampler that creates a chain that could be split at each computation
 */
@UnstableKMathAPI
public class RandomForkingSampler<T : Any>(
    private val scope: CoroutineScope,
    private val initialValue: suspend (RandomGenerator) -> T,
    private val makeStep: suspend RandomGenerator.(T) -> List<T>
) : Sampler<T?> {

    override fun sample(generator: RandomGenerator): Chain<T?> =
        buildChain(scope, initial = { initialValue(generator) }) { generator.makeStep(it) }

    public companion object {
        private suspend fun <T> Channel<T>.receiveEvents(
            initial: T,
            buffer: Int = 50,
            makeStep: suspend (T) -> List<T>
        ) {
            send(initial)
            //inner dispatch queue
            val innerChannel = Channel<T>(buffer)
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


        internal fun <T : Any> buildChain(
            scope: CoroutineScope,
            initial: suspend () -> T,
            makeStep: suspend (T) -> List<T>
        ): Chain<T?> {
            val channel = Channel<T>(Channel.RENDEZVOUS)
            scope.launch {
                channel.receiveEvents(initial(), makeStep = makeStep)
            }

            return object : Chain<T?> {
                override suspend fun next(): T? = channel.receiveCatching().getOrNull()

                override suspend fun fork(): Chain<T?> = buildChain(scope, { channel.receive() }, makeStep)
            }
        }


        public fun <T : Any, A> metropolisHastings(
            scope: CoroutineScope,
            algebra: A,
            startPoint: suspend (RandomGenerator) -> T,
            stepSampler: Sampler<T>,
            initialEnergy: Float64,
            energySplitRule: suspend RandomForkingSample<T>.() -> List<Float64>,
            stepScaleRule: suspend (Float64) -> Float64 = { 1.0 },
            targetPdf: suspend (T) -> Float64,
        ): RandomForkingSampler<RandomForkingSample<T>> where A : Group<T>, A : ScaleOperations<T> =
            RandomForkingSampler<RandomForkingSample<T>>(
                scope = scope,
                initialValue = { generator ->
                    RandomForkingSample<T>(
                        value = scope.async { startPoint(generator) },
                        generation = 0,
                        energy = initialEnergy,
                        stepChain = stepSampler.sample(generator)
                    )
                }
            ) { previousSample: RandomForkingSample<T> ->
                val value = previousSample.value.await()
                previousSample.energySplitRule().map { energy ->
                    RandomForkingSample<T>(
                        value = scope.async<T> {
                            val proposalPoint = with(algebra) {
                                value + previousSample.stepChain.next() * stepScaleRule(previousSample.energy)
                            }
                            val ratio = targetPdf(proposalPoint) / targetPdf(value)

                            if (ratio >= 1.0) {
                                proposalPoint
                            } else {
                                val acceptanceProbability = nextDouble()
                                if (acceptanceProbability <= ratio) {
                                    proposalPoint
                                } else {
                                    value
                                }
                            }
                        },
                        generation = previousSample.generation + 1,
                        energy = 0.0,
                        stepChain = previousSample.stepChain
                    )
                }
            }

    }
}
