/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer


//trigonometric

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.sin(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.sin(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.cos(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.cos(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.tan(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.tan(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.asin(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.asin(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.acos(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.acos(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.atan(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.atan(arg.origin).asSeries(arg.position)


//exponential

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.exp(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.exp(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.ln(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.ln(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.sinh(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.sinh(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.cosh(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.cosh(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.tanh(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.tanh(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.asinh(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.asinh(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.acosh(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.acosh(arg.origin).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.atanh(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.atanh(arg.origin).asSeries(arg.position)


//power

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.power(
    arg: Series<T>,
    pow: Number,
): Series<T> where BA : BufferAlgebra<T, *>, BA : PowerOperations<Buffer<T>> =
    bufferAlgebra.power(arg.origin, pow).asSeries(arg.position)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.sqrt(
    arg: Series<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : PowerOperations<Buffer<T>> =
    bufferAlgebra.sqrt(arg.origin).asSeries(arg.position)

// reduction

public fun <T> SeriesAlgebra<T, *, *, *>.sum(arg: Series<T>): T = with(elementAlgebra){
    arg.origin.fold(zero) { acc, value -> acc + value }
}
