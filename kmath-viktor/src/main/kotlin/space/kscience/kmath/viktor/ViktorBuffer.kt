/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.viktor

import org.jetbrains.bio.viktor.F64FlatArray
import space.kscience.kmath.structures.MutableBuffer

@Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")
public class ViktorBuffer(public val flatArray: F64FlatArray) : MutableBuffer<Double> {
    public override val size: Int
        get() = flatArray.size

    public override inline fun get(index: Int): Double = flatArray[index]

    public override inline fun set(index: Int, value: Double) {
        flatArray[index] = value
    }

    public override fun copy(): MutableBuffer<Double> = ViktorBuffer(flatArray.copy().flatten())
    public override operator fun iterator(): Iterator<Double> = flatArray.data.iterator()
}
