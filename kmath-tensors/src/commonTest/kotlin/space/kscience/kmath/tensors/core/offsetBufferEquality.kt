/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Float64Buffer
import space.kscience.kmath.structures.indices
import kotlin.jvm.JvmName


/**
 * Simplified [Float64Buffer] to array comparison
 */
public fun OffsetDoubleBuffer.contentEquals(vararg doubles: Double): Boolean = indices.all { get(it) == doubles[it] }

@JvmName("contentEqualsArray")
public infix fun OffsetDoubleBuffer.contentEquals(otherArray: DoubleArray): Boolean = contentEquals(*otherArray)

@JvmName("contentEqualsBuffer")
public infix fun OffsetDoubleBuffer.contentEquals(otherBuffer: Buffer<Float64>): Boolean =
    indices.all { get(it) == otherBuffer[it] }