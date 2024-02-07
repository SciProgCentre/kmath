/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.testutils

import space.kscience.kmath.structures.Float64Buffer
import kotlin.jvm.JvmName

/**
 * Simplified [Float64Buffer] to array comparison
 */
public fun Float64Buffer.contentEquals(vararg doubles: Double): Boolean = array.contentEquals(doubles)

@JvmName("contentEqualsArray")
public infix fun Float64Buffer.contentEquals(otherArray: DoubleArray): Boolean = array.contentEquals(otherArray)

@JvmName("contentEqualsBuffer")
public infix fun Float64Buffer.contentEquals(otherBuffer: Float64Buffer): Boolean = array.contentEquals(otherBuffer.array)