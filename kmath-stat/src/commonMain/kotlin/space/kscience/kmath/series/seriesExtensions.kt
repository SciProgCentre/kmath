package space.kscience.kmath.series

import space.kscience.kmath.operations.BufferAlgebra
import space.kscience.kmath.operations.ExponentialOperations
import space.kscience.kmath.operations.PowerOperations
import space.kscience.kmath.operations.TrigonometricOperations
import space.kscience.kmath.structures.Buffer


//trigonometric

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.sin(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.sin(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.cos(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.cos(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.tan(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.tan(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.asin(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.asin(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.acos(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.acos(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.atan(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : TrigonometricOperations<Buffer<T>> =
    bufferAlgebra.atan(arg).moveTo(arg.startOffset)


//exponential

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.exp(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.exp(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.ln(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.ln(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.sinh(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.sinh(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.cosh(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.cosh(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.tanh(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.tanh(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.asinh(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.asinh(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.acosh(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.acosh(arg).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.atanh(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : ExponentialOperations<Buffer<T>> =
    bufferAlgebra.atanh(arg).moveTo(arg.startOffset)


//power

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.power(
    arg: Buffer<T>,
    pow: Number,
): Series<T> where BA : BufferAlgebra<T, *>, BA : PowerOperations<Buffer<T>> =
    bufferAlgebra.power(arg, pow).moveTo(arg.startOffset)

public fun <T, BA> SeriesAlgebra<T, *, BA, *>.sqrt(
    arg: Buffer<T>,
): Series<T> where BA : BufferAlgebra<T, *>, BA : PowerOperations<Buffer<T>> =
    bufferAlgebra.sqrt(arg).moveTo(arg.startOffset)