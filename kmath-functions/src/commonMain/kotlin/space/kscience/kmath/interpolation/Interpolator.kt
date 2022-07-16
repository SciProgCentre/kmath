/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.interpolation

import space.kscience.kmath.data.XYColumnarData
import space.kscience.kmath.functions.PiecewisePolynomial
import space.kscience.kmath.functions.asFunction
import space.kscience.kmath.functions.value
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer

/**
 * And interpolator for data with x column type [X], y column type [Y].
 */
public fun interface Interpolator<T, in X : T, Y : T> {
    public fun interpolate(points: XYColumnarData<T, X, Y>): (X) -> Y
}

/**
 * And interpolator returning [PiecewisePolynomial] function
 */
public interface PolynomialInterpolator<T : Comparable<T>> : Interpolator<T, T, T> {
    public val algebra: Ring<T>

    public fun getDefaultValue(): T = error("Out of bounds")

    public fun interpolatePolynomials(points: XYColumnarData<T, T, T>): PiecewisePolynomial<T>

    override fun interpolate(points: XYColumnarData<T, T, T>): (T) -> T = { x ->
        interpolatePolynomials(points).value(algebra, x) ?: getDefaultValue()
    }
}


public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    x: Buffer<T>,
    y: Buffer<T>,
): PiecewisePolynomial<T> {
    val pointSet = XYColumnarData.of(x, y)
    return interpolatePolynomials(pointSet)
}

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    data: Map<T, T>,
): PiecewisePolynomial<T> {
    val pointSet = XYColumnarData.of(data.keys.toList().asBuffer(), data.values.toList().asBuffer())
    return interpolatePolynomials(pointSet)
}

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolatePolynomials(
    data: List<Pair<T, T>>,
): PiecewisePolynomial<T> {
    val pointSet = XYColumnarData.of(data.map { it.first }.asBuffer(), data.map { it.second }.asBuffer())
    return interpolatePolynomials(pointSet)
}

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolate(
    x: Buffer<T>,
    y: Buffer<T>,
): (T) -> T? = interpolatePolynomials(x, y).asFunction(algebra)

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolate(
    data: Map<T, T>,
): (T) -> T? = interpolatePolynomials(data).asFunction(algebra)

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolate(
    data: List<Pair<T, T>>,
): (T) -> T? = interpolatePolynomials(data).asFunction(algebra)


public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolate(
    x: Buffer<T>,
    y: Buffer<T>,
    defaultValue: T,
): (T) -> T = interpolatePolynomials(x, y).asFunction(algebra, defaultValue)

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolate(
    data: Map<T, T>,
    defaultValue: T,
): (T) -> T = interpolatePolynomials(data).asFunction(algebra, defaultValue)

public fun <T : Comparable<T>> PolynomialInterpolator<T>.interpolate(
    data: List<Pair<T, T>>,
    defaultValue: T,
): (T) -> T = interpolatePolynomials(data).asFunction(algebra, defaultValue)