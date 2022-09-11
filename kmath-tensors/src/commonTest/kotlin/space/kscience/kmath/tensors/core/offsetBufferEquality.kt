/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.indices
import kotlin.jvm.JvmName


/**
 * Simplified [DoubleBuffer] to array comparison
 */
public fun OffsetDoubleBuffer.contentEquals(vararg doubles: Double): Boolean = indices.all { get(it) == doubles[it] }

@JvmName("contentEqualsArray")
public infix fun OffsetDoubleBuffer.contentEquals(otherArray: DoubleArray): Boolean = contentEquals(*otherArray)

@JvmName("contentEqualsBuffer")
public infix fun OffsetDoubleBuffer.contentEquals(otherBuffer: Buffer<Double>): Boolean =
    indices.all { get(it) == otherBuffer[it] }