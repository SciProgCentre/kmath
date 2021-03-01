package space.kscience.kmath.structures

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.RingWithNumbers
import java.util.*
import java.util.stream.IntStream

/**
 * A demonstration implementation of NDField over Real using Java [DoubleStream] for parallel execution
 */
@OptIn(UnstableKMathAPI::class)
class StreamRealNDField(
    override val shape: IntArray,
) : NDField<Double, RealField>,
    RingWithNumbers<NDStructure<Double>>,
    ExtendedField<NDStructure<Double>> {

    private val strides = DefaultStrides(shape)
    override val elementContext: RealField get() = RealField
    override val zero: NDBuffer<Double> by lazy { produce { zero } }
    override val one: NDBuffer<Double> by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Double> {
        val d = value.toDouble() // minimize conversions
        return produce { d }
    }

    private val NDStructure<Double>.buffer: RealBuffer
        get() = when {
            !shape.contentEquals(this@StreamRealNDField.shape) -> throw ShapeMismatchException(
                this@StreamRealNDField.shape,
                shape
            )
            this is NDBuffer && this.strides == this@StreamRealNDField.strides -> this.buffer as RealBuffer
            else -> RealBuffer(strides.linearSize) { offset -> get(strides.index(offset)) }
        }


    override fun produce(initializer: RealField.(IntArray) -> Double): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            val index = strides.index(offset)
            RealField.initializer(index)
        }.toArray()

        return NDBuffer(strides, array.asBuffer())
    }

    override fun NDStructure<Double>.map(
        transform: RealField.(Double) -> Double,
    ): NDBuffer<Double> {
        val array = Arrays.stream(buffer.array).parallel().map { RealField.transform(it) }.toArray()
        return NDBuffer(strides, array.asBuffer())
    }

    override fun NDStructure<Double>.mapIndexed(
        transform: RealField.(index: IntArray, Double) -> Double,
    ): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            RealField.transform(
                strides.index(offset),
                buffer.array[offset]
            )
        }.toArray()

        return NDBuffer(strides, array.asBuffer())
    }

    override fun combine(
        a: NDStructure<Double>,
        b: NDStructure<Double>,
        transform: RealField.(Double, Double) -> Double,
    ): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            RealField.transform(a.buffer.array[offset], b.buffer.array[offset])
        }.toArray()
        return NDBuffer(strides, array.asBuffer())
    }

    override fun power(arg: NDStructure<Double>, pow: Number): NDBuffer<Double> = arg.map() { power(it, pow) }

    override fun exp(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { exp(it) }

    override fun ln(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { ln(it) }

    override fun sin(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { sin(it) }
    override fun cos(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { cos(it) }
    override fun tan(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { tan(it) }
    override fun asin(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { asin(it) }
    override fun acos(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { acos(it) }
    override fun atan(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { atan(it) }

    override fun sinh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { sinh(it) }
    override fun cosh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { cosh(it) }
    override fun tanh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { tanh(it) }
    override fun asinh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { asinh(it) }
    override fun acosh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { acosh(it) }
    override fun atanh(arg: NDStructure<Double>): NDBuffer<Double> = arg.map() { atanh(it) }
}

fun NDAlgebra.Companion.realWithStream(vararg shape: Int): StreamRealNDField = StreamRealNDField(shape)