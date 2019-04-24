package scientifik.kmath.transform

import org.apache.commons.math3.transform.*
import scientifik.kmath.operations.Complex
import scientifik.kmath.streaming.Processor
import scientifik.kmath.streaming.Producer
import scientifik.kmath.streaming.map
import scientifik.kmath.structures.*


/**
 *
 */
object Transformations {

    private fun Buffer<Complex>.toArray(): Array<org.apache.commons.math3.complex.Complex> =
        Array(size) { org.apache.commons.math3.complex.Complex(get(it).re, get(it).im) }

    private fun Buffer<Double>.asArray() = if (this is DoubleBuffer) {
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
    ): BufferTransform<Complex, Complex> = {
        FastFourierTransformer(normalization).transform(it.toArray(), direction).asBuffer()
    }

    fun realFourier(
        normalization: DftNormalization = DftNormalization.STANDARD,
        direction: TransformType = TransformType.FORWARD
    ): BufferTransform<Double, Complex> = {
        FastFourierTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    fun sine(
        normalization: DstNormalization = DstNormalization.STANDARD_DST_I,
        direction: TransformType = TransformType.FORWARD
    ): BufferTransform<Double, Double> = {
        FastSineTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    fun cosine(
        normalization: DctNormalization = DctNormalization.STANDARD_DCT_I,
        direction: TransformType = TransformType.FORWARD
    ): BufferTransform<Double, Double> = {
        FastCosineTransformer(normalization).transform(it.asArray(), direction).asBuffer()
    }

    fun hadamard(
        direction: TransformType = TransformType.FORWARD
    ): BufferTransform<Double, Double> = {
        FastHadamardTransformer().transform(it.asArray(), direction).asBuffer()
    }
}

/**
 * Process given [Producer] with commons-math fft transformation
 */
fun Producer<Buffer<Complex>>.FFT(
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD
): Processor<Buffer<Complex>, Buffer<Complex>> {
    val transform = Transformations.fourier(normalization, direction)
    return map { transform(it) }
}

@JvmName("realFFT")
fun Producer<Buffer<Double>>.FFT(
    normalization: DftNormalization = DftNormalization.STANDARD,
    direction: TransformType = TransformType.FORWARD
): Processor<Buffer<Double>, Buffer<Complex>> {
    val transform = Transformations.realFourier(normalization, direction)
    return map { transform(it) }
}