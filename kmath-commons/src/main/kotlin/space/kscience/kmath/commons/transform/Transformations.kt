/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.commons.transform

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.apache.commons.math3.transform.*
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.streaming.chunked
import space.kscience.kmath.streaming.spread
import space.kscience.kmath.structures.*


/**
 * Streaming and buffer transformations
 */
public object Transformations {
    private fun Buffer<Complex<Double>>.toArray(): Array<org.apache.commons.math3.complex.Complex> =
        Array(size) { org.apache.commons.math3.complex.Complex(get(it).re, get(it).im) }

    private fun Buffer<Double>.asArray() = if (this is DoubleBuffer) {
        array
    } else {
        DoubleArray(size) { i -> get(i) }
    }

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
    ): SuspendBufferTransform<Complex<Double>, Complex<Double>> = {
        FastFourierTransformer(normalization).transform(it.toArray(), direction).asBuffer()
    }

    public fun realFourier(
        normalization: DftNormalization = DftNormalization.STANDARD,
        direction: TransformType = TransformType.FORWARD,
    ): SuspendBufferTransform<Double, Complex<Double>> = {
        FastFourierTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    public fun sine(
        normalization: DstNormalization = DstNormalization.STANDARD_DST_I,
        direction: TransformType = TransformType.FORWARD,
    ): SuspendBufferTransform<Double, Double> = {
        FastSineTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    public fun cosine(
        normalization: DctNormalization = DctNormalization.STANDARD_DCT_I,
        direction: TransformType = TransformType.FORWARD,
    ): SuspendBufferTransform<Double, Double> = {
        FastCosineTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    public fun hadamard(
        direction: TransformType = TransformType.FORWARD,
    ): SuspendBufferTransform<Double, Double> = {
        FastHadamardTransformer().transform(it.asArray(), direction).asBuffer()
    }
}

/**
 * Process given [Flow] with commons-math fft transformation
 */
@FlowPreview
public fun Flow<Buffer<Complex<Double>>>.FFT(
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD,
): Flow<Buffer<Complex<Double>>> {
    val transform = Transformations.fourier(normalization, direction)
    return map { transform(it) }
}

@FlowPreview
@JvmName("realFFT")
public fun Flow<Buffer<Double>>.FFT(
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD,
): Flow<Buffer<Complex<Double>>> {
    val transform = Transformations.realFourier(normalization, direction)
    return map(transform)
}

/**
 * Process a continuous flow of real numbers in FFT splitting it in chunks of [bufferSize].
 */
@FlowPreview
@JvmName("realFFT")
public fun Flow<Double>.FFT(
    bufferSize: Int = Int.MAX_VALUE,
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD,
): Flow<Complex<Double>> = chunked(bufferSize).FFT(normalization, direction).spread()

/**
 * Map a complex flow into real flow by taking real part of each number
 */
@FlowPreview
public fun Flow<Complex<Double>>.real(): Flow<Double> = map { it.re }
