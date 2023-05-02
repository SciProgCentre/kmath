/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.viktor

import org.jetbrains.bio.viktor.F64Array
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.ColumnStrides
import space.kscience.kmath.nd.MutableStructureND
import space.kscience.kmath.nd.ShapeND

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public class ViktorStructureND(public val f64Buffer: F64Array) : MutableStructureND<Double> {
    override val shape: ShapeND get() = ShapeND(f64Buffer.shape)

    @OptIn(PerformancePitfall::class)
    override inline fun get(index: IntArray): Double = f64Buffer.get(*index)

    @OptIn(PerformancePitfall::class)
    override inline fun set(index: IntArray, value: Double) {
        f64Buffer.set(*index, value = value)
    }

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, Double>> =
        ColumnStrides(shape).asSequence().map { it to get(it) }
}

public fun F64Array.asStructure(): ViktorStructureND = ViktorStructureND(this)


