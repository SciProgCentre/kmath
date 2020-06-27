package scientifik.kmath.commons.transform

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.apache.commons.math3.transform.*
import scientifik.kmath.operations.Complex
import scientifik.kmath.streaming.chunked
import scientifik.kmath.streaming.spread
import scientifik.kmath.structures.*


/**
 * Streaming and buffer transformations
 */
object Transformations {

    private fun Buffer<Complex>.toArray(): Array<org.apache.commons.math3.complex.Complex> =
        Array(size) { org.apache.commons.math3.complex.Complex(get(it).re, get(it).im) }

    private fun Buffer<Double>.asArray() = if (this is RealBuffer) {
        array
    } else {
        DoubleArray(size) { i -> get(i) }
    }

    /**
     * Create a virtual buffer on top of array
     */
    private fun Array<org.apache.commons.math3.complex.Complex>.asBuffer() = VirtualBuffer<Complex>(size) {
        val value = get(it)
        Complex(value.real, value.imaginary)
    }

    fun fourier(
        normalization: DftNormalization = DftNormalization.STANDARD,
        direction: TransformType = TransformType.FORWARD
    ): SuspendBufferTransform<Complex, Complex> = {
        FastFourierTransformer(normalization).transform(it.toArray(), direction).asBuffer()
    }

    fun realFourier(
        normalization: DftNormalization = DftNormalization.STANDARD,
        direction: TransformType = TransformType.FORWARD
    ): SuspendBufferTransform<Double, Complex> = {
        FastFourierTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    fun sine(
        normalization: DstNormalization = DstNormalization.STANDARD_DST_I,
        direction: TransformType = TransformType.FORWARD
    ): SuspendBufferTransform<Double, Double> = {
        FastSineTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    fun cosine(
        normalization: DctNormalization = DctNormalization.STANDARD_DCT_I,
        direction: TransformType = TransformType.FORWARD
    ): SuspendBufferTransform<Double, Double> = {
        FastCosineTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    fun hadamard(
        direction: TransformType = TransformType.FORWARD
    ): SuspendBufferTransform<Double, Double> = {
        FastHadamardTransformer().transform(it.asArray(), direction).asBuffer()
    }
}

/**
 * Process given [Flow] with commons-math fft transformation
 */
@FlowPreview
fun Flow<Buffer<Complex>>.FFT(
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD
): Flow<Buffer<Complex>> {
    val transform = Transformations.fourier(normalization, direction)
    return map { transform(it) }
}

@FlowPreview
@JvmName("realFFT")
fun Flow<Buffer<Double>>.FFT(
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD
): Flow<Buffer<Complex>> {
    val transform = Transformations.realFourier(normalization, direction)
    return map(transform)
}

/**
 * Process a continous flow of real numbers in FFT splitting it in chunks of [bufferSize].
 */
@FlowPreview
@JvmName("realFFT")
fun Flow<Double>.FFT(
    bufferSize: Int = Int.MAX_VALUE,
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD
): Flow<Complex> {
    return chunked(bufferSize).FFT(normalization,direction).spread()
}

/**
 * Map a complex flow into real flow by taking real part of each number
 */
@FlowPreview
fun Flow<Complex>.real(): Flow<Double> = map{it.re}