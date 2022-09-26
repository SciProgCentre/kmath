/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.commons.transform

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.apache.commons.math3.transform.*
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.operations.BufferTransform
import space.kscience.kmath.streaming.chunked
import space.kscience.kmath.streaming.spread
import space.kscience.kmath.structures.*

/**
 * Streaming and buffer transformations with Commons-math algorithms
 */
public object Transformations {
    private fun Buffer<Complex>.toCmComplexArray(): Array<org.apache.commons.math3.complex.Complex> =
        Array(size) { org.apache.commons.math3.complex.Complex(get(it).re, get(it).im) }

    /**
     * Create a virtual buffer on top of array
     */
    private fun Array<org.apache.commons.math3.complex.Complex>.asBuffer() = VirtualBuffer(size) {
        val value = get(it)
        Complex(value.real, value.imaginary)
    }

    public fun fourier(
        normalization: DftNormalization = DftNormalization.STANDARD,
        direction: TransformType = TransformType.FORWARD,
    ): BufferTransform<Complex, Complex> = BufferTransform {
        FastFourierTransformer(normalization).transform(it.toCmComplexArray(), direction).asBuffer()
    }

    public fun realFourier(
        normalization: DftNormalization = DftNormalization.STANDARD,
        direction: TransformType = TransformType.FORWARD,
    ): BufferTransform<Double, Complex> = BufferTransform {
        FastFourierTransformer(normalization).transform(it.toDoubleArray(), direction).asBuffer()
    }

    public fun sine(
        normalization: DstNormalization = DstNormalization.STANDARD_DST_I,
        direction: TransformType = TransformType.FORWARD,
    ): BufferTransform<Double, Double> = DoubleBufferTransform {
        FastSineTransformer(normalization).transform(it.array, direction).asBuffer()
    }

    public fun cosine(
        normalization: DctNormalization = DctNormalization.STANDARD_DCT_I,
        direction: TransformType = TransformType.FORWARD,
    ): BufferTransform<Double, Double> = BufferTransform {
        FastCosineTransformer(normalization).transform(it.toDoubleArray(), direction).asBuffer()
    }

    public fun hadamard(
        direction: TransformType = TransformType.FORWARD,
    ): BufferTransform<Double, Double> = DoubleBufferTransform {
        FastHadamardTransformer().transform(it.array, direction).asBuffer()
    }
}

/**
 * Process given [Flow] with commons-math fft transformation
 */
public fun Flow<Buffer<Complex>>.fft(
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD,
): Flow<Buffer<Complex>> {
    val transform = Transformations.fourier(normalization, direction)
    return map(transform::transform)
}

@JvmName("realFFT")
public fun Flow<Buffer<Double>>.fft(
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD,
): Flow<Buffer<Complex>> {
    val transform = Transformations.realFourier(normalization, direction)
    return map(transform::transform)
}

/**
 * Process a continuous flow of real numbers in FFT splitting it in chunks of [bufferSize].
 */
@JvmName("realFFT")
public fun Flow<Double>.fft(
    bufferSize: Int = Int.MAX_VALUE,
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD,
): Flow<Complex> = chunked(bufferSize).fft(normalization, direction).spread()

/**
 * Map a complex flow into real flow by taking real part of each number
 */
@FlowPreview
public fun Flow<Complex>.real(): Flow<Double> = map { it.re }
