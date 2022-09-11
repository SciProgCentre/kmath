/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.testutils

import space.kscience.kmath.structures.DoubleBuffer
import kotlin.jvm.JvmName

/**
 * Simplified [DoubleBuffer] to array comparison
 */
public fun DoubleBuffer.contentEquals(vararg doubles: Double): Boolean = array.contentEquals(doubles)

@JvmName("contentEqualsArray")
public infix fun DoubleBuffer.contentEquals(otherArray: DoubleArray): Boolean = array.contentEquals(otherArray)

@JvmName("contentEqualsBuffer")
public infix fun DoubleBuffer.contentEquals(otherBuffer: DoubleBuffer): Boolean = array.contentEquals(otherBuffer.array)