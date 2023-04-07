/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall

public interface StructureNDOfDouble : StructureND<Double> {
    /**
     * Guaranteed non-blocking access to content
     */
    public fun getDouble(index: IntArray): Double
}

/**
 * Optimized method to access primitive without boxing if possible
 */
@OptIn(PerformancePitfall::class)
public fun StructureND<Double>.getDouble(index: IntArray): Double =
    if (this is StructureNDOfDouble) getDouble(index) else get(index)

public interface MutableStructureNDOfDouble : StructureNDOfDouble, MutableStructureND<Double> {
    /**
     * Guaranteed non-blocking access to content
     */
    public fun setDouble(index: IntArray, value: Double)
}

@OptIn(PerformancePitfall::class)
public fun MutableStructureND<Double>.getDouble(index: IntArray): Double =
    if (this is StructureNDOfDouble) getDouble(index) else get(index)


public interface StructureNDOfInt : StructureND<Int> {
    /**
     * Guaranteed non-blocking access to content
     */
    public fun getInt(index: IntArray): Int
}

@OptIn(PerformancePitfall::class)
public fun StructureND<Int>.getInt(index: IntArray): Int =
    if (this is StructureNDOfInt) getInt(index) else get(index)
